package kr.hakdang.cassdio.core.metadata.config

fun interface MetadataDbConfigProvider {
    fun getConfig(): MetadataDbConfig
}
