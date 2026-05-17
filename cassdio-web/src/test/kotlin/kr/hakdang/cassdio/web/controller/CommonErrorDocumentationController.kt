package kr.hakdang.cassdio.web.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kr.hakdang.cassdio.core.exception.NotFoundException
import kr.hakdang.cassdio.core.exception.UnauthorizedException
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/api/test")
class CommonErrorDocumentationController {
    @PostMapping("/validation")
    fun validation(
        @Valid @RequestBody request: ValidationTestRequest,
    ): ValidationTestRequest = request

    @GetMapping("/unauthorized")
    fun unauthorized(): Nothing = throw UnauthorizedException()

    @GetMapping("/not-found")
    fun notFound(): Nothing = throw NotFoundException()
}

data class ValidationTestRequest(
    @field:NotBlank
    @field:Size(max = 20)
    val name: String,
)
