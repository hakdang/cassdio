package kr.hakdang.cassdio.web.controller

import kr.hakdang.cassdio.core.error.ErrorMessageResolver
import kr.hakdang.cassdio.core.error.GlobalExceptionHandler
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.i18n.FixedLocaleResolver
import java.util.Locale

@WebMvcTest(
    controllers = [CommonErrorDocumentationController::class],
    properties = [
        "spring.web.locale=ko",
        "spring.web.locale-resolver=fixed",
    ],
)
@AutoConfigureRestDocs
@Import(GlobalExceptionHandler::class, ErrorMessageResolver::class, CommonErrorDocumentationTestConfig::class)
class CommonErrorDocumentationTests(
    @Autowired private val mockMvc: MockMvc,
) {
    @Test
    fun `validation failure returns standard error response`() {
        mockMvc
            .post("/api/test/validation") {
                contentType = MediaType.APPLICATION_JSON
                header("Accept-Language", "ko")
                content = """{"name":""}"""
            }.andExpect {
                status { isBadRequest() }
                jsonPath("$.code") { value("E001") }
                jsonPath("$.message") { value("잘못된 요청입니다") }
                jsonPath("$.errors[0].field") { value("name") }
            }.andDo {
                handle(
                    document(
                        "error-validation",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                            fieldWithPath("name").description("검증 대상 이름"),
                        ),
                        responseFields(
                            fieldWithPath("code").description("에러 코드"),
                            fieldWithPath("message").description("에러 메시지"),
                            fieldWithPath("errors[].field").description("검증 실패 필드"),
                            fieldWithPath("errors[].message").description("검증 실패 메시지"),
                            fieldWithPath("errors[].rejectedValue").description("거부된 값").optional(),
                            fieldWithPath("traceId").description("추적 ID"),
                            fieldWithPath("timestamp").description("응답 생성 시각(epoch seconds)"),
                        ),
                    ),
                )
            }
    }

    @Test
    fun `unauthorized failure returns standard error response`() {
        mockMvc
            .get("/api/test/unauthorized")
            .andExpect {
                status { isUnauthorized() }
                jsonPath("$.code") { value("E002") }
                jsonPath("$.message") { value("Unauthorized") }
            }.andDo {
                handle(
                    document(
                        "error-unauthorized",
                        preprocessResponse(prettyPrint()),
                        commonErrorResponseFields(),
                    ),
                )
            }
    }

    @Test
    fun `not found failure returns standard error response`() {
        mockMvc
            .get("/api/test/not-found")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.code") { value("E004") }
                jsonPath("$.message") { value("Resource not found") }
            }
    }

    private fun commonErrorResponseFields() =
        responseFields(
            fieldWithPath("code").description("에러 코드"),
            fieldWithPath("message").description("에러 메시지"),
            fieldWithPath("traceId").description("추적 ID"),
            fieldWithPath("timestamp").description("응답 생성 시각(epoch seconds)"),
        )
}

@TestConfiguration
class CommonErrorDocumentationTestConfig {
    @Bean
    fun localeResolver(): LocaleResolver = FixedLocaleResolver(Locale.KOREAN)
}
