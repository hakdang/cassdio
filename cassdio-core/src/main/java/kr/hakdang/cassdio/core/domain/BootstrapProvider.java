package kr.hakdang.cassdio.core.domain;

import kr.hakdang.cassdio.core.domain.cluster.CassdioConsistencyLevel;
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * BootstrapProvider
 *
 * @author akageun
 * @since 2024-07-30
 */
@Slf4j
@Service
public class BootstrapProvider {

    public Map<String, Object> consistencyLevels() {
        Map<String, Object> result = new HashMap<>();
        result.put("consistencyLevels", CassdioConsistencyLevel.makeList(DefaultConsistencyLevel.values()));
        result.put("defaultConsistencyLevel", CassdioConsistencyLevel.make(DefaultConsistencyLevel.LOCAL_ONE)); //TODO : System Config

        return result;
    }
}
