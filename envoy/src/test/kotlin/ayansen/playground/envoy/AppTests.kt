package ayansen.playground.envoy

import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest
import io.envoyproxy.envoy.service.endpoint.v3.EndpointDiscoveryServiceGrpc
import io.grpc.ManagedChannelBuilder
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertNotNull
import kotlin.test.fail

@SpringBootTest
class AppTests {

    @Test
    fun contextLoads() {
    }
}
