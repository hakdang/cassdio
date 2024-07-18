package kr.hakdang.cassdio.core.domain.cluster.info;

import com.fasterxml.jackson.core.type.TypeReference;
import io.micrometer.common.util.StringUtils;
import kr.hakdang.cassdio.common.utils.IdGenerator;
import kr.hakdang.cassdio.common.utils.Jsons;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.Serializer;
import org.springframework.stereotype.Service;

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
public class ClusterManager {

    private final DB mapDb;

    public ClusterManager(DB mapDb) {
        this.mapDb = mapDb;
    }

    public void register(ClusterInfoArgs args) {
        ConcurrentMap<String, String> map = mapDb
            .hashMap("cluster", Serializer.STRING, Serializer.STRING)
            .createOrOpen();

        String clusterId = IdGenerator.makeId();

        map.put(clusterId, Jsons.toJson(args.makeClusterInfo(clusterId)));

        mapDb.commit();
    }

    public void update(String clusterId, ClusterInfoArgs args) {
        ConcurrentMap<String, String> map = mapDb
            .hashMap("cluster", Serializer.STRING, Serializer.STRING)
            .createOrOpen();

        map.put(clusterId, Jsons.toJson(args.makeClusterInfo(clusterId)));

        mapDb.commit();
    }

    public List<ClusterInfo> findAll() {
        ConcurrentMap<String, String> map = mapDb
            .hashMap("cluster", Serializer.STRING, Serializer.STRING)
            .createOrOpen();

        return map.values().stream()
            .map(info -> Jsons.toObject(info, new TypeReference<ClusterInfo>() {
            })).toList();
    }

    //TODO : Cache
    public ClusterInfo findById(String clusterId) {
        ConcurrentMap<String, String> map = mapDb
            .hashMap("cluster", Serializer.STRING, Serializer.STRING)
            .createOrOpen();

        String value = map.get(clusterId);

        if (StringUtils.isBlank(value)) {
            throw new RuntimeException("not found clusterId");
        }

        return Jsons.toObject(value, ClusterInfo.class);
    }

    public void deleteById(String clusterId) {
        ConcurrentMap<String, String> map = mapDb
            .hashMap("cluster", Serializer.STRING, Serializer.STRING)
            .createOrOpen();

        map.remove(clusterId);

        mapDb.commit();
    }

}
