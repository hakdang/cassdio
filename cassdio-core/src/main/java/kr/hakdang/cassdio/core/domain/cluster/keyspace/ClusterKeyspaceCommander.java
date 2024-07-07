package kr.hakdang.cassdio.core.domain.cluster.keyspace;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.ClusterUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public List<String> allKeyspaceNames(CqlSession session, boolean includeSystemKeyspace) {
        return session.execute(SimpleStatement.newInstance("DESC KEYSPACES"))
            .all()
            .stream()
            .map(info -> info.getString("keyspace_name"))
            .filter(keyspaceName -> includeSystemKeyspace || !CassandraSystemKeyspace.isSystemKeyspace(keyspaceName))
            .toList();
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

    public Map<String, Object> describe(CqlSession session, ClusterKeyspaceDescribeArgs args) {
        SimpleStatement statement = SimpleStatement.newInstance(String.format("DESC %s", args.getKeyspace()))
            .setPageSize(1)
            .setTimeout(Duration.ofSeconds(3))  // 3s timeout
            ;

        ResultSet resultSet = session.execute(statement);

        ColumnDefinitions definitions = resultSet.getColumnDefinitions();

        return ClusterUtils.convertMap(session.getContext().getCodecRegistry(), definitions, resultSet.one());
    }

}
