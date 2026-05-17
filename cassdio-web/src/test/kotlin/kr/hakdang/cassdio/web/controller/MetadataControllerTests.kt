package kr.hakdang.cassdio.web.controller

import kr.hakdang.cassdio.core.error.ErrorMessageResolver
import kr.hakdang.cassdio.core.error.GlobalExceptionHandler
import kr.hakdang.cassdio.core.metadata.bootstrap.MetadataStatus
import kr.hakdang.cassdio.core.metadata.bootstrap.MetadataStatusService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.util.UUID

@WebMvcTest(MetadataController::class)
@AutoConfigureRestDocs
@Import(GlobalExceptionHandler::class, ErrorMessageResolver::class)
class MetadataControllerTests(
    @Autowired private val mockMvc: MockMvc,
) {
    @MockitoBean
    private lateinit var metadataStatusService: MetadataStatusService

    @Test
    fun `metadata status returns connection state without credentials`() {
        val installationId = UUID.fromString("018f84e3-7f87-7ac3-9c41-7c6e8c5d9d01")
        `when`(metadataStatusService.status())
            .thenReturn(
                MetadataStatus(
                    configured = true,
                    source = "APPLICATION_CONFIG",
                    keyspace = "cassdio_meta",
                    connected = true,
                    schemaVersion = "202602010001",
                    bootstrapCompleted = true,
                    installationId = installationId,
                ),
            )

        mockMvc
            .get("/api/metadata/status")
            .andExpect {
                status { isOk() }
                jsonPath("$.code") { value("200") }
                jsonPath("$.message") { value("Success") }
                jsonPath("$.data.configured") { value(true) }
                jsonPath("$.data.source") { value("APPLICATION_CONFIG") }
                jsonPath("$.data.keyspace") { value("cassdio_meta") }
                jsonPath("$.data.connected") { value(true) }
                jsonPath("$.data.schemaVersion") { value("202602010001") }
                jsonPath("$.data.bootstrapCompleted") { value(true) }
                jsonPath("$.data.installationId") { value(installationId.toString()) }
                jsonPath("$.data.username") { doesNotExist() }
                jsonPath("$.data.password") { doesNotExist() }
            }.andDo {
                handle(
                    document(
                        "metadata-status",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                            fieldWithPath("code").description("응답 코드"),
                            fieldWithPath("message").description("응답 메시지"),
                            fieldWithPath("data.configured").description("Metadata DB 설정 여부"),
                            fieldWithPath("data.source").description("Metadata DB 설정 출처"),
                            fieldWithPath("data.keyspace").description("Metadata DB keyspace"),
                            fieldWithPath("data.connected").description("Metadata DB 연결 가능 여부"),
                            fieldWithPath("data.schemaVersion").description("적용된 metadata schema version").optional(),
                            fieldWithPath("data.bootstrapCompleted").description("Metadata bootstrap 완료 여부"),
                            fieldWithPath("data.installationId").description("Cassdio installation ID").optional(),
                            fieldWithPath("timestamp").description("응답 생성 시각(epoch seconds)"),
                        ),
                    ),
                )
            }
    }
}
