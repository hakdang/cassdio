package kr.hakdang.cadio.core.domain.cluster.keyspace;

import lombok.Getter;

import java.util.Arrays;

/**
 * CassandraSystemKeyspace
 *
 * @author seungh0
 * @since 2024-07-02
 */
@Getter
public enum CassandraSystemKeyspace {

    SYSTEM_SCHEMA("system_schema"),
    SYSTEM("system"),
    SYSTEM_AUTH("system_auth"),
    SYSTEM_DISTRIBUTED("system_distributed"),
    SYSTEM_TRACES("system_traces"),
    SYSTEM_VIEWS("system_views"),
    SYSTEM_VIRTUAL_SCHEMA("system_virtual_schema"),
    ;

    private final String keyspaceName;

    CassandraSystemKeyspace(String keyspaceName) {
        this.keyspaceName = keyspaceName;
    }

    public static boolean isSystemKeyspace(String keyspaceName) {
        return Arrays.stream(CassandraSystemKeyspace.values())
            .anyMatch(keyspace -> keyspace.getKeyspaceName().equals(keyspaceName));
    }

}
