package kr.hakdang.cadio.core.domain.cluster.keyspace.table.column;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * ColumnClusteringOrder
 *
 * @author seungh0
 * @since 2024-07-01
 */
@Slf4j
public enum ColumnClusteringOrder {

    ASC,
    DESC,
    NONE,
    UNKNOWN,
    ;

    public static ColumnClusteringOrder findByCode(String code) {
        try {
            return ColumnClusteringOrder.valueOf(StringUtils.upperCase(code));
        } catch (Exception exception) {
            log.error("Unexpected ColumnClusteringOrder: {}", code);
            return UNKNOWN;
        }
    }

}
