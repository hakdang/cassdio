package kr.hakdang.cassdio.web.route;

import kr.hakdang.cassdio.core.domain.bootstrap.BootstrapProvider;
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

    private final BootstrapProvider bootstrapProvider;

    public BootstrapApi(
        BootstrapProvider bootstrapProvider
    ) {
        this.bootstrapProvider = bootstrapProvider;
    }

    @GetMapping("/bootstrap")
    public ApiResponse<Map<String, Object>> getBootstrap() {
        Map<String, Object> result = new HashMap<>();

        result.put("systemAvailable", bootstrapProvider.systemAvailable()); //해당 값이 false 경우에는 초기 세팅 화면으로 넘어간다.

        return ApiResponse.ok(result);
    }
}
