package kr.hakdang.cassdio.core.metadata.bootstrap

internal fun String.cqlLiteral(): String = "'${replace("'", "''")}'"

internal fun Boolean.cqlLiteral(): String = toString()
