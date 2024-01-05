package ayansen.playground.envoy.entity

// kotlin class to deserialize routes.yaml file  into a kotlin object
data class Routes(
    val virtualHosts: List<VirtualHost>
) {

    fun toProtoRoutes() : List<io.envoyproxy.envoy.config.route.v3.RouteConfiguration> {
        return virtualHosts.map {
            io.envoyproxy.envoy.config.route.v3.RouteConfiguration.newBuilder()
                .setName(it.name)
                .addAllVirtualHosts(
                    it.domains.map { domain ->
                        io.envoyproxy.envoy.config.route.v3.VirtualHost.newBuilder()
                            .setName(domain)
                            .addAllDomains(listOf(domain))
                            .addAllRoutes(
                                it.routes.map { route ->
                                    io.envoyproxy.envoy.config.route.v3.Route.newBuilder()
                                        .setMatch(
                                            io.envoyproxy.envoy.config.route.v3.RouteMatch.newBuilder()
                                                .setPrefix(route.match.prefix)
                                        )
                                        .setRoute(
                                            io.envoyproxy.envoy.config.route.v3.RouteAction.newBuilder()
                                                .setCluster(route.cluster)
                                        )
                                        .build()
                                }
                            )
                            .build()
                    }
                )
                .build()
        }
    }
    data class VirtualHost(
        val name: String,
        val domains: List<String>,
        val routes: List<Route>
    )

    data class Route(
        val match: Match,
        val cluster: String,
        val mutations: Mutations

    )

    data class Match(
        val prefix: String
    )

    data class Mutations(
        val prefixRewrite: String
    )
}

