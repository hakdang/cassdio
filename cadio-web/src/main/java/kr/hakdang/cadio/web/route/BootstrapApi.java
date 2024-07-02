package kr.hakdang.cadio.web.route;

import kr.hakdang.cadio.core.domain.bootstrap.BootstrapProvider;
import kr.hakdang.cadio.web.common.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
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
    public ApiResponse<Map<String, Object>> getBootstrap(
    ) {
        Map<String, Object> result = new HashMap<>();

        result.put("systemAvailable", bootstrapProvider.systemAvailable()); //해당 값이 false 경우에는 초기 세팅 화면으로 넘어간다.

        return ApiResponse.ok(
            result
        );
    }
}
