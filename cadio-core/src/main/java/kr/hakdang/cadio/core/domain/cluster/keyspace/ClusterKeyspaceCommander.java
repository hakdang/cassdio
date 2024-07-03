package kr.hakdang.cadio.core.domain.cluster.keyspace;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.shaded.guava.common.collect.Maps;
import kr.hakdang.cadio.core.domain.cluster.BaseClusterCommander;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ClusterKeyspaceDescribeCommander
 *
 * @author akageun
 * @since 2024-06-30
 */
@Slf4j
@Service
public class ClusterKeyspaceCommander extends BaseClusterCommander {

    public ClusterKeyspaceListResult keyspaceList(CqlSession session) {
        ResultSet resultSet = session.execute(QueryBuilder.selectFrom(
            "system_schema",
            "keyspaces"
        ).all().build());

        boolean wasApplied = resultSet.wasApplied();
        List<KeyspaceResult> keyspaceList = new ArrayList<>();
        for (Row row : resultSet.all()) {
            keyspaceList.add(
                KeyspaceResult.builder()
                    .keyspaceName(row.getString("keyspace_name"))
                    .durableWrites(row.getBoolean("durable_writes"))
                    .replication(row.getMap("replication", String.class, String.class))
                    .build()
            );
        }

        return ClusterKeyspaceListResult.builder()
            .wasApplied(wasApplied)
            .keyspaceList(keyspaceList)
            .build();
    }

    public String describe(CqlSession session, ClusterKeyspaceDescribeArgs args) {
        //TODO : system keyspace 는 접근 못함.
        KeyspaceMetadata keyspaceMetadata = session.getMetadata().getKeyspace(args.getKeyspace())
            .orElseThrow(() -> new RuntimeException("not found keyspace"));

        return args.isWithChildren() ?
            keyspaceMetadata.describeWithChildren(args.isPretty()) :
            keyspaceMetadata.describe(args.isPretty());

    }
}
