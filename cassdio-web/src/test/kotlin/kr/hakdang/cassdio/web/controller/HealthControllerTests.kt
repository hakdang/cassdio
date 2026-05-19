package kr.hakdang.cassdio.web.controller

import kr.hakdang.cassdio.core.error.ErrorMessageResolver
import kr.hakdang.cassdio.core.error.GlobalExceptionHandler
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(HealthController::class)
@AutoConfigureRestDocs
@Import(GlobalExceptionHandler::class, ErrorMessageResolver::class)
class HealthControllerTests(
    @Autowired private val mockMvc: MockMvc,
) {
    @Test
    fun `health returns standard api response`() {
        mockMvc
            .get("/api/health")
            .andExpect {
                status { isOk() }
                jsonPath("$.code") { value("200") }
                jsonPath("$.message") { value("Success") }
                jsonPath("$.data.status") { value("UP") }
                jsonPath("$.data.service") { value("cassdio-web") }
            }.andDo {
                handle(
                    document(
                        "health",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                            fieldWithPath("code").description("응답 코드"),
                            fieldWithPath("message").description("응답 메시지"),
                            fieldWithPath("data.status").description("서비스 상태"),
                            fieldWithPath("data.service").description("서비스 이름"),
                            fieldWithPath("data.timestamp").description("상태 생성 시각"),
                            fieldWithPath("timestamp").description("응답 생성 시각(epoch seconds)"),
                        ),
                    ),
                )
            }
    }

    @Test
    fun `version returns standard api response`() {
        mockMvc
            .get("/api/version")
            .andExpect {
                status { isOk() }
                jsonPath("$.code") { value("200") }
                jsonPath("$.message") { value("Success") }
                jsonPath("$.data.name") { value("cassdio-web") }
                jsonPath("$.data.version") { value("0.1.0") }
            }.andDo {
                handle(
                    document(
                        "version",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                            fieldWithPath("code").description("응답 코드"),
                            fieldWithPath("message").description("응답 메시지"),
                            fieldWithPath("data.name").description("애플리케이션 이름"),
                            fieldWithPath("data.version").description("애플리케이션 버전"),
                            fieldWithPath("timestamp").description("응답 생성 시각(epoch seconds)"),
                        ),
                    ),
                )
            }
    }
}
