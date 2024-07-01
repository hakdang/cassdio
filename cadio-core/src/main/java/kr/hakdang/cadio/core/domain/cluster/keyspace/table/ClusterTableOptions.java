package kr.hakdang.cadio.core.domain.cluster.keyspace.table;

import com.datastax.oss.driver.api.core.cql.Row;

import java.util.function.Function;

public enum ClusterTableOptions {

    ADDITIONAL_WRITE_POLICY(row -> row.getString("additional_write_policy")),
    CACHING(row -> row.getMap("caching", String.class, String.class)),
    COMPACTION(row -> row.getMap("compaction", String.class, String.class)),
    COMPRESSION(row -> row.getMap("compression", String.class, String.class)),
    DEFAULT_TIME_TO_LIVE(row -> row.getInt("default_time_to_live")),
    GR_GRACE_SECONDS(row -> row.getInt("gc_grace_seconds")),
    BLOOM_FILTER_FP_CHANCE(row -> row.getDouble("bloom_filter_fp_chance")),
    ;

    private final Function<Row, Object> extracting;

    ClusterTableOptions(Function<Row, Object> extracting) {
        this.extracting = extracting;
    }

    public Object extract(Row row) {
        return this.extracting.apply(row);
    }

}
