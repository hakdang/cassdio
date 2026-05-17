package kr.hakdang.cassdio.web.dto

data class MetadataStatusResponse(
    val configured: Boolean,
    val source: String,
    val keyspace: String,
    val connected: Boolean,
    val schemaVersion: String?,
    val bootstrapCompleted: Boolean,
    val installationId: String?,
)
