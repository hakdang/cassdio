package kr.hakdang.cadio.web.route;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
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
    public Map<String, Object> getBootstrap() {
        return Collections.emptyMap();
    }
}
