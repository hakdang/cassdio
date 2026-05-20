package kr.hakdang.cassdio.core.metadata.schema

import java.time.Instant
import java.util.UUID

data class SchemaKeyspace(
    val name: String,
    val durableWrites: Boolean,
    val replication: Map<String, String>,
    val system: Boolean,
    val queryable: Boolean,
    val tableCount: Int,
    val userTypeCount: Int,
    val describeCql: String,
)

data class SchemaTableSummary(
    val keyspaceName: String,
    val name: String,
    val kind: SchemaTableKind,
    val comment: String?,
    val catalog: TableCatalogMetadata?,
)

data class SchemaTablePage(
    val keyspaceName: String,
    val items: List<SchemaTableSummary>,
    val nextCursor: String?,
)

data class SchemaTableDetail(
    val keyspaceName: String,
    val name: String,
    val kind: SchemaTableKind,
    val columns: List<SchemaColumn>,
    val indexes: List<SchemaIndex>,
    val views: List<SchemaView>,
    val options: Map<String, String>,
    val createStatement: String,
    val catalog: TableCatalogMetadata?,
)

data class SchemaColumn(
    val name: String,
    val type: String,
    val kind: SchemaColumnKind,
    val position: Int,
    val clusteringOrder: String?,
    val catalog: ColumnCatalogMetadata?,
    val userTypeLink: SchemaUserTypeLink?,
)

data class SchemaIndex(
    val name: String,
    val tableName: String,
    val kind: String?,
    val target: String?,
    val options: Map<String, String>,
)

data class SchemaView(
    val name: String,
    val baseTableName: String,
    val whereClause: String?,
    val includeAllColumns: Boolean,
)

data class SchemaUserTypeSummary(
    val keyspaceName: String,
    val name: String,
    val fieldCount: Int,
)

data class SchemaUserTypeDetail(
    val keyspaceName: String,
    val name: String,
    val fields: List<SchemaUserTypeField>,
    val createStatement: String,
)

data class SchemaUserTypeField(
    val name: String,
    val type: String,
    val position: Int,
)

data class SchemaUserTypeLink(
    val keyspaceName: String,
    val typeName: String,
)

data class TableCatalogMetadata(
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

data class ColumnCatalogMetadata(
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

data class TableCatalogUpdate(
    val owner: String?,
    val escalationContact: String?,
    val description: String?,
    val dataFreshness: String?,
    val retentionPolicy: String?,
    val accessPattern: String?,
    val tags: Set<String>,
)

data class ColumnCatalogUpdate(
    val description: String?,
    val sensitive: Boolean,
    val maskingPolicy: String?,
    val exportPolicy: String?,
    val tags: Set<String>,
)

data class SchemaChangeRecord(
    val changeId: UUID,
    val clusterId: UUID,
    val keyspaceName: String,
    val tableName: String?,
    val changeType: SchemaChangeType,
    val actor: String,
    val beforeCql: String?,
    val afterCql: String?,
    val diff: List<String>,
    val details: Map<String, String>,
    val createdAt: Instant,
)

data class SchemaDangerousActionRequest(
    val confirmation: String,
    val actor: String = "System",
)

data class SchemaDangerousActionResult(
    val accepted: Boolean,
    val workflowRequired: Boolean,
    val message: String,
    val change: SchemaChangeRecord?,
)

enum class SchemaTableKind {
    TABLE,
    MATERIALIZED_VIEW,
}

enum class SchemaColumnKind {
    PARTITION_KEY,
    CLUSTERING,
    STATIC,
    REGULAR,
}

enum class SchemaChangeType {
    DROP_KEYSPACE,
    DROP_TABLE,
    TRUNCATE_TABLE,
    CATALOG_UPDATED,
}
