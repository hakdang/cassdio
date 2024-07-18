package kr.hakdang.cassdio.web.route;

import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
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
@RestController
@RequestMapping("/api")
public class BootstrapApi {

    @GetMapping("/bootstrap")
    public ApiResponse<Map<String, Object>> getBootstrap() {
        Map<String, Object> result = new HashMap<>();

        result.put("systemAvailable", true);

        return ApiResponse.ok(result);
    }
}
