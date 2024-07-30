package kr.hakdang.cassdio.web.config.context;

import kr.hakdang.cassdio.web.common.dto.response.CassdioConsistencyLevel;

import java.util.Objects;

/**
 * CassdioContextHolder
 *
 * @author akageun
 * @since 2024-07-29
 */
public class CassdioContextHolder {

    private static final ThreadLocal<CassdioContext> DATA = new ThreadLocal<>();

    public static void set(CassdioContext context) {
        DATA.set(context);
    }

    public static CassdioContext get() {
        return DATA.get();
    }

    public static void clear() {
        DATA.remove();
    }
}
