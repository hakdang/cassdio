package kr.hakdang.cassdio.core.domain.cluster;

import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassdioColumnDefinition;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * CqlSessionSelectResult
 *
 * @author akageun
 * @since 2024-07-09
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CqlSessionSelectResult {

    private Map<String, Object> row;
    private List<CassdioColumnDefinition> columns;

    @Builder
    public CqlSessionSelectResult(Map<String, Object> row, List<CassdioColumnDefinition> columns) {
        this.row = row;
        this.columns = columns;
    }

    public static CqlSessionSelectResult empty() {
        return CqlSessionSelectResult.builder()
            .row(Collections.emptyMap())
            .columns(Collections.emptyList())
            .build();
    }
}