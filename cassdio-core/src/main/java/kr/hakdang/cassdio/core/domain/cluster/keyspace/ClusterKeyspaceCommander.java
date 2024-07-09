package kr.hakdang.cassdio.core.domain.cluster.keyspace;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.ProtocolVersion;
import com.datastax.oss.driver.api.core.Version;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.Node;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.internal.core.channel.DriverChannel;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.ClusterUtils;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.CassandraSystemTable;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column.CassandraSystemTablesColumn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;
import static java.util.Collections.emptyMap;

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
    public List<String> allKeyspaceNames(CqlSession session) {
        SimpleStatement statement = QueryBuilder
            .selectFrom(CassandraSystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_KEYSPACES.getTableName())
            .all()
            .build()
            .setTimeout(Duration.ofSeconds(3));

        ResultSet resultSet = session.execute(statement);
        Iterator<Row> page1Iter = resultSet.iterator();
        ColumnDefinitions definitions = resultSet.getColumnDefinitions();
        CodecRegistry codecRegistry = session.getContext().getCodecRegistry();

        List<Map<String, Object>> rows = new ArrayList<>();
        while (0 < resultSet.getAvailableWithoutFetching()) {
            Row tempRow = page1Iter.next();
            rows.add(ClusterUtils.convertMap(codecRegistry, definitions, tempRow));
        }

        List<String> allKeyspace = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            allKeyspace.add(String.valueOf(row.get("keyspace_name")));
        }

        //TODO 진짜 붙는 곳 node 정보 가져와야함.
        Node node = session.getMetadata().getNodes().entrySet().stream().findFirst().orElseThrow().getValue();
        if (node.getCassandraVersion().compareTo(Version.V4_0_0) >= 0) {
            SimpleStatement statement2 = QueryBuilder
                .selectFrom(CassandraSystemKeyspace.SYSTEM_VIRTUAL_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_KEYSPACES.getTableName())
                .all()
                .build()
                .setTimeout(Duration.ofSeconds(3));

            ResultSet resultSet2 = session.execute(statement2);
            Iterator<Row> page2Iter = resultSet2.iterator();
            ColumnDefinitions definitions2 = resultSet2.getColumnDefinitions();
            CodecRegistry codecRegistry2 = session.getContext().getCodecRegistry();

            List<Map<String, Object>> rows2 = new ArrayList<>();
            while (0 < resultSet.getAvailableWithoutFetching()) {
                Row tempRow = page2Iter.next();
                rows2.add(ClusterUtils.convertMap(codecRegistry2, definitions2, tempRow));
            }

            for (Map<String, Object> row : rows) {
                allKeyspace.add(String.valueOf(row.get("keyspace_name")));
            }
        }

        return allKeyspace;
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

        return session.getMetadata().getKeyspace(args.getKeyspace()).orElseThrow()
            .describe(true);
    }

}
