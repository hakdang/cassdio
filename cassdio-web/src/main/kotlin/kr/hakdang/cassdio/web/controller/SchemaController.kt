package kr.hakdang.cassdio.web.controller

import kr.hakdang.cassdio.core.api.ApiResponse
import kr.hakdang.cassdio.core.metadata.schema.SchemaExplorerService
import kr.hakdang.cassdio.web.dto.ColumnCatalogResponse
import kr.hakdang.cassdio.web.dto.SchemaChangeResponse
import kr.hakdang.cassdio.web.dto.SchemaDangerousActionRequestDto
import kr.hakdang.cassdio.web.dto.SchemaDangerousActionResponse
import kr.hakdang.cassdio.web.dto.SchemaKeyspaceResponse
import kr.hakdang.cassdio.web.dto.SchemaTableDetailResponse
import kr.hakdang.cassdio.web.dto.SchemaTablePageResponse
import kr.hakdang.cassdio.web.dto.SchemaUserTypeDetailResponse
import kr.hakdang.cassdio.web.dto.SchemaUserTypeSummaryResponse
import kr.hakdang.cassdio.web.dto.TableCatalogResponse
import kr.hakdang.cassdio.web.dto.UpdateColumnCatalogRequest
import kr.hakdang.cassdio.web.dto.UpdateTableCatalogRequest
import kr.hakdang.cassdio.web.dto.toCoreRequest
import kr.hakdang.cassdio.web.dto.toResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/clusters/{clusterId}/schema")
class SchemaController(
    private val schemaExplorerService: SchemaExplorerService,
) {
    @GetMapping("/keyspaces")
    fun keyspaces(
        @PathVariable clusterId: UUID,
        @RequestParam(defaultValue = "false") includeSystem: Boolean,
        @RequestParam(defaultValue = "false") refresh: Boolean,
    ): ApiResponse<List<SchemaKeyspaceResponse>> =
        ApiResponse.success(schemaExplorerService.listKeyspaces(clusterId, includeSystem, refresh).map { it.toResponse() })

    @GetMapping("/keyspaces/{keyspaceName}")
    fun keyspace(
        @PathVariable clusterId: UUID,
        @PathVariable keyspaceName: String,
    ): ApiResponse<SchemaKeyspaceResponse> = ApiResponse.success(schemaExplorerService.keyspaceDetail(clusterId, keyspaceName).toResponse())

    @DeleteMapping("/keyspaces/{keyspaceName}")
    fun dropKeyspace(
        @PathVariable clusterId: UUID,
        @PathVariable keyspaceName: String,
        @RequestBody request: SchemaDangerousActionRequestDto,
    ): ApiResponse<SchemaDangerousActionResponse> =
        ApiResponse.success(schemaExplorerService.dropKeyspace(clusterId, keyspaceName, request.toCoreRequest()).toResponse())

    @GetMapping("/keyspaces/{keyspaceName}/tables")
    fun tables(
        @PathVariable clusterId: UUID,
        @PathVariable keyspaceName: String,
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = "50") limit: Int,
    ): ApiResponse<SchemaTablePageResponse> =
        ApiResponse.success(schemaExplorerService.listTables(clusterId, keyspaceName, cursor, limit).toResponse())

    @GetMapping("/keyspaces/{keyspaceName}/tables/{tableName}")
    fun table(
        @PathVariable clusterId: UUID,
        @PathVariable keyspaceName: String,
        @PathVariable tableName: String,
    ): ApiResponse<SchemaTableDetailResponse> = ApiResponse.success(schemaExplorerService.tableDetail(clusterId, keyspaceName, tableName).toResponse())

    @DeleteMapping("/keyspaces/{keyspaceName}/tables/{tableName}")
    fun dropTable(
        @PathVariable clusterId: UUID,
        @PathVariable keyspaceName: String,
        @PathVariable tableName: String,
        @RequestBody request: SchemaDangerousActionRequestDto,
    ): ApiResponse<SchemaDangerousActionResponse> =
        ApiResponse.success(schemaExplorerService.dropTable(clusterId, keyspaceName, tableName, request.toCoreRequest()).toResponse())

    @PostMapping("/keyspaces/{keyspaceName}/tables/{tableName}/truncate")
    fun truncateTable(
        @PathVariable clusterId: UUID,
        @PathVariable keyspaceName: String,
        @PathVariable tableName: String,
        @RequestBody request: SchemaDangerousActionRequestDto,
    ): ApiResponse<SchemaDangerousActionResponse> =
        ApiResponse.success(schemaExplorerService.truncateTable(clusterId, keyspaceName, tableName, request.toCoreRequest()).toResponse())

    @GetMapping("/keyspaces/{keyspaceName}/tables/{tableName}/columns")
    fun columns(
        @PathVariable clusterId: UUID,
        @PathVariable keyspaceName: String,
        @PathVariable tableName: String,
    ) = ApiResponse.success(schemaExplorerService.listColumns(clusterId, keyspaceName, tableName).map { it.toResponse() })

    @PutMapping("/keyspaces/{keyspaceName}/tables/{tableName}/catalog")
    fun updateTableCatalog(
        @PathVariable clusterId: UUID,
        @PathVariable keyspaceName: String,
        @PathVariable tableName: String,
        @RequestBody request: UpdateTableCatalogRequest,
    ): ApiResponse<TableCatalogResponse> =
        ApiResponse.success(schemaExplorerService.updateTableCatalog(clusterId, keyspaceName, tableName, request.toCoreRequest(), request.actor).toResponse())

    @PutMapping("/keyspaces/{keyspaceName}/tables/{tableName}/columns/{columnName}/catalog")
    fun updateColumnCatalog(
        @PathVariable clusterId: UUID,
        @PathVariable keyspaceName: String,
        @PathVariable tableName: String,
        @PathVariable columnName: String,
        @RequestBody request: UpdateColumnCatalogRequest,
    ): ApiResponse<ColumnCatalogResponse> =
        ApiResponse.success(schemaExplorerService.updateColumnCatalog(clusterId, keyspaceName, tableName, columnName, request.toCoreRequest(), request.actor).toResponse())

    @GetMapping("/keyspaces/{keyspaceName}/types")
    fun userTypes(
        @PathVariable clusterId: UUID,
        @PathVariable keyspaceName: String,
    ): ApiResponse<List<SchemaUserTypeSummaryResponse>> =
        ApiResponse.success(schemaExplorerService.listUserTypes(clusterId, keyspaceName).map { it.toResponse() })

    @GetMapping("/keyspaces/{keyspaceName}/types/{typeName}")
    fun userType(
        @PathVariable clusterId: UUID,
        @PathVariable keyspaceName: String,
        @PathVariable typeName: String,
    ): ApiResponse<SchemaUserTypeDetailResponse> = ApiResponse.success(schemaExplorerService.userTypeDetail(clusterId, keyspaceName, typeName).toResponse())

    @GetMapping("/keyspaces/{keyspaceName}/history")
    fun history(
        @PathVariable clusterId: UUID,
        @PathVariable keyspaceName: String,
        @RequestParam(required = false) tableName: String?,
    ): ApiResponse<List<SchemaChangeResponse>> =
        ApiResponse.success(schemaExplorerService.history(clusterId, keyspaceName, tableName).map { it.toResponse() })
}
