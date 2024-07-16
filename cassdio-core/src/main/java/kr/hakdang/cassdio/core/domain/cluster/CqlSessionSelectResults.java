package kr.hakdang.cassdio.core.domain.cluster;

import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassdioColumnDefinition;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * CqlSessionSelectResults
 *
 * @author akageun
 * @since 2024-07-09
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CqlSessionSelectResults {

    private List<Map<String, Object>> rows;
    private List<CassdioColumnDefinition> columnHeader;
    private String nextCursor;

    @Builder
    public CqlSessionSelectResults(
        List<Map<String, Object>> rows,
        List<CassdioColumnDefinition> columnHeader,
        String nextCursor
    ) {
        this.rows = rows;
        this.columnHeader = columnHeader;
        this.nextCursor = nextCursor;
    }
}
