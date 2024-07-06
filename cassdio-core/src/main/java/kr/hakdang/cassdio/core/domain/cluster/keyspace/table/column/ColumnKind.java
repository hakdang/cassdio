package kr.hakdang.cassdio.core.domain.cluster.keyspace.table.column;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * ColumnKind
 *
 * @author seungh0
 * @since 2024-07-01
 */
@Slf4j
@Getter
public enum ColumnKind {

    PARTITION_KEY(1),
    CLUSTERING(2),
    REGULAR(3),
    UNKNOWN(4),
    ;

    private final int order;

    ColumnKind(int order) {
        this.order = order;
    }

    public static ColumnKind findByCode(String code) {
        try {
            return ColumnKind.valueOf(StringUtils.upperCase(code));
        } catch (Exception exception) {
            log.error("Unexpected ColumnKind: {}", code);
            return UNKNOWN;
        }
    }

}
