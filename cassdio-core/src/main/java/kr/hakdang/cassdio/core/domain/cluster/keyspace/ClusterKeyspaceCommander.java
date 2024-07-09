package kr.hakdang.cassdio.core.domain.cluster.keyspace;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.Version;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.driver.internal.core.metadata.schema.queries.KeyspaceFilter;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.ClusterUtils;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.CassandraSystemTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;

/**
 * ClusterKeyspaceDescribeCommander
 *
 * @author akageun
 * @since 2024-06-30
 */
@Slf4j
@Service
public class ClusterKeyspaceCommander extends BaseClusterCommander {

    /**
     * All Keyspace name
     * - system
     *
     * @param session
     * @return
     */
    public List<KeyspaceResult> allKeyspaceList(CqlSession session) {
        List<KeyspaceResult> result = new ArrayList<>();

        SimpleStatement generalSimpleStatement = makeKeyspaceListSelect(false);

        ResultSet resultSet = session.execute(generalSimpleStatement);
        Iterator<Row> iter = resultSet.iterator();

        KeyspaceFilter keyspaceFilter = ClusterUtils.makeKeyspaceFilter(session.getContext());

        while (0 < resultSet.getAvailableWithoutFetching()) {
            Row tempRow = iter.next();
            result.add(KeyspaceResult.make(tempRow, keyspaceFilter));
        }

        if (ClusterUtils.cassandraVersion(session).compareTo(Version.V4_0_0) >= 0) {
            SimpleStatement virtualSimpleStatement = makeKeyspaceListSelect(true);

            ResultSet resultSet2 = session.execute(virtualSimpleStatement);
            Iterator<Row> iter2 = resultSet2.iterator();

            while (0 < resultSet2.getAvailableWithoutFetching()) {
                Row tempRow = iter2.next();
                result.add(KeyspaceResult.make(tempRow, keyspaceFilter));
            }
        }

        return result;
    }

    private SimpleStatement makeKeyspaceListSelect(
        boolean isVirtual
    ) {
        Select select;
        if (isVirtual) {
            select = QueryBuilder
                .selectFrom(
                    CassandraSystemKeyspace.SYSTEM_VIRTUAL_SCHEMA.getKeyspaceName(),
                    CassandraSystemTable.SYSTEM_SCHEMA_KEYSPACES.getTableName()
                )
                .column("keyspace_name")
                .column("replication")
                .column("durable_writes")
            //.all()
            ;
        } else {
            select = QueryBuilder
                .selectFrom(
                    CassandraSystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName(),
                    CassandraSystemTable.SYSTEM_SCHEMA_KEYSPACES.getTableName()
                )
                .column("keyspace_name")
                .column("replication")
                .column("durable_writes")
            //.all()
            ;
        }

        return select.build()
            .setTimeout(Duration.ofSeconds(3));
    }

    public ClusterKeyspaceListResult generalKeyspaceList(CqlSession session) {
        List<KeyspaceResult> keyspaceList = new ArrayList<>();
        for (Map.Entry<CqlIdentifier, KeyspaceMetadata> entry : session.getMetadata().getKeyspaces().entrySet()) {
            keyspaceList.add(
                KeyspaceResult.builder()
                    .keyspaceName(entry.getKey().asCql(true))
                    .durableWrites(entry.getValue().isDurableWrites())
                    .replication(entry.getValue().getReplication())
                    .build()
            );
        }

        return ClusterKeyspaceListResult.builder()
            .wasApplied(true)
            .keyspaceList(keyspaceList)
            .build();
    }

    public String describe(CqlSession session, ClusterKeyspaceDescribeArgs args) {
        if (ClusterUtils.isSystemKeyspace(session.getContext(), args.getKeyspace())) { //System 테이블은 제공 안함.
            return "";
        }

        return session.getMetadata()
            .getKeyspace(args.getKeyspace())
            .orElseThrow() //TODO : 에러처리
            .describe(true);
    }

}
