package kr.hakdang.cassdio.web.config.context;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * CassdioContext
 *
 * @author akageun
 * @since 2024-07-29
 */
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CassdioContext {
    private String requestId; //Header

    @Builder
    public CassdioContext(String requestId) {
        this.requestId = requestId;
    }
}
