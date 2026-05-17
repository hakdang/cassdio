package kr.hakdang.cassdio.core.error

import org.slf4j.event.Level

enum class ErrorLoggingPolicy(
    val level: Level,
) {
    CLIENT_ERROR(Level.WARN),
    SERVER_ERROR(Level.ERROR),
}
