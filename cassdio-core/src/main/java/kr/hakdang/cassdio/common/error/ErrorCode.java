package kr.hakdang.cassdio.common.error;

import lombok.Getter;

@Getter
public enum ErrorCode {

    E400_INVALID_PARAMETER(400, "invalid_parameter"),
    E404_NOT_FOUND_CLUSTER_NODE(404, "not_exists_node"),
    E404_NOT_FOUND_KEYSPACE(404, "not_exists_keyspace"),
    E404_NOT_FOUND_TABLE(404, "not_exists_table"),
    E500_INTERNAL_SERVER_ERROR(500, "internal_server_error"),
    ;

    private final int httpStatusCode;
    private final String code;

    ErrorCode(int httpStatusCode, String code) {
        this.httpStatusCode = httpStatusCode;
        this.code = code;
    }

}