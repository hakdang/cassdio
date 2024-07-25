package kr.hakdang.cassdio.core.domain.cluster.client;

import com.datastax.oss.driver.api.core.cql.Row;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.net.InetAddress;

/**
 * CassandraClient
 *
 * @author seungh0
 * @since 2024-07-25
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CassandraClient {

    private InetAddress address;
    private String driverVersion;
    private long requestCount;
    private String username;

    @Builder
    private CassandraClient(InetAddress address, String driverVersion, long requestCount, String username) {
        this.address = address;
        this.driverVersion = driverVersion;
        this.requestCount = requestCount;
        this.username = username;
    }

    public static CassandraClient from(Row row) {
        return CassandraClient.builder()
            .address(row.getInetAddress("address"))
            .driverVersion(row.getString("driver_version"))
            .requestCount(row.getLong("request_count"))
            .username(row.getString("username"))
            .build();
    }

}
