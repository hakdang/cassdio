package kr.hakdang.cassdio.core.identity

import java.time.Instant

internal fun String.cqlLiteral(): String = "'${replace("'", "''")}'"

internal fun String?.cqlNullableLiteral(): String = this?.cqlLiteral() ?: "null"

internal fun Boolean.cqlLiteral(): String = toString()

internal fun Instant.timestampLiteral(): String = toString().cqlLiteral()

internal fun Iterable<String>.cqlSetLiteral(): String = joinToString(separator = ", ", prefix = "{", postfix = "}") { it.cqlLiteral() }
