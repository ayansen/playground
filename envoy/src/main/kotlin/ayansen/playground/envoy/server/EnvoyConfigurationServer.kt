import io.envoyproxy.controlplane.cache.v3.SimpleCache
import io.envoyproxy.controlplane.server.V3DiscoveryServer
import io.grpc.Server
import io.grpc.netty.NettyServerBuilder
import org.slf4j.LoggerFactory


/**
 * The EnvoyConfigurationServer class is responsible for starting a server that handles xDS (Discovery Service) requests from Envoy proxies. It uses a SimpleCache to store and retrieve configuration data.
 * Example Usage
    val simpleCache = SimpleCache<Any>()
    val server = EnvoyConfigurationServer(simpleCache)
    server.start()
    This code creates an instance of EnvoyConfigurationServer with a SimpleCache and starts the server. The server will listen for xDS requests on port 8000.


 * Main functionalities
    - The main functionalities of the EnvoyConfigurationServer class are:
    - Starting a server to handle xDS requests
    - Using a SimpleCache to store and retrieve configuration data
 * @property simpleCache
 */

class EnvoyConfigurationServer(private val simpleCache: SimpleCache<Any>) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(EnvoyConfigurationServer::class.java)
    }
    fun start() {
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