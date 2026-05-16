package kr.hakdang.cassdio.web.dto

import java.time.Instant

data class HealthResponse(
    val status: String,
    val service: String,
    val timestamp: Instant = Instant.now(),
)
