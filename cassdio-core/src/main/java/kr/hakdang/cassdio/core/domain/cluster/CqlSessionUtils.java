package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.Version;
import com.datastax.oss.driver.api.core.cql.ColumnDefinition;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.metadata.Node;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.datastax.oss.driver.internal.core.channel.DriverChannel;
import com.datastax.oss.driver.internal.core.context.InternalDriverContext;
import com.datastax.oss.driver.internal.core.util.Strings;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassdioColumnDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * CqlSessionUtils
 *
 * @author akageun
 * @since 2024-07-09
 */
public abstract class CqlSessionUtils {

    public static CqlSessionSelectResult resultSet(
        CqlSession session,
        ResultSet resultSet
    ) {
        Iterator<Row> page1Iter = resultSet.iterator();
        ColumnDefinitions definitions = resultSet.getColumnDefinitions();
        CodecRegistry codecRegistry = session.getContext().getCodecRegistry();

        List<Map<String, Object>> rows = new ArrayList<>();
        while (0 < resultSet.getAvailableWithoutFetching()) {
            Row tempRow = page1Iter.next();
            rows.add(convertMap(codecRegistry, definitions, tempRow));
        }

        return CqlSessionSelectResult.builder()
            .rows(rows)
            .columns(CassdioColumnDefinition.makes(definitions))
            .build();
    }

    public static Map<String, Object> convertMap(
        CodecRegistry codecRegistry,
        ColumnDefinitions definitions,
        Row row
    ) {
        Map<String, Object> result = new HashMap<>();

        for (int i = 0; i < definitions.size(); i++) {
            ColumnDefinition definition = definitions.get(i);
            String name = definition.getName().asCql(true);
            TypeCodec<Object> codec = codecRegistry.codecFor(definition.getType());
            Object value = codec.decode(row.getBytesUnsafe(i), row.protocolVersion());

            String formatedValue = codec.format(value);
            if (Strings.isQuoted(formatedValue)) {
                formatedValue = Strings.unquote(formatedValue);
            }

            result.put(name, formatedValue);
        }

        return result;
    }
}
