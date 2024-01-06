package ayansen.playground.envoy.provider

import ayansen.playground.envoy.entity.ListenersConfiguration
import io.envoyproxy.controlplane.cache.v3.SimpleCache
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class FileProxyProviderTests {

    // FileConfigRepository initializes successfully with a SimpleCache instance
    @Test
    fun `test file config repository initialization`() {
        // Given
        val simpleCache = SimpleCache<Any> { "key" }
        val listenersConfiguration = ListenersConfiguration(emptyList())
        // When
        val fileConfigRepository = FileProxyProvider("./configs", simpleCache,listenersConfiguration)

        // Then
        assertNotNull(fileConfigRepository)
    }
}