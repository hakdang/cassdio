package kr.hakdang.cassdio.core.api

data class FieldErrorResponse(
    val field: String,
    val message: String,
    val rejectedValue: String? = null,
)
