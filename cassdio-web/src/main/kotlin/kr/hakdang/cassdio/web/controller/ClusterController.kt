package kr.hakdang.cassdio.web.controller

import jakarta.validation.Valid
import kr.hakdang.cassdio.core.api.ApiResponse
import kr.hakdang.cassdio.core.metadata.cluster.ManagedClusterService
import kr.hakdang.cassdio.web.dto.ClusterConnectionTestResponse
import kr.hakdang.cassdio.web.dto.ClusterCreateRequest
import kr.hakdang.cassdio.web.dto.ClusterResponse
import kr.hakdang.cassdio.web.dto.ClusterSessionClearResponse
import kr.hakdang.cassdio.web.dto.ClusterUpdateRequest
import kr.hakdang.cassdio.web.dto.toCoreRequest
import kr.hakdang.cassdio.web.dto.toResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/clusters", "/api/cluster")
class ClusterController(
    private val clusterService: ManagedClusterService,
) {
    @GetMapping
    fun list(): ApiResponse<List<ClusterResponse>> = ApiResponse.success(clusterService.list().map { it.toResponse() })

    @GetMapping("/{clusterId}")
    fun detail(
        @PathVariable clusterId: UUID,
    ): ApiResponse<ClusterResponse> = ApiResponse.success(clusterService.detail(clusterId).toResponse())

    @PostMapping("/test")
    fun testConnection(
        @Valid @RequestBody request: ClusterCreateRequest,
    ): ApiResponse<ClusterConnectionTestResponse> = ApiResponse.success(clusterService.testConnection(request.toCoreRequest()).toResponse())

    @PostMapping
    fun create(
        @Valid @RequestBody request: ClusterCreateRequest,
    ): ApiResponse<ClusterResponse> = ApiResponse.success(clusterService.create(request.toCoreRequest()).toResponse())

    @PutMapping("/{clusterId}")
    fun update(
        @PathVariable clusterId: UUID,
        @Valid @RequestBody request: ClusterUpdateRequest,
    ): ApiResponse<ClusterResponse> = ApiResponse.success(clusterService.update(clusterId, request.toCoreRequest()).toResponse())

    @DeleteMapping("/{clusterId}")
    fun delete(
        @PathVariable clusterId: UUID,
    ): ApiResponse<ClusterResponse> = ApiResponse.success(clusterService.delete(clusterId).toResponse())

    @PostMapping("/session/clear")
    fun clearAllSessions(): ApiResponse<ClusterSessionClearResponse> = ApiResponse.success(clusterService.clearAllSessions().toResponse())

    @PostMapping("/{clusterId}/session/clear")
    fun clearSession(
        @PathVariable clusterId: UUID,
    ): ApiResponse<ClusterSessionClearResponse> = ApiResponse.success(clusterService.clearSession(clusterId).toResponse())
}
