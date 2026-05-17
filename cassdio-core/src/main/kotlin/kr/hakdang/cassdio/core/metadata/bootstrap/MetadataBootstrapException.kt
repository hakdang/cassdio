package kr.hakdang.cassdio.core.metadata.bootstrap

class MetadataBootstrapException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

class MetadataBootstrapLockException(
    message: String,
) : RuntimeException(message)
