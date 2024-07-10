package kr.hakdang.cassdio.core.domain.cluster;

import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassdioColumnDefinition;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private List<Map<String, Object>> rows;
    private List<CassdioColumnDefinition> columns;

    @Builder
    public CqlSessionSelectResult(List<Map<String, Object>> rows, List<CassdioColumnDefinition> columns) {
        this.rows = rows;
        this.columns = columns;
    }
}
