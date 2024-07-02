package kr.hakdang.cadio.core.domain.cluster.info;

import com.fasterxml.jackson.core.type.TypeReference;
import kr.hakdang.cadio.core.domain.cluster.ClusterInfo;

import java.io.File;
import java.util.List;

/**
 * BaseClusterInfo
 *
 * @author akageun
 * @since 2024-07-02
 */
public abstract class BaseClusterInfo {

    protected final static TypeReference<List<ClusterInfo>> TYPED = new TypeReference<List<ClusterInfo>>() {
    };

    protected String cadioBaseDir() {
        return System.getProperty("user.home") + "/.cadio";
    }

    protected File getClusterJsonFile() {
        return new File(cadioBaseDir() + File.separator + "cluster.json");
    }
}
