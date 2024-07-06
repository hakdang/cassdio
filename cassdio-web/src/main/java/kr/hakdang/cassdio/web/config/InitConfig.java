package kr.hakdang.cassdio.web.config;

import kr.hakdang.cassdio.core.domain.bootstrap.BootstrapProvider;
import kr.hakdang.cassdio.core.domain.cluster.info.ClusterInfoProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * InitConfig
 *
 * @author akageun
 * @since 2024-07-02
 */
@Configuration
public class InitConfig implements InitializingBean {

    @Autowired
    private BootstrapProvider bootstrapProvider;

    @Autowired
    private ClusterInfoProvider clusterInfoProvider;

    @Override
    public void afterPropertiesSet() throws Exception {
        bootstrapProvider.updateMinClusterCountCheck(clusterInfoProvider.checkMinClusterCount());
    }
}
