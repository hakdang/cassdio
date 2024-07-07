package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.context.DriverContext;
import com.datastax.oss.driver.api.core.cql.ColumnDefinition;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.internal.core.metadata.schema.queries.KeyspaceFilter;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassandraSystemKeyspace;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * ClusterUtils
 *
 * @author akageun
 * @since 2024-07-05
 */
public abstract class ClusterUtils {

    public static Map<String, Object> convertMap(CodecRegistry codecRegistry, ColumnDefinitions definitions, Row row) {
        Map<String, Object> result = new HashMap<>();

        for (int i = 0; i < definitions.size(); i++) {
            ColumnDefinition definition = definitions.get(i);
            String name = definition.getName().asCql(true);
            TypeCodec<Object> codec = codecRegistry.codecFor(definition.getType());
            Object value = codec.decode(row.getBytesUnsafe(i), row.protocolVersion());

            result.put(name, value);
        }

        return result;
    }

    public static KeyspaceFilter makeKeyspaceFilter(DriverContext context) {
        return KeyspaceFilter.newInstance(context.getSessionName(), context.getConfig()
            .getDefaultProfile()
            .getStringList(DefaultDriverOption.METADATA_SCHEMA_REFRESHED_KEYSPACES, Collections.emptyList()));
    }

    public static boolean isVirtualKeyspace(DriverContext context, String keyspace) {
        return !makeKeyspaceFilter(context).includes(keyspace) && CassandraSystemKeyspace.isVirtualSystemKeyspace(keyspace);
    }

    public static String generateClusterId() {
        String uuid = UUID.randomUUID().toString();
        byte[] temp = uuid.getBytes(StandardCharsets.UTF_8);
        byte[] test;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            test = digest.digest(temp);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < 4; j++) {
            sb.append(String.format("%02x", test[j]));
        }

        return sb.toString();
    }
}
