package kr.hakdang.cadio.core.domain.system;

import lombok.Getter;

/**
 * SystemKeyspace
 *
 * @author seungh0
 * @since 2024-07-02
 */
@Getter
public enum SystemKeyspace {

    SYSTEM_SCHEMA("system_schema"),
    SYSTEM("system"),
    SYSTEM_AUTH("system_auth"),
    SYSTEM_DISTRIBUTED("system_distributed"),
    SYSTEM_TRACES("system_traces"),
    SYSTEM_VIEWS("system_views"),
    SYSTEM_VIRTUAL_SCHEMA("system_virtual_schema"),
    ;

    private final String keyspaceName;

    SystemKeyspace(String keyspaceName) {
        this.keyspaceName = keyspaceName;
    }

}
