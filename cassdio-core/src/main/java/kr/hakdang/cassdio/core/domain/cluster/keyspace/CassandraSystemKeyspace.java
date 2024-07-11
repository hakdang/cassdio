package kr.hakdang.cassdio.core.domain.cluster.keyspace;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

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
    SYSTEM_VIEWS("system_views", true),
    SYSTEM_VIRTUAL_SCHEMA("system_virtual_schema", true),
    ;

    private final String keyspaceName;
    private final boolean isVirtualKeyspace;

    CassandraSystemKeyspace(String keyspaceName, boolean isVirtualKeyspace) {
        this.keyspaceName = keyspaceName;
        this.isVirtualKeyspace = isVirtualKeyspace;
    }

    CassandraSystemKeyspace(String keyspaceName) {
        this.keyspaceName = keyspaceName;
        this.isVirtualKeyspace = false;
    }

    public static boolean isSystemKeyspace(String keyspaceName) {
        return Arrays.stream(CassandraSystemKeyspace.values())
            .anyMatch(keyspace -> keyspace.getKeyspaceName().equals(keyspaceName));
    }

    public static boolean isVirtualSystemKeyspace(String keyspaceName) {
        return Arrays.stream(CassandraSystemKeyspace.values())
            .filter(keyspace -> keyspace.isVirtualKeyspace)
            .anyMatch(keyspace -> keyspace.getKeyspaceName().equals(keyspaceName));
    }

    public static List<CassandraSystemKeyspace> onlySystemKeyspaceList() {
        return Arrays.stream(CassandraSystemKeyspace.values())
            .filter(keyspace -> !keyspace.isVirtualKeyspace)
            .toList();
    }

    public static List<CassandraSystemKeyspace> virtualSystemKeyspaceList() {
        return Arrays.stream(CassandraSystemKeyspace.values())
            .filter(keyspace -> keyspace.isVirtualKeyspace)
            .toList();
    }
}
