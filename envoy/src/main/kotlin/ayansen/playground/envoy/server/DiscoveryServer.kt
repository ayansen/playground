import io.envoyproxy.controlplane.cache.v3.SimpleCache
import io.envoyproxy.controlplane.server.V3DiscoveryServer
import io.grpc.Server
import io.grpc.netty.NettyServerBuilder
import org.slf4j.LoggerFactory


/**
 * The DiscoveryServer class is responsible for starting a gRPC server and initializing the necessary services for Envoy proxy configuration discovery.
Example Usage
val simpleCache = SimpleCache<Any>()
val discoveryServer = DiscoveryServer(simpleCache)
The code above creates an instance of SimpleCache and passes it to the DiscoveryServer constructor. It then starts the gRPC server and initializes the necessary services for Envoy proxy configuration discovery.
Code Analysis
Main functionalities
The main functionalities of the DiscoveryServer class are:
Starting a gRPC server on a specified port.
Initializing the services for aggregated discovery, cluster discovery, endpoint discovery, listener discovery, and route discovery.
 *
 * @property simpleCache
 */
class DiscoveryServer(private val simpleCache: SimpleCache<Any>) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(DiscoveryServer::class.java)
    }

    init {
        val v3DiscoveryServer = V3DiscoveryServer(simpleCache)
        val builder = NettyServerBuilder.forPort(8000)
            .addService(v3DiscoveryServer.aggregatedDiscoveryServiceImpl)
            .addService(v3DiscoveryServer.clusterDiscoveryServiceImpl)
            .addService(v3DiscoveryServer.endpointDiscoveryServiceImpl)
            .addService(v3DiscoveryServer.listenerDiscoveryServiceImpl)
            .addService(v3DiscoveryServer.routeDiscoveryServiceImpl)
        val server: Server = builder.build()
        server.start()
        LOGGER.info("xds Server has started on port " + server.port)
        Runtime.getRuntime().addShutdownHook(Thread(server::shutdown))
    }
}