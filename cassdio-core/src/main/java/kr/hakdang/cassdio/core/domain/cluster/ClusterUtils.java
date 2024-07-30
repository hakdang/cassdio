package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.Version;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.context.DriverContext;
import com.datastax.oss.driver.api.core.metadata.Node;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.select.SelectFrom;
import com.datastax.oss.driver.internal.core.channel.DriverChannel;
import com.datastax.oss.driver.internal.core.context.InternalDriverContext;
import com.datastax.oss.driver.internal.core.metadata.schema.queries.KeyspaceFilter;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassandraSystemKeyspace;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.table.CassandraSystemTable;

import java.util.Collections;

/**
 * ClusterUtils
 *
 * @author akageun
 * @since 2024-07-05
 */
public abstract class ClusterUtils {

    public static KeyspaceFilter makeKeyspaceFilter(DriverContext context) {
        return KeyspaceFilter.newInstance(context.getSessionName(), context.getConfig()
            .getDefaultProfile()
            .getStringList(DefaultDriverOption.METADATA_SCHEMA_REFRESHED_KEYSPACES, Collections.emptyList()));
    }

    public static boolean isVirtualKeyspace(DriverContext context, String keyspace) {
        return isSystemKeyspace(context, keyspace) && CassandraSystemKeyspace.isVirtualSystemKeyspace(keyspace);
    }

    public static boolean isSystemKeyspace(DriverContext context, String keyspace) {
        return !makeKeyspaceFilter(context).includes(keyspace);
    }

    public static SelectFrom getSchemaTables(CqlSession session, String keyspace) {
        if (ClusterUtils.isVirtualKeyspace(session.getContext(), keyspace)) {
            return QueryBuilder
                .selectFrom(CassandraSystemKeyspace.SYSTEM_VIRTUAL_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_TABLES.getTableName());
        }

        return QueryBuilder
            .selectFrom(CassandraSystemKeyspace.SYSTEM_SCHEMA.getKeyspaceName(), CassandraSystemTable.SYSTEM_SCHEMA_TABLES.getTableName());
    }
}
