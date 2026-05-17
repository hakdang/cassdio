package kr.hakdang.cassdio.web.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(HealthController::class)
class HealthControllerTests(
    @Autowired private val mockMvc: MockMvc,
) {
    @Test
    fun `health returns up status`() {
        mockMvc
            .get("/api/health")
            .andExpect {
                status { isOk() }
                jsonPath("$.status") { value("UP") }
                jsonPath("$.service") { value("cassdio-web") }
            }
    }

    @Test
    fun `version returns application version`() {
        mockMvc
            .get("/api/version")
            .andExpect {
                status { isOk() }
                jsonPath("$.name") { value("cassdio-web") }
                jsonPath("$.version") { value("0.1.0") }
            }
    }
}
