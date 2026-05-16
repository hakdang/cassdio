package kr.hakdang.cassdio.core

import kotlin.test.Test
import kotlin.test.assertNotNull

class CoreModuleTests {
    @Test
    fun `core module marker can be created`() {
        assertNotNull(CoreModule())
    }
}
