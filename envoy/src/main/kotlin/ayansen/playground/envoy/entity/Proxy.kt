package ayansen.playground.envoy.entity

import io.envoyproxy.envoy.config.core.v3.Address
import io.envoyproxy.envoy.config.endpoint.v3.ClusterLoadAssignment
import io.envoyproxy.envoy.config.endpoint.v3.Endpoint
import io.envoyproxy.envoy.config.endpoint.v3.LbEndpoint
import io.envoyproxy.envoy.config.endpoint.v3.LocalityLbEndpoints


data class Proxy(
    val name: String,
    val domains: List<String>,
    val routes: List<Route>
) {

    data class Route(
        val match: Match,
        val cluster: Cluster,
        val mutations: Mutations

    )

    data class Match(
        val prefix: String
    )

    data class Mutations(
        val prefixRewrite: String
    )

    data class Cluster(
        val name: String,
        val connectTimeout: String,
        val type: String,
        val lbPolicy: String,
        val hosts: List<Host>
    )

    data class Host(
        val socketAddress: SocketAddress
    )

    data class SocketAddress(
        val address: String,
        val port: Int
    )

    fun toProtoRoute(): io.envoyproxy.envoy.config.route.v3.RouteConfiguration {
        return io.envoyproxy.envoy.config.route.v3.RouteConfiguration.newBuilder()
            .setName(name)
            .addAllVirtualHosts(
                domains.map { domain ->
                    io.envoyproxy.envoy.config.route.v3.VirtualHost.newBuilder()
                        .setName(domain)
                        .addAllDomains(listOf(domain))
                        .addAllRoutes(
                            routes.map { route ->
                                io.envoyproxy.envoy.config.route.v3.Route.newBuilder()
                                    .setMatch(
                                        io.envoyproxy.envoy.config.route.v3.RouteMatch.newBuilder()
                                            .setPrefix(route.match.prefix)
                                    )
                                    .setRoute(
                                        io.envoyproxy.envoy.config.route.v3.RouteAction.newBuilder()
                                            .setCluster(route.cluster.name)
                                    )
                                    .build()
                            }
                        )
                        .build()
                }
            )
            .build()
    }
    fun toProtoEndpoints(): List<ClusterLoadAssignment> {
        return routes.map {
            ClusterLoadAssignment.newBuilder()
                .setClusterName(it.cluster.name)
                .addAllEndpoints(
                    (it.cluster.hosts.map { host ->
                        LocalityLbEndpoints.newBuilder()
                            .addLbEndpoints(
                                LbEndpoint.newBuilder()
                                    .setEndpoint(
                                        Endpoint.newBuilder()
                                            .setAddress(
                                                Address.newBuilder()
                                                    .setSocketAddress(
                                                        io.envoyproxy.envoy.config.core.v3.SocketAddress.newBuilder()
                                                            .setAddress(host.socketAddress.address)
                                                            .setPortValue(host.socketAddress.port)
                                                    )
                                            )
                                    )
                            ).build()
                    })
                )
                .build()
        }
    }
    fun toProtoClusters(): List<io.envoyproxy.envoy.config.cluster.v3.Cluster> {
        return routes.map {
            io.envoyproxy.envoy.config.cluster.v3.Cluster.newBuilder()
                .setName(it.cluster.name)
                .setConnectTimeout(
                    com.google.protobuf.Duration.newBuilder()
                        .setSeconds(it.cluster.connectTimeout.toLong())
                )
                .setType(io.envoyproxy.envoy.config.cluster.v3.Cluster.DiscoveryType.valueOf(it.cluster.type))
                .setLbPolicy(io.envoyproxy.envoy.config.cluster.v3.Cluster.LbPolicy.valueOf(it.cluster.lbPolicy))
                .build()
        }
    }
}
