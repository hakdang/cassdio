package kr.hakdang.cassdio.web.controller

import kr.hakdang.cassdio.core.api.ApiResponse
import kr.hakdang.cassdio.core.metadata.bootstrap.MetadataStatusService
import kr.hakdang.cassdio.web.dto.MetadataStatusResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/metadata")
class MetadataController(
    private val metadataStatusService: MetadataStatusService,
) {
    @GetMapping("/status")
    fun status(): ApiResponse<MetadataStatusResponse> {
        val status = metadataStatusService.status()

        return ApiResponse.success(
            MetadataStatusResponse(
                configured = status.configured,
                source = status.source,
                keyspace = status.keyspace,
                connected = status.connected,
                schemaVersion = status.schemaVersion,
                bootstrapCompleted = status.bootstrapCompleted,
                installationId = status.installationId?.toString(),
            ),
        )
    }
}
