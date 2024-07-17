package kr.hakdang.cassdio.core.domain.cluster.info;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hakdang.cassdio.common.utils.IdGenerator;
import kr.hakdang.cassdio.common.utils.Jsons;
import kr.hakdang.cassdio.core.domain.cluster.ClusterUtils;
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

    public void register(ClusterInfoRegisterArgs args) {
        ObjectMapper om = Jsons.OBJECT_MAPPER;

        try {
            String cassdioBaseDir = cassdioBaseDir();

            File baseDir = new File(cassdioBaseDir);
            if (!baseDir.exists()) {
                log.info("make base directory : {}, path : {}", baseDir.mkdir(), cassdioBaseDir);
            }

            File clusterJsonFile = getClusterJsonFile();
            if (!clusterJsonFile.exists()) {
                om.writeValue(clusterJsonFile, Collections.emptyList());
            }

            List<ClusterInfo> result = om.readValue(clusterJsonFile, TYPED);
            result.add(args.makeClusterInfo(IdGenerator.makeId()));

            om.writeValue(clusterJsonFile, result);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
