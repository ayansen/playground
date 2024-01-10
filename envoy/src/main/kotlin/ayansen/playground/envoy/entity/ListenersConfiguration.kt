package ayansen.playground.envoy.entity

import io.envoyproxy.envoy.config.accesslog.v3.AccessLog
import io.envoyproxy.envoy.config.core.v3.ApiConfigSource
import io.envoyproxy.envoy.config.listener.v3.FilterChain
import io.envoyproxy.envoy.extensions.filters.network.http_connection_manager.v3.HttpFilter
import io.envoyproxy.envoy.extensions.filters.network.http_connection_manager.v3.Rds
import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "envoy-listeners")
data class ListenersConfiguration(
    var listeners: List<Listener> = emptyList()
) {
    data class Listener(
        var name: String = "",
        var socketAddress: SocketAddress = SocketAddress()
    )


    data class SocketAddress(
        var address: String = "",
        var port: Int = 0
    )
    fun toProtoListeners(): List<io.envoyproxy.envoy.config.listener.v3.Listener> {
        val listeners =  listeners.map {
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
                                        .addHttpFilters(
                                            HttpFilter.newBuilder().setName("envoy.filters.http.router").build()
                                        )
                                        .addAccessLog(
                                            AccessLog.newBuilder().setName("envoy.access_loggers.file").setTypedConfig(
                                                com.google.protobuf.Any.pack(
                                                    io.envoyproxy.envoy.extensions.access_loggers.file.v3.FileAccessLog.newBuilder()
                                                        .setPath("/dev/stdout")
                                                        .build()
                                                )
                                            ).build()
                                        )
                                        .setRds(
                                            Rds.newBuilder()
                                                .setRouteConfigName("chained_envoy_hosts")
                                                .setConfigSource(
                                                    io.envoyproxy.envoy.config.core.v3.ConfigSource.newBuilder()
                                                        .setResourceApiVersion(io.envoyproxy.envoy.config.core.v3.ApiVersion.V3)
                                                        .setApiConfigSource(
                                                            ApiConfigSource.newBuilder()
                                                                .setApiType(ApiConfigSource.ApiType.GRPC)
                                                                .setTransportApiVersion(io.envoyproxy.envoy.config.core.v3.ApiVersion.V3)
                                                                .addGrpcServices(
                                                                    io.envoyproxy.envoy.config.core.v3.GrpcService.newBuilder()
                                                                        .setEnvoyGrpc(
                                                                            io.envoyproxy.envoy.config.core.v3.GrpcService.EnvoyGrpc.newBuilder()
                                                                                .setClusterName("envoy_control_plane")
                                                                        )
                                                                )
                                                        )
                                                        .build()
                                                )
                                                .build()
                                        )
                                        .build()
                                )
                            )

                    )
                )
                .build()
        }
        return listeners
    }
}