package ayansen.playground.envoy.entity

import io.envoyproxy.envoy.config.listener.v3.FilterChain
import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "envoy-listeners")
data class ListenersConfiguration(
    var listeners: List<Listener> = emptyList()
) {

    fun toProtoListeners(): List<io.envoyproxy.envoy.config.listener.v3.Listener> {
        return  listeners.map {
            io.envoyproxy.envoy.config.listener.v3.Listener.newBuilder()
                .setName(it.name)
                .setAddress(
                    io.envoyproxy.envoy.config.core.v3.Address.newBuilder()
                        .setSocketAddress(
                            io.envoyproxy.envoy.config.core.v3.SocketAddress.newBuilder()
                                .setAddress(it.socketAddress.address)
                                .setPortValue(it.socketAddress.port)
                        )
                ).addFilterChains(
                    FilterChain.newBuilder().addFilters(
                        io.envoyproxy.envoy.config.listener.v3.Filter.newBuilder()
                            .setName("envoy.filters.network.http_connection_manager")
                            .setTypedConfig(
                                com.google.protobuf.Any.pack(
                                    io.envoyproxy.envoy.extensions.filters.network.http_connection_manager.v3.HttpConnectionManager.newBuilder()
                                        .setStatPrefix(it.name)
                                        .setCodecType(io.envoyproxy.envoy.extensions.filters.network.http_connection_manager.v3.HttpConnectionManager.CodecType.AUTO)
                                        .build()
                                )
                            )

                    )
                )
                .build()
        }
    }
    data class Listener(
        val name: String,
        val socketAddress: SocketAddress
    )


    data class SocketAddress(
        val address: String,
        val port: Int
    )
}