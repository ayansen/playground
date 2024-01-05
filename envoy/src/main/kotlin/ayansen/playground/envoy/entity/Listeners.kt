package ayansen.playground.envoy.entity


data class Listeners(
    val listeners: List<Listener>
) {

    fun toProtoListeners(): List<io.envoyproxy.envoy.config.listener.v3.Listener> {
        return listeners.map {
            io.envoyproxy.envoy.config.listener.v3.Listener.newBuilder()
                .setName(it.name)
                .setAddress(
                    io.envoyproxy.envoy.config.core.v3.Address.newBuilder()
                        .setSocketAddress(
                            io.envoyproxy.envoy.config.core.v3.SocketAddress.newBuilder()
                                .setAddress(it.socketAddress.address)
                                .setPortValue(it.socketAddress.port)
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