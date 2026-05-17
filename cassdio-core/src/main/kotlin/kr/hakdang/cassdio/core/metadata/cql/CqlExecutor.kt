package kr.hakdang.cassdio.core.metadata.cql

interface CqlExecutor {
    fun execute(statement: String)

    fun queryOne(statement: String): CqlRow?
}

data class CqlRow(
    private val values: Map<String, Any?>,
) {
    fun string(name: String): String? = values[name]?.toString()

    fun boolean(name: String): Boolean? = values[name] as? Boolean

    fun int(name: String): Int? = values[name] as? Int
}
