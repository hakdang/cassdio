package kr.hakdang.cassdio.web.controller

import kr.hakdang.cassdio.core.api.ApiResponse
import kr.hakdang.cassdio.web.dto.HealthResponse
import kr.hakdang.cassdio.web.dto.VersionResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class HealthController(
    @Value("\${spring.application.name:cassdio-web}") private val applicationName: String,
    @Value("\${cassdio.version:0.1.0}") private val version: String,
) {
    @GetMapping("/health")
    fun health(): ApiResponse<HealthResponse> =
        ApiResponse.success(
            HealthResponse(
                status = "UP",
                service = applicationName,
            ),
        )

    @GetMapping("/version")
    fun version(): ApiResponse<VersionResponse> =
        ApiResponse.success(
            VersionResponse(
                name = applicationName,
                version = version,
            ),
        )
}
