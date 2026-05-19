package kr.hakdang.cassdio.core.metadata.bootstrap

internal fun String.cqlLiteral(): String = "'${replace("'", "''")}'"

internal fun Boolean.cqlLiteral(): String = toString()

internal fun Iterable<String>.cqlListLiteral(): String = joinToString(separator = ", ", prefix = "[", postfix = "]") { it.cqlLiteral() }

internal fun Iterable<String>.cqlSetLiteral(): String = joinToString(separator = ", ", prefix = "{", postfix = "}") { it.cqlLiteral() }

internal fun Map<String, String>.cqlMapLiteral(): String =
    entries.joinToString(separator = ", ", prefix = "{", postfix = "}") { (key, value) ->
        "${key.cqlLiteral()}: ${value.cqlLiteral()}"
    }
