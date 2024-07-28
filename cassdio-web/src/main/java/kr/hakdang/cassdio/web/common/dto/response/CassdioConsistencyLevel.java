package kr.hakdang.cassdio.web.common.dto.response;

import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * CassdioConsistencyLevel
 *
 * @author akageun
 * @since 2024-07-28
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CassdioConsistencyLevel {
    private String name;
    private int protocolCode;
    private boolean isDcLocal;
    private boolean isSerial;

    @Builder
    public CassdioConsistencyLevel(String name, int protocolCode, boolean isDcLocal, boolean isSerial) {
        this.name = name;
        this.protocolCode = protocolCode;
        this.isDcLocal = isDcLocal;
        this.isSerial = isSerial;
    }

    public static CassdioConsistencyLevel make(DefaultConsistencyLevel consistencyLevel) {
        return CassdioConsistencyLevel.builder()
            .name(consistencyLevel.name())
            .protocolCode(consistencyLevel.getProtocolCode())
            .isDcLocal(consistencyLevel.isDcLocal())
            .isSerial(consistencyLevel.isSerial())
            .build();
    }

    public static List<CassdioConsistencyLevel> makeList(DefaultConsistencyLevel[] consistencyLevels) {
        return Arrays.stream(consistencyLevels)
            .map(info -> CassdioConsistencyLevel.builder()
                .name(info.name())
                .protocolCode(info.getProtocolCode())
                .isDcLocal(info.isDcLocal())
                .isSerial(info.isSerial())
                .build())
            .toList();
    }
}
