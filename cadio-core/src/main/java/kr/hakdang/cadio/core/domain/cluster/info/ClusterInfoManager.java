package kr.hakdang.cadio.core.domain.cluster.info;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hakdang.cadio.common.Jsons;
import kr.hakdang.cadio.core.domain.bootstrap.BootstrapProvider;
import kr.hakdang.cadio.core.domain.cluster.ClusterInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * ClusterManager
 *
 * @author akageun
 * @since 2024-07-02
 */
@Slf4j
@Service
public class ClusterInfoManager extends BaseClusterInfo {

    private final BootstrapProvider bootstrapProvider;

    private final ClusterInfoProvider clusterInfoProvider;

    public ClusterInfoManager(
        BootstrapProvider bootstrapProvider,
        ClusterInfoProvider clusterInfoProvider
    ) {
        this.bootstrapProvider = bootstrapProvider;
        this.clusterInfoProvider = clusterInfoProvider;
    }

    public void register(
        String contactPoints,
        int port,
        String localDatacenter,
        String username,
        String password
    ) {
        ObjectMapper om = Jsons.OBJECT_MAPPER;

        try {
            String cadioBaseDir = cadioBaseDir();

            File baseDir = new File(cadioBaseDir);
            if (!baseDir.exists()) {
                boolean result = baseDir.mkdir();
                log.info("make base directory : {}, path : {}", result, cadioBaseDir);
            }

            File clusterJsonFile = getClusterJsonFile();
            if (!clusterJsonFile.exists()) {
                om.writeValue(clusterJsonFile, Collections.emptyList());
            }

            List<ClusterInfo> result = om.readValue(clusterJsonFile, TYPED);
            result.add(ClusterInfo.builder()
                .contactPoints(contactPoints)
                .port(port)
                .localDatacenter(localDatacenter)
                .username(username)
                .password(password)
                .build());

            om.writeValue(clusterJsonFile, result);
            bootstrapProvider.updateMinClusterCountCheck(clusterInfoProvider.checkMinClusterCount());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
