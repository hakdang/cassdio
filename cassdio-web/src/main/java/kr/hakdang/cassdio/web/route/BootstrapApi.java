package kr.hakdang.cassdio.web.route;


import kr.hakdang.cassdio.core.domain.BootstrapProvider;
import kr.hakdang.cassdio.web.common.dto.response.ApiResponse;
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

    private final BootstrapProvider bootstrapProvider;

    public BootstrapApi(BootstrapProvider bootstrapProvider) {
        this.bootstrapProvider = bootstrapProvider;
    }

    @GetMapping("/bootstrap")
    public ApiResponse<Map<String, Object>> bootstrap() {
        Map<String, Object> result = new HashMap<>();

        result.put("systemAvailable", true);
        result.put("login", false);

        result.putAll(bootstrapProvider.consistencyLevels());

        return ApiResponse.ok(result);
    }
}
