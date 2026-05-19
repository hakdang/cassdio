package kr.hakdang.cassdio.core.api

import kr.hakdang.cassdio.core.error.ErrorCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ApiResponseTests {
    @Test
    fun `success wraps data with standard code and message`() {
        val response = ApiResponse.success(mapOf("status" to "UP"))

        assertEquals("200", response.code)
        assertEquals("Success", response.message)
        assertEquals(mapOf("status" to "UP"), response.data)
        assertNotNull(response.timestamp)
    }

    @Test
    fun `failure maps error code and message`() {
        val response = ApiResponse.failure(ErrorCode.INVALID_REQUEST)

        assertEquals("E001", response.code)
        assertEquals("Invalid request", response.message)
        assertEquals(null, response.data)
        assertNotNull(response.timestamp)
    }

    @Test
    fun `page response exposes pagination metadata`() {
        val response =
            PageResponse(
                content = listOf("a", "b"),
                page = 0,
                size = 2,
                totalElements = 5,
                totalPages = 3,
                first = true,
                last = false,
            )

        assertEquals(2, response.content.size)
        assertEquals(5, response.totalElements)
        assertEquals(3, response.totalPages)
    }
}
