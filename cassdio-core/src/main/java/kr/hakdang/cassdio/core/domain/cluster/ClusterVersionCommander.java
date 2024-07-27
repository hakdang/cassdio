package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.Version;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassandraSystemKeyspace;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.CassandraSystemTable;
import org.springframework.stereotype.Service;

/**
 * ClusterVersionCommander
 *
 * @author seungh0
 * @since 2024-07-25
 */
@Service
public class ClusterVersionCommander extends BaseClusterCommander {

    public Version getCassandraVersion(CqlSession session) {
        SimpleStatement statement = QueryBuilder
            .selectFrom(CassandraSystemKeyspace.SYSTEM.getKeyspaceName(), CassandraSystemTable.SYSTEM_LOCAL.getTableName())
            .all()
            .build();

        ResultSet rs = session.execute(statement);
        Row row = rs.one();
        if (row == null) {
            throw new RuntimeException("Can't read release version in system_local");
        }

        return Version.parse(row.getString("release_version"));
    }

}
