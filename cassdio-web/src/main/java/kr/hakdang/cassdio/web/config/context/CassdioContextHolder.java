package kr.hakdang.cassdio.web.config.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * CassdioContextHolder
 *
 * @author akageun
 * @since 2024-07-29
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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
