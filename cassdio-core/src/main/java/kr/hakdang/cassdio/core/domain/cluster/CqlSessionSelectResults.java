package kr.hakdang.cassdio.core.domain.cluster;

import com.datastax.oss.protocol.internal.util.Bytes;
import io.micrometer.common.util.StringUtils;
import kr.hakdang.cassdio.core.domain.cluster.keyspace.CassdioColumnDefinition;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
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
    private List<CassdioColumnDefinition> rowHeader;
    private String nextCursor;

    @Builder
    public CqlSessionSelectResults(
        List<Map<String, Object>> rows,
        List<CassdioColumnDefinition> rowHeader,
        String nextCursor
    ) {
        this.rows = rows;
        this.rowHeader = rowHeader;
        this.nextCursor = nextCursor;
    }

    public static CqlSessionSelectResults of(
        List<Map<String, Object>> rows,
        List<CassdioColumnDefinition> rowHeader,
        ByteBuffer pagingState
    ) {
        String nextCursor = "";
        if (pagingState != null) {
            nextCursor = Bytes.toHexString(pagingState);
        }

        return CqlSessionSelectResults.builder()
            .rowHeader(rowHeader)
            .rows(rows)
            .nextCursor(nextCursor)
            .build();
    }

    public static CqlSessionSelectResults of(
        List<Map<String, Object>> rows,
        List<CassdioColumnDefinition> rowHeader
    ) {

        return CqlSessionSelectResults.builder()
            .rowHeader(rowHeader)
            .rows(rows)
            .build();
    }

    public boolean hasNext() {
        return StringUtils.isNotBlank(nextCursor);
    }
}
