package kr.hakdang.cassdio.web

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = ["kr.hakdang.cassdio"],
    exclude = [CassandraAutoConfiguration::class],
)
class CassdioWebApplication

fun main(args: Array<String>) {
    runApplication<CassdioWebApplication>(*args)
}
