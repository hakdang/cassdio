package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.Version;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.context.DriverContext;
import com.datastax.oss.driver.api.core.cql.ColumnDefinition;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.metadata.Node;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.MapType;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.internal.core.channel.DriverChannel;
import com.datastax.oss.driver.internal.core.context.InternalDriverContext;
import com.datastax.oss.driver.internal.core.metadata.schema.queries.KeyspaceFilter;
import com.datastax.oss.driver.internal.core.type.codec.ListCodec;
import com.datastax.oss.driver.internal.core.type.codec.MapCodec;
import com.datastax.oss.driver.internal.core.type.codec.SetCodec;
import com.datastax.oss.driver.internal.core.util.Strings;
import kr.hakdang.cassdio.common.utils.Jsons;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassandraSystemKeyspace;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.CRC32;

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

            String formatedValue;
            if (codec instanceof MapCodec) {
                DataType cqlType = codec.getCqlType();
                DataType keyType = ((MapType) cqlType).getKeyType();
                DataType valueType = ((MapType) cqlType).getValueType();
                TypeCodec<Object> keyCodec = codecRegistry.codecFor(keyType);
                TypeCodec<Object> valueCodec = codecRegistry.codecFor(valueType);
                Map<Object, Object> valueMap = (Map<Object, Object>) value;
                Map<String, String> convertedMap = new HashMap<>();
                for (Map.Entry<Object, Object> entry : valueMap.entrySet()) {
                    String key = keyCodec.format(entry.getKey());
                    if (Strings.isQuoted(key)) {
                        key = Strings.unquote(key);
                    }

                    String entryValue = valueCodec.format(entry.getValue());
                    if (Strings.isQuoted(entryValue)) {
                        entryValue = Strings.unquote(entryValue);
                    }

                    convertedMap.put(key, entryValue);
                }

                formatedValue = Jsons.toJson(convertedMap);
            } else {
                formatedValue = codec.format(value);
                if (Strings.isQuoted(formatedValue)) {
                    formatedValue = Strings.unquote(formatedValue);
                }
            }

            result.put(name, formatedValue);
        }

        return result;
    }


    public static Version cassandraVersion(CqlSession session) {
        DriverChannel channel = ((InternalDriverContext) session.getContext()).getControlConnection().channel();
        Node node = session.getMetadata().findNode(channel.getEndPoint())
            .orElseThrow();//TODO : node not found exception 처리

        return node.getCassandraVersion();
    }

    public static KeyspaceFilter makeKeyspaceFilter(DriverContext context) {
        return KeyspaceFilter.newInstance(context.getSessionName(), context.getConfig()
            .getDefaultProfile()
            .getStringList(DefaultDriverOption.METADATA_SCHEMA_REFRESHED_KEYSPACES, Collections.emptyList()));
    }

    public static boolean isVirtualKeyspace(DriverContext context, String keyspace) {
        return !makeKeyspaceFilter(context).includes(keyspace) && CassandraSystemKeyspace.isVirtualSystemKeyspace(keyspace);
    }

    public static boolean isSystemKeyspace(DriverContext context, String keyspace) {
        return !makeKeyspaceFilter(context).includes(keyspace);
    }
}
