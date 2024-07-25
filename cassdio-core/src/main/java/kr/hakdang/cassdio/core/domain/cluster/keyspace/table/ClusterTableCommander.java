package kr.hakdang.cassdio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import kr.hakdang.cassdio.core.domain.cluster.BaseClusterCommander;
import kr.hakdang.cassdio.core.domain.cluster.ClusterUtils;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.ClusterKeyspaceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;

/**
 * ClusterTableCommander
 *
 * @author akageun
 * @since 2024-06-30
 */
@Slf4j
@Service
public class ClusterTableCommander extends BaseClusterCommander {

    public String tableDescribe(CqlSession session, String keyspace, String table) {
        if (ClusterUtils.isSystemKeyspace(session.getContext(), keyspace)) {
            return "";
        }

        //DESC 를 통한 정보 조회는 Cassandra 3.x 에서는 지원 안됨
        try {
            return session.getMetadata()
                .getKeyspace(keyspace)
                .orElseThrow(() -> new ClusterKeyspaceException.ClusterKeyspaceNotFoundException(String.format("not found keyspace (%s)", keyspace)))
                .getTable(table)
                .orElseThrow(() -> new ClusterTableException.ClusterTableNotFoundException(String.format("not found table(%s)", table)))
                .describe(true);

        } catch (NoSuchElementException e) { //ignore
            return "";
        }
    }

    public void tableDrop(CqlSession session, String keyspace, String table) {
        ResultSet resultSet = session.execute(SchemaBuilder.dropTable(keyspace, table).build());
        log.info("Table Drop Result - keyspace: {}, table: {}, ok: {}", keyspace, table, resultSet.wasApplied());
    }

    public void tableTruncate(CqlSession session, String keyspace, String table) {
        ResultSet resultSet = session.execute(QueryBuilder.truncate(keyspace, table).build());
        log.info("Table Truncate Result - keyspace: {}, table: {}, ok: {}", keyspace, table, resultSet.wasApplied());
    }


}
