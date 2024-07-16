package kr.hakdang.cassdio.core.domain.cluster.info;

import com.fasterxml.jackson.core.type.TypeReference;
import io.micrometer.common.util.StringUtils;
import kr.hakdang.cassdio.common.utils.IdGenerator;
import kr.hakdang.cassdio.common.utils.Jsons;
import kr.hakdang.cassdio.core.domain.cluster.ClusterUtils;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.List;
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
public class ClusterManager implements InitializingBean, DisposableBean {

    private static DB db;

    public void register(ClusterInfoArgs args) {
        ConcurrentMap<String, String> map = db
            .hashMap("cluster", Serializer.STRING, Serializer.STRING)
            .createOrOpen();

        String clusterId = IdGenerator.makeId();

        map.put(clusterId, Jsons.toJson(args.makeClusterInfo(clusterId)));

        db.commit();
    }

    public void update(String clusterId, ClusterInfoArgs args) {
        ConcurrentMap<String, String> map = db
            .hashMap("cluster", Serializer.STRING, Serializer.STRING)
            .createOrOpen();

        map.put(clusterId, Jsons.toJson(args.makeClusterInfo(clusterId)));

        db.commit();
    }

    public List<ClusterInfo> findAll() {
        ConcurrentMap<String, String> map = db
            .hashMap("cluster", Serializer.STRING, Serializer.STRING)
            .createOrOpen();

        return map.values().stream()
            .map(info -> Jsons.toObject(info, new TypeReference<ClusterInfo>() {
            })).toList();
    }

    //TODO : Cache
    public ClusterInfo findById(String clusterId) {
        ConcurrentMap<String, String> map = db
            .hashMap("cluster", Serializer.STRING, Serializer.STRING)
            .createOrOpen();

        String value = map.get(clusterId);

        if (StringUtils.isBlank(value)) {
            throw new RuntimeException("not found clusterId");
        }

        return Jsons.toObject(value, ClusterInfo.class);
    }

    public void deleteById(String clusterId) {
        ConcurrentMap<String, String> map = db
            .hashMap("cluster", Serializer.STRING, Serializer.STRING)
            .createOrOpen();

        map.remove(clusterId);

        db.commit();
    }

    private DB makeDB() {
        DBMaker.Maker maker = DBMaker

            //TODO : 추후 프로퍼티 받아서 주입할 수 있도록 변경 예정
            .fileDB(System.getProperty("user.home") + "/.cassdio/cassdio_v1.db")
            .fileMmapEnable()            // Always enable mmap
            .fileMmapEnableIfSupported() // Only enable mmap on supported platforms
            .fileMmapPreclearDisable()   // Make mmap file faster
            .transactionEnable()
            // Unmap (release resources) file when its closed.
            // That can cause JVM crash if file is accessed after it was unmapped
            // (there is possible race condition).
            .cleanerHackEnable();

        return maker.make();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (db == null) {
            db = makeDB();
        }

    }

    @Override
    public void destroy() throws Exception {
        if (db != null) {
            db.close();
        }
    }
}
