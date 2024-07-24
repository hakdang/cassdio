package kr.hakdang.cassdio.core.domain.cluster.keyspace;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.Version;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.select.SelectFrom;
import com.datastax.oss.driver.internal.core.metadata.schema.queries.KeyspaceFilter;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.ClusterUtils;
import kr.hakdang.cassdio.core.domain.cluster.CqlSessionSelectResult;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.CassandraSystemTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
    public List<KeyspaceDTO.KeyspaceNameResult> allKeyspaceNameList(CqlSession session) {
        List<KeyspaceDTO.KeyspaceNameResult> result = new ArrayList<>();

        SimpleStatement generalSimpleStatement = makeKeyspaceListSelect(false);

        ResultSet resultSet = session.execute(generalSimpleStatement);
        Iterator<Row> iter = resultSet.iterator();

        KeyspaceFilter keyspaceFilter = ClusterUtils.makeKeyspaceFilter(session.getContext());

        while (0 < resultSet.getAvailableWithoutFetching()) {
            Row tempRow = iter.next();
            result.add(KeyspaceDTO.KeyspaceNameResult.make(tempRow, keyspaceFilter));
        }

        if (ClusterUtils.cassandraVersion(session).compareTo(Version.V4_0_0) >= 0) {
            SimpleStatement virtualSimpleStatement = makeKeyspaceListSelect(true);

            ResultSet resultSet2 = session.execute(virtualSimpleStatement);
            Iterator<Row> iter2 = resultSet2.iterator();

            while (0 < resultSet2.getAvailableWithoutFetching()) {
                Row tempRow = iter2.next();
                result.add(KeyspaceDTO.KeyspaceNameResult.make(tempRow, keyspaceFilter));
            }
        }

        return result;
    }

    private SelectFrom makeKeyspaceSelectFrom(boolean virtual) {
        if (virtual) {
            return QueryBuilder
                .selectFrom(
                    CassandraSystemKeyspace.SYSTEM_VIRTUAL_SCHEMA.getKeyspaceName(),
                    CassandraSystemTable.SYSTEM_SCHEMA_KEYSPACES.getTableName()
                );
        } else {
            return QueryBuilder
                .selectFrom(
                    CassandraSystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName(),
                    CassandraSystemTable.SYSTEM_SCHEMA_KEYSPACES.getTableName()
                );
        }
    }

    private SimpleStatement makeKeyspaceListSelect(boolean virtual) {
        return makeKeyspaceSelectFrom(virtual)
            .column("keyspace_name")
            .build()
            .setTimeout(Duration.ofSeconds(3));
    }

    public KeyspaceDTO.ClusterKeyspaceListResult generalKeyspaceList(CqlSession session) {
        List<KeyspaceResult> keyspaceList = new ArrayList<>();
        for (Map.Entry<CqlIdentifier, KeyspaceMetadata> entry : session.getMetadata().getKeyspaces().entrySet()) {
            String keyspaceName = entry.getKey().asCql(true);

            keyspaceList.add(
                KeyspaceResult.builder()
                    .keyspaceName(keyspaceName)
                    .durableWrites(entry.getValue().isDurableWrites())
                    .replication(entry.getValue().getReplication())
                    .systemKeyspace(ClusterUtils.isSystemKeyspace(session.getContext(), keyspaceName))
                    .build()
            );
        }

        return KeyspaceDTO.ClusterKeyspaceListResult.builder()
            .wasApplied(true)
            .keyspaceList(keyspaceList)
            .build();
    }

    public String describe(CqlSession session, KeyspaceDTO.ClusterKeyspaceDescribeArgs args) {
        if (ClusterUtils.isSystemKeyspace(session.getContext(), args.getKeyspace())) { //System 테이블은 제공 안함.
            return "";
        }

        try {
            return session.getMetadata()
                .getKeyspace(args.getKeyspace())
                .orElseThrow(() -> new ClusterKeyspaceException.ClusterKeyspaceNotFoundException(String.format("not found keyspace (%s)", args.getKeyspace())))
                .describe(true);

        } catch (NoSuchElementException e) { //ignore
            return "";
        }
    }

    public CqlSessionSelectResult keyspaceDetail(CqlSession session, String keyspace) {
        if (ClusterUtils.isVirtualKeyspace(session.getContext(), keyspace)) { //system table 에 keyspace 만 존재함
            return CqlSessionSelectResult.empty();
        }

        SimpleStatement statement = makeKeyspaceSelectFrom(false)
            .all()
            .whereColumn("keyspace_name")
            .isEqualTo(bindMarker())
            .limit(1)
            .build()
            .setTimeout(Duration.ofSeconds(3));

        PreparedStatement preparedStatement = session.prepare(statement);

        ResultSet resultSet = session.execute(preparedStatement.bind(keyspace));
        CodecRegistry codecRegistry = session.getContext().getCodecRegistry();
        ColumnDefinitions definitions = resultSet.getColumnDefinitions();

        Row row = resultSet.one();
        if (row == null) {
            throw new IllegalArgumentException(String.format("not found keyspace(%s)", keyspace));
        }

        Map<String, Object> rowResult = new HashMap<>(ClusterUtils.convertMap(codecRegistry, definitions, row));

        return CqlSessionSelectResult.builder()
            .row(rowResult)
            .rowHeader(CassdioColumnDefinition.makes(definitions))
            .build();
    }

    public void keyspaceDrop(CqlSession session, String keyspace) {
        ResultSet resultSet = session.execute(SchemaBuilder.dropKeyspace(keyspace).build());
        log.info("Keyspace Drop Result - keyspace: {}, ok: {}", keyspace, resultSet.wasApplied());
    }
}
