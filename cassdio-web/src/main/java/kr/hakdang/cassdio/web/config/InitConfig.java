package kr.hakdang.cassdio.web.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * InitConfig
 *
 * @author akageun
 * @since 2024-07-02
 */
@Configuration
public class InitConfig implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        // Do Nothing
    }

}
