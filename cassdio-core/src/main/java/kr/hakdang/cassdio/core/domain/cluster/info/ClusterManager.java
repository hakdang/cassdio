package kr.hakdang.cassdio.core.domain.cluster.info;

import kr.hakdang.cassdio.common.utils.Jsons;
import kr.hakdang.cassdio.core.domain.cluster.ClusterUtils;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * ClusterManager
 *
 * @author akageun
 * @since 2024-07-16
 */
@Slf4j
@Service
public class ClusterManager implements InitializingBean {

    public void register(ClusterInfoRegisterArgs args) {
        try (DB db = makeDB(true)) {
            ConcurrentMap<String, String> map = db
                .hashMap("cluster", Serializer.STRING, Serializer.STRING)
                .createOrOpen();

            String clusterId = ClusterUtils.generateClusterId();

            map.put(clusterId, Jsons.toJson(args.makeClusterInfo(clusterId)));

            db.commit();
        }
    }

    public void findAll() {
        try (DB db = makeDB(false)) {
            ConcurrentMap<String, String> map = db
                .hashMap("cluster", Serializer.STRING, Serializer.STRING)
                .createOrOpen();

            for (Map.Entry<String, String> entry : map.entrySet()) {
                log.info("key : {}, value : {}", entry.getKey(), entry.getValue());
            }
        }
    }


    private DB makeDB(boolean transactionEnable) {
        DBMaker.Maker maker = DBMaker
            .fileDB(System.getProperty("user.home") + "/.cassdio/cassdio_v1.db")
            .fileMmapEnable()            // Always enable mmap
            .fileMmapEnableIfSupported() // Only enable mmap on supported platforms
            .fileMmapPreclearDisable()   // Make mmap file faster

            // Unmap (release resources) file when its closed.
            // That can cause JVM crash if file is accessed after it was unmapped
            // (there is possible race condition).
            .cleanerHackEnable();

        if (transactionEnable) {
            maker.transactionEnable();
        }

        return maker.make();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
