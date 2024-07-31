package kr.hakdang.cassdio.core.domain.cluster.info;

import com.fasterxml.jackson.core.type.TypeReference;
import io.micrometer.common.util.StringUtils;
import kr.hakdang.cassdio.common.utils.IdGenerator;
import kr.hakdang.cassdio.common.utils.Jsons;
import kr.hakdang.cassdio.core.domain.cluster.ClusterException;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * ClusterManager
 *
 * @author akageun
 * @since 2024-07-16
 */
@Slf4j
@Service
public class ClusterManager implements InitializingBean, DisposableBean {

    public static final String CLUSTER = "cluster";
    @Value("${cassdio.system.database.path}")
    private String dbPath;

    private static DB mapDb;

    public void register(ClusterInfo info) {
        ConcurrentMap<String, String> map = mapDb
            .hashMap(CLUSTER, Serializer.STRING, Serializer.STRING)
            .createOrOpen();

        map.put(info.getClusterId(), Jsons.toJson(info));

        mapDb.commit();
    }

    public void update(String clusterId, ClusterInfo info) {
        ConcurrentMap<String, String> map = mapDb
            .hashMap(CLUSTER, Serializer.STRING, Serializer.STRING)
            .createOrOpen();

        map.put(clusterId, Jsons.toJson(info));

        mapDb.commit();
    }

    public List<ClusterInfo> findAll() {
        ConcurrentMap<String, String> map = mapDb
            .hashMap(CLUSTER, Serializer.STRING, Serializer.STRING)
            .createOrOpen();

        return map.values().stream()
            .map(info -> Jsons.toObject(info, new TypeReference<ClusterInfo>() {
            })).toList();
    }

    public ClusterInfo findById(String clusterId) {
        ConcurrentMap<String, String> map = mapDb
            .hashMap(CLUSTER, Serializer.STRING, Serializer.STRING)
            .createOrOpen();

        String value = map.get(clusterId);

        if (StringUtils.isBlank(value)) {
            throw new ClusterException.ClusterNodeNotFoundException(String.format("not found clusterId : %s", clusterId));
        }

        return Jsons.toObject(value, ClusterInfo.class);
    }

    public void deleteById(String clusterId) {
        ConcurrentMap<String, String> map = mapDb
            .hashMap(CLUSTER, Serializer.STRING, Serializer.STRING)
            .createOrOpen();

        map.remove(clusterId);

        mapDb.commit();
    }

    @Override
    public void destroy() throws Exception {
        if (mapDb != null) {
            mapDb.close();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        File file = new File(dbPath);
        if (!file.exists()) {
            boolean result = file.mkdirs();
            log.info("create db path : {}", result);
        }

        DBMaker.Maker maker = DBMaker
            .fileDB(dbPath + "/cassdio_v1.db")
            .fileMmapEnable()            // Always enable mmap
            .fileMmapEnableIfSupported() // Only enable mmap on supported platforms
            .fileMmapPreclearDisable()   // Make mmap file faster
            .transactionEnable()
            .fileLockDisable()
            // Unmap (release resources) file when its closed.
            // That can cause JVM crash if file is accessed after it was unmapped
            // (there is possible race condition).
            .cleanerHackEnable();

        mapDb = maker.make();
    }
}
