package kr.hakdang.cassdio.core.domain.cluster.info;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hakdang.cassdio.common.utils.Jsons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClusterInfoProvider
 * - 전체적으로 구조 잡고, file 에 캐시 적용할 예정
 * - 캐시 적용 후 eviction 처리 잘 할 것
 *
 * @author akageun
 * @since 2024-07-02
 */
@Slf4j
@Service
public class ClusterInfoProvider extends BaseClusterInfo {

    private static final ObjectMapper OM = Jsons.OBJECT_MAPPER;

    public boolean checkMinClusterCount() {
        long count = registeredCount();

        return count > 0;
    }

    public long registeredCount() {

        try {
            List<ClusterInfo> result = OM.readValue(getClusterJsonFile(), TYPED);

            if (CollectionUtils.isEmpty(result)) {
                return 0;
            }

            return result.size();

        } catch (FileNotFoundException e) {
            log.warn(e.getMessage(), e); //첫 실행시, TODO : 에러메시지 등 고민 필요
            return 0;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    public List<ClusterInfo> getList() {
        try {
            return OM.readValue(getClusterJsonFile(), TYPED);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * @return key : clusterId
     */
    public Map<String, ClusterInfo> getDictionary() {
        return getList().stream().collect(Collectors.toMap(ClusterInfo::getClusterId, o -> o));
    }

    public ClusterInfo findClusterInfo(String clusterId) {
        return getDictionary().get(clusterId);
    }
}
