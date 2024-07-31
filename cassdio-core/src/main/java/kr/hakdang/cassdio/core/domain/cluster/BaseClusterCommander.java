package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ColumnDefinition;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataType;
import com.datastax.oss.driver.api.core.type.MapType;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.internal.core.type.codec.MapCodec;
import com.datastax.oss.driver.internal.core.util.Strings;
import kr.hakdang.cassdio.common.utils.Jsons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

/**
 * BaseClusterCommander
 *
 * @author akageun
 * @since 2024-07-01
 */
@Slf4j
public abstract class BaseClusterCommander {

    @Autowired
    protected CqlSessionFactory cqlSessionFactory;

    protected List<Map<String, Object>> convertRows(CqlSession session, ResultSet resultSet) {
        ColumnDefinitions definitions = resultSet.getColumnDefinitions();
        CodecRegistry codecRegistry = session.getContext().getCodecRegistry();

        return StreamSupport.stream(resultSet.spliterator(), false)
            .limit(resultSet.getAvailableWithoutFetching())
            .map(row -> convertRow(codecRegistry, definitions, row))
            .toList();
    }

    protected Map<String, Object> convertRow(CodecRegistry codecRegistry, ColumnDefinitions definitions, Row row) {
        Map<String, Object> result = new HashMap<>();

        for (int i = 0; i < definitions.size(); i++) {
            ColumnDefinition definition = definitions.get(i);
            String name = definition.getName().asCql(true);
            TypeCodec<Object> codec = codecRegistry.codecFor(definition.getType());
            Object value = codec.decode(row.getBytesUnsafe(i), row.protocolVersion());

            String formatedValue = makeFormatedValue(codecRegistry, codec, value);

            result.put(name, formatedValue);
        }

        return result;
    }

    private static String makeFormatedValue(CodecRegistry codecRegistry, TypeCodec<Object> codec, Object value) {
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

            return Jsons.toJson(convertedMap);
        }

        String formatedValue = codec.format(value);
        if (Strings.isQuoted(formatedValue)) {
            formatedValue = Strings.unquote(formatedValue);
        }
        return formatedValue;
    }
}
