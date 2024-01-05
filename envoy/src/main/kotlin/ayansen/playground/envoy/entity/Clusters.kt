package ayansen.playground.envoy.entity

import io.envoyproxy.envoy.config.core.v3.Address
import io.envoyproxy.envoy.config.endpoint.v3.ClusterLoadAssignment
import io.envoyproxy.envoy.config.endpoint.v3.Endpoint
import io.envoyproxy.envoy.config.endpoint.v3.LbEndpoint
import io.envoyproxy.envoy.config.endpoint.v3.LocalityLbEndpoints

//kotlin class to deserialize clusters.yaml file  into a kotlin object
data class Clusters(
    val clusters: List<Cluster>
) {
    //converts kotlin object to envoy proto object
    fun toProtoClusters(): List<io.envoyproxy.envoy.config.cluster.v3.Cluster> {
        return clusters.map {
            io.envoyproxy.envoy.config.cluster.v3.Cluster.newBuilder()
                .setName(it.name)
                .setConnectTimeout(
                    com.google.protobuf.Duration.newBuilder()
                        .setSeconds(it.connectTimeout.toLong())
                )
                .setType(io.envoyproxy.envoy.config.cluster.v3.Cluster.DiscoveryType.valueOf(it.type))
                .setLbPolicy(io.envoyproxy.envoy.config.cluster.v3.Cluster.LbPolicy.valueOf(it.lbPolicy))
                .build()
        }
    }
    fun toProtoEndpoints(): List<ClusterLoadAssignment> {
        return clusters.map {
            ClusterLoadAssignment.newBuilder()
                .setClusterName(it.name)
                .addAllEndpoints(
                    (it.hosts.map { host ->
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

}