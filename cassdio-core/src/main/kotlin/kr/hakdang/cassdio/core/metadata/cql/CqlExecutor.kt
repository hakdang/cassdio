package kr.hakdang.cassdio.core.metadata.cql

interface CqlExecutor {
    fun execute(statement: String)

    fun queryOne(statement: String): CqlRow?

    fun query(statement: String): List<CqlRow> = queryOne(statement)?.let(::listOf) ?: emptyList()
}

data class CqlRow(
    private val values: Map<String, Any?>,
) {
    fun string(name: String): String? = values[name]?.toString()

    fun boolean(name: String): Boolean? = values[name] as? Boolean

    fun int(name: String): Int? = values[name] as? Int

    fun uuid(name: String): java.util.UUID? =
        when (val value = values[name]) {
            is java.util.UUID -> value
            is String -> java.util.UUID.fromString(value)
            else -> null
        }

    fun instant(name: String): java.time.Instant? =
        when (val value = values[name]) {
            is java.time.Instant -> value
            is java.util.Date -> value.toInstant()
            is String -> java.time.Instant.parse(value)
            else -> null
        }

    @Suppress("UNCHECKED_CAST")
    fun stringMap(name: String): Map<String, String>? = values[name] as? Map<String, String>

    @Suppress("UNCHECKED_CAST")
    fun stringList(name: String): List<String>? = values[name] as? List<String>

    @Suppress("UNCHECKED_CAST")
    fun stringSet(name: String): Set<String>? = values[name] as? Set<String>
}
