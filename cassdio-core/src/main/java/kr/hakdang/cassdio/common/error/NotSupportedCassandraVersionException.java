package kr.hakdang.cassdio.common.error;

/**
 * NotSupportedCassandraVersionException
 *
 * @author seungh0
 * @since 2024-07-25
 */
public class NotSupportedCassandraVersionException extends BaseException {

    public NotSupportedCassandraVersionException(String message) {
        super(message, ErrorCode.E501_NOT_SUPPORTED_CASSANDRA_VERSION);
    }

}
