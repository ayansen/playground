package ayansen.playground.envoy.repository

import io.envoyproxy.controlplane.cache.v3.SimpleCache
import io.envoyproxy.controlplane.cache.v3.Snapshot
import io.envoyproxy.envoy.config.cluster.v3.Cluster
import io.envoyproxy.envoy.config.endpoint.v3.ClusterLoadAssignment
import io.envoyproxy.envoy.config.listener.v3.Listener
import io.envoyproxy.envoy.config.route.v3.RouteConfiguration


abstract class ConfigRepository(private val simpleCache: SimpleCache<Any>) {
    companion object {
        private const val GROUP = "key"
        private var version = 0
    }

    protected abstract fun getListeners(): List<Listener>
    protected abstract fun getClusters(): List<Cluster>
    protected abstract fun getRoutes(): List<RouteConfiguration>
    protected abstract fun getEndpoints(): List<ClusterLoadAssignment>
    protected abstract fun init()

    protected fun updateCache() {
        simpleCache.setSnapshot(
            /* group = */ GROUP,
            /* snapshot = */ Snapshot.create(
                getClusters(),
                getEndpoints(),
                getListeners(),
                getRoutes(),
                listOf(),
                version++.toString()
            )
        )

    }
}