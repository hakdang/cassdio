package kr.hakdang.cassdio.web.route;

import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
import kr.hakdang.cassdio.web.common.dto.response.CassdioConsistencyLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * BootstrapApi
 *
 * @author akageun
 * @since 2024-06-30
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class BootstrapApi {

    @GetMapping("/bootstrap")
    public ApiResponse<Map<String, Object>> bootstrap() {
        Map<String, Object> result = new HashMap<>();

        result.put("systemAvailable", true);
        result.put("login", false);

        result.put("consistencyLevels", CassdioConsistencyLevel.makeList(DefaultConsistencyLevel.values()));
        result.put("defaultConsistencyLevel", CassdioConsistencyLevel.make(DefaultConsistencyLevel.LOCAL_ONE)); //TODO : System Config

        return ApiResponse.ok(result);
    }
}
