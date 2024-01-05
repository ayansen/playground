package ayansen.playground.envoy.provider

import ayansen.playground.envoy.FileProviderConfiguration
import io.envoyproxy.controlplane.cache.v3.SimpleCache
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class FileConfigProviderTests {

    // FileConfigRepository initializes successfully with a SimpleCache instance
    @Test
    fun `test file config repository initialization`() {
        // Given
        val simpleCache = SimpleCache<Any> { "key" }
        val fileConfigProvider = FileProviderConfiguration(path = "./configs")
        // When
        val fileConfigRepository = FileConfigProvider(simpleCache,fileConfigProvider)

        // Then
        assertNotNull(fileConfigRepository)
    }
}