package ayansen.playground.envoy.server

import io.envoyproxy.envoy.config.core.v3.Node
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryRequest
import io.envoyproxy.envoy.service.discovery.v3.DiscoveryResponse
import io.envoyproxy.envoy.service.endpoint.v3.EndpointDiscoveryServiceGrpc
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertNotNull
import kotlin.test.fail

@SpringBootTest
class DiscoveryServerTests {
    @Test
    fun `test endpoint discovery server returns a valid response`() {

        val client = EndpointDiscoveryServiceGrpc.newStub(
            ManagedChannelBuilder.forAddress("127.0.0.1", 8000).usePlaintext().build()
        )

        client.streamEndpoints(object :
            StreamObserver<DiscoveryResponse> {
            override fun onNext(value: DiscoveryResponse?) {
                assertNotNull(value)
            }

            override fun onError(t: Throwable?) {
                fail(t?.message)
            }

            override fun onCompleted() {
                println("completed")
            }

        })?.onNext(
            DiscoveryRequest.newBuilder().setTypeUrl(
                "type.googleapis.com/envoy.config.endpoint.v3.ClusterLoadAssignment"
            ).setNode(
                Node.newBuilder().setId("key").build(
                )
            ).build()
        )
    }
}