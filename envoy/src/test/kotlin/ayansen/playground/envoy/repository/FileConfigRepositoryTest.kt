package ayansen.playground.envoy.repository

import io.envoyproxy.controlplane.cache.v3.SimpleCache
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class FileConfigRepositoryTest {

    // FileConfigRepository initializes successfully with a SimpleCache instance
    @Test
    fun `test file config repository initialization`() {
        // Given
        val simpleCache = SimpleCache<Any> { "key" }
        // When
        val fileConfigRepository = FileConfigRepository(simpleCache)

        // Then
        assertNotNull(fileConfigRepository)
    }
}