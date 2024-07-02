package kr.hakdang.cadio.core.domain.cluster.info;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hakdang.cadio.common.Jsons;
import kr.hakdang.cadio.core.domain.cluster.ClusterInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

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

    public boolean checkMinClusterCount() {
        long count = registeredCount();

        return count > 0;
    }

    public long registeredCount() {
        ObjectMapper om = Jsons.OBJECT_MAPPER;

        try {
            List<ClusterInfo> result = om.readValue(getClusterJsonFile(), TYPED);

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

}
