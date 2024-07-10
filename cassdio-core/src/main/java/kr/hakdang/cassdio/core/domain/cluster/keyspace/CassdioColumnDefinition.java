package kr.hakdang.cassdio.core.domain.cluster.keyspace;

import com.datastax.oss.driver.api.core.cql.ColumnDefinition;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * CassdioColumnDefinition
 *
 * @author akageun
 * @since 2024-07-09
 */
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CassdioColumnDefinition {
    private String keyspace;
    private String table;
    private String columnName;
    private String type;
    private String viewType;

    @Builder
    public CassdioColumnDefinition(String keyspace, String table, String columnName, String type, String viewType) {
        this.keyspace = keyspace;
        this.table = table;
        this.columnName = columnName;
        this.type = type;
        this.viewType = viewType;
    }

    public static CassdioColumnDefinition make(ColumnDefinition definition) {

        return CassdioColumnDefinition.builder()
            .keyspace(definition.getKeyspace().asCql(true))
            .table(definition.getTable().asCql(true))
            .columnName(definition.getName().asCql(true))
            .type(definition.getType().asCql(true, true))
            .viewType("") //TODO :type 값으로 확인해서 enum 화
            .build();
    }

    public static List<CassdioColumnDefinition> makes(ColumnDefinitions definitions) {
        List<CassdioColumnDefinition> list = new ArrayList<>();
        for (ColumnDefinition definition : definitions) {
            list.add(make(definition));
        }

        return list;
    }
}
