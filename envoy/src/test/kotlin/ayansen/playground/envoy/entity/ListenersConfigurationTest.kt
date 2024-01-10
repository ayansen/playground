package ayansen.playground.envoy.entity

import ayansen.playground.envoy.Fixtures.parseYamlFile
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class ListenersConfigurationTest {

    @Test
    fun `toProtoListeners method returns a list of Listener protobuf objects when given a list of ListenerConfiguration objects`() {
        // Given
        val listeners: ListenersConfiguration =
            parseYamlFile(File("src/test/resources/listeners/multiple_http_listeners.yaml"))


        // When
        val result = listeners.toProtoListeners()

        // Then
        assertEquals(2, result.size)
        listeners.listeners.forEachIndexed { index, listener ->
            assertEquals(listener.name, result[index].name)
            assertEquals(
                listener.socketAddress.address,
                result[index].address.socketAddress.address
            )
            assertEquals(result[index].filterChainsCount, 1)
            assertEquals(
                listener.socketAddress.port,
                result[index].address.socketAddress.portValue
            )
        }
    }
}