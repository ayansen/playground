package ayansen.playground.envoy.provider

import ayansen.playground.envoy.entity.ListenersConfiguration
import ayansen.playground.envoy.entity.Proxy
import io.envoyproxy.controlplane.cache.v3.SimpleCache
import io.envoyproxy.controlplane.cache.v3.Snapshot


abstract class ProxyProvider(private val simpleCache: SimpleCache<Any>, private val listeners: ListenersConfiguration) {
    companion object {
        private const val GROUP = "key"
        private var version = 0
    }

    abstract fun getProxies(): List<Proxy>
    abstract fun createOrUpdateProxy(proxy: Proxy): Proxy
    abstract fun deleteProxy(proxy: Proxy): Proxy

    fun updateCache() {
        val proxies = getProxies()
        simpleCache.setSnapshot(
            /* group = */ GROUP,
            /* snapshot = */ Snapshot.create(
                proxies.flatMap { it.toProtoClusters()},
                proxies.flatMap { it.toProtoEndpoints() },
                listeners.toProtoListeners(),
                proxies.map { it.toProtoRoute() },
                listOf(),
                version++.toString()
            )
        )

    }
}