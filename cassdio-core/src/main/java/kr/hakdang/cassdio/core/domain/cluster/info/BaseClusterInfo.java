package kr.hakdang.cassdio.core.domain.cluster.info;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.util.List;

/**
 * BaseClusterInfo
 *
 * @author akageun
 * @since 2024-07-02
 */
public abstract class BaseClusterInfo {

    protected final static TypeReference<List<ClusterInfo>> TYPED = new TypeReference<>() {
    };

    protected String cassdioBaseDir() {
        return System.getProperty("user.home") + "/.cassdio";
    }

    protected File getClusterJsonFile() {
        return new File(cassdioBaseDir() + File.separator + "cluster.json");
    }
}
