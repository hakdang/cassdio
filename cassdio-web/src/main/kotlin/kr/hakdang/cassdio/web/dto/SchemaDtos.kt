package kr.hakdang.cassdio.web.dto

import kr.hakdang.cassdio.core.metadata.schema.ColumnCatalogMetadata
import kr.hakdang.cassdio.core.metadata.schema.ColumnCatalogUpdate
import kr.hakdang.cassdio.core.metadata.schema.SchemaChangeRecord
import kr.hakdang.cassdio.core.metadata.schema.SchemaColumn
import kr.hakdang.cassdio.core.metadata.schema.SchemaDangerousActionRequest
import kr.hakdang.cassdio.core.metadata.schema.SchemaDangerousActionResult
import kr.hakdang.cassdio.core.metadata.schema.SchemaIndex
import kr.hakdang.cassdio.core.metadata.schema.SchemaKeyspace
import kr.hakdang.cassdio.core.metadata.schema.SchemaTableDetail
import kr.hakdang.cassdio.core.metadata.schema.SchemaTablePage
import kr.hakdang.cassdio.core.metadata.schema.SchemaTableSummary
import kr.hakdang.cassdio.core.metadata.schema.SchemaUserTypeDetail
import kr.hakdang.cassdio.core.metadata.schema.SchemaUserTypeField
import kr.hakdang.cassdio.core.metadata.schema.SchemaUserTypeLink
import kr.hakdang.cassdio.core.metadata.schema.SchemaUserTypeSummary
import kr.hakdang.cassdio.core.metadata.schema.SchemaView
import kr.hakdang.cassdio.core.metadata.schema.TableCatalogMetadata
import kr.hakdang.cassdio.core.metadata.schema.TableCatalogUpdate
import java.time.Instant
import java.util.UUID

data class SchemaKeyspaceResponse(
    val name: String,
    val durableWrites: Boolean,
    val replication: Map<String, String>,
    val system: Boolean,
    val queryable: Boolean,
    val tableCount: Int,
    val userTypeCount: Int,
    val describeCql: String,
)

data class SchemaTablePageResponse(
    val keyspaceName: String,
    val items: List<SchemaTableSummaryResponse>,
    val nextCursor: String?,
)

data class SchemaTableSummaryResponse(
    val keyspaceName: String,
    val name: String,
    val kind: String,
    val comment: String?,
    val catalog: TableCatalogResponse?,
)

data class SchemaTableDetailResponse(
    val keyspaceName: String,
    val name: String,
    val kind: String,
    val columns: List<SchemaColumnResponse>,
    val indexes: List<SchemaIndexResponse>,
    val views: List<SchemaViewResponse>,
    val options: Map<String, String>,
    val createStatement: String,
    val catalog: TableCatalogResponse?,
)

data class SchemaColumnResponse(
    val name: String,
    val type: String,
    val kind: String,
    val position: Int,
    val clusteringOrder: String?,
    val catalog: ColumnCatalogResponse?,
    val userTypeLink: SchemaUserTypeLinkResponse?,
)

data class SchemaIndexResponse(
    val name: String,
    val tableName: String,
    val kind: String?,
    val target: String?,
    val options: Map<String, String>,
)

data class SchemaViewResponse(
    val name: String,
    val baseTableName: String,
    val whereClause: String?,
    val includeAllColumns: Boolean,
)

data class SchemaUserTypeSummaryResponse(
    val keyspaceName: String,
    val name: String,
    val fieldCount: Int,
)

data class SchemaUserTypeDetailResponse(
    val keyspaceName: String,
    val name: String,
    val fields: List<SchemaUserTypeFieldResponse>,
    val createStatement: String,
)

data class SchemaUserTypeFieldResponse(
    val name: String,
    val type: String,
    val position: Int,
)

data class SchemaUserTypeLinkResponse(
    val keyspaceName: String,
    val typeName: String,
)

data class TableCatalogResponse(
    val clusterId: UUID,
    val keyspaceName: String,
    val tableName: String,
    val owner: String?,
    val escalationContact: String?,
    val description: String?,
    val dataFreshness: String?,
    val retentionPolicy: String?,
    val accessPattern: String?,
    val tags: Set<String>,
    val updatedAt: Instant,
)

data class ColumnCatalogResponse(
    val clusterId: UUID,
    val keyspaceName: String,
    val tableName: String,
    val columnName: String,
    val description: String?,
    val sensitive: Boolean,
    val maskingPolicy: String?,
    val exportPolicy: String?,
    val tags: Set<String>,
    val updatedAt: Instant,
)

data class UpdateTableCatalogRequest(
    val owner: String? = null,
    val escalationContact: String? = null,
    val description: String? = null,
    val dataFreshness: String? = null,
    val retentionPolicy: String? = null,
    val accessPattern: String? = null,
    val tags: Set<String> = emptySet(),
    val actor: String = "System",
)

data class UpdateColumnCatalogRequest(
    val description: String? = null,
    val sensitive: Boolean = false,
    val maskingPolicy: String? = null,
    val exportPolicy: String? = null,
    val tags: Set<String> = emptySet(),
    val actor: String = "System",
)

data class SchemaDangerousActionRequestDto(
    val confirmation: String,
    val actor: String = "System",
)

data class SchemaDangerousActionResponse(
    val accepted: Boolean,
    val workflowRequired: Boolean,
    val message: String,
    val change: SchemaChangeResponse?,
)

data class SchemaChangeResponse(
    val changeId: UUID,
    val clusterId: UUID,
    val keyspaceName: String,
    val tableName: String?,
    val changeType: String,
    val actor: String,
    val beforeCql: String?,
    val afterCql: String?,
    val diff: List<String>,
    val details: Map<String, String>,
    val createdAt: Instant,
)

fun SchemaKeyspace.toResponse(): SchemaKeyspaceResponse =
    SchemaKeyspaceResponse(name, durableWrites, replication, system, queryable, tableCount, userTypeCount, describeCql)

fun SchemaTablePage.toResponse(): SchemaTablePageResponse = SchemaTablePageResponse(keyspaceName, items.map { it.toResponse() }, nextCursor)

fun SchemaTableSummary.toResponse(): SchemaTableSummaryResponse =
    SchemaTableSummaryResponse(keyspaceName, name, kind.name, comment, catalog?.toResponse())

fun SchemaTableDetail.toResponse(): SchemaTableDetailResponse =
    SchemaTableDetailResponse(
        keyspaceName = keyspaceName,
        name = name,
        kind = kind.name,
        columns = columns.map { it.toResponse() },
        indexes = indexes.map { it.toResponse() },
        views = views.map { it.toResponse() },
        options = options,
        createStatement = createStatement,
        catalog = catalog?.toResponse(),
    )

fun SchemaColumn.toResponse(): SchemaColumnResponse =
    SchemaColumnResponse(name, type, kind.name, position, clusteringOrder, catalog?.toResponse(), userTypeLink?.toResponse())

fun SchemaIndex.toResponse(): SchemaIndexResponse = SchemaIndexResponse(name, tableName, kind, target, options)

fun SchemaView.toResponse(): SchemaViewResponse = SchemaViewResponse(name, baseTableName, whereClause, includeAllColumns)

fun SchemaUserTypeSummary.toResponse(): SchemaUserTypeSummaryResponse = SchemaUserTypeSummaryResponse(keyspaceName, name, fieldCount)

fun SchemaUserTypeDetail.toResponse(): SchemaUserTypeDetailResponse =
    SchemaUserTypeDetailResponse(keyspaceName, name, fields.map { it.toResponse() }, createStatement)

fun SchemaUserTypeField.toResponse(): SchemaUserTypeFieldResponse = SchemaUserTypeFieldResponse(name, type, position)

fun SchemaUserTypeLink.toResponse(): SchemaUserTypeLinkResponse = SchemaUserTypeLinkResponse(keyspaceName, typeName)

fun TableCatalogMetadata.toResponse(): TableCatalogResponse =
    TableCatalogResponse(
        clusterId,
        keyspaceName,
        tableName,
        owner,
        escalationContact,
        description,
        dataFreshness,
        retentionPolicy,
        accessPattern,
        tags,
        updatedAt,
    )

fun ColumnCatalogMetadata.toResponse(): ColumnCatalogResponse =
    ColumnCatalogResponse(
        clusterId,
        keyspaceName,
        tableName,
        columnName,
        description,
        sensitive,
        maskingPolicy,
        exportPolicy,
        tags,
        updatedAt,
    )

fun UpdateTableCatalogRequest.toCoreRequest(): TableCatalogUpdate =
    TableCatalogUpdate(owner, escalationContact, description, dataFreshness, retentionPolicy, accessPattern, tags)

fun UpdateColumnCatalogRequest.toCoreRequest(): ColumnCatalogUpdate =
    ColumnCatalogUpdate(description, sensitive, maskingPolicy, exportPolicy, tags)

fun SchemaDangerousActionRequestDto.toCoreRequest(): SchemaDangerousActionRequest = SchemaDangerousActionRequest(confirmation, actor)

fun SchemaDangerousActionResult.toResponse(): SchemaDangerousActionResponse =
    SchemaDangerousActionResponse(accepted, workflowRequired, message, change?.toResponse())

fun SchemaChangeRecord.toResponse(): SchemaChangeResponse =
    SchemaChangeResponse(
        changeId,
        clusterId,
        keyspaceName,
        tableName,
        changeType.name,
        actor,
        beforeCql,
        afterCql,
        diff,
        details,
        createdAt,
    )
