package ayansen.playground.envoy.provider

import ayansen.playground.envoy.entity.Clusters
import ayansen.playground.envoy.entity.Listeners
import ayansen.playground.envoy.entity.Routes
import io.envoyproxy.controlplane.cache.v3.SimpleCache
import io.envoyproxy.controlplane.cache.v3.Snapshot
import io.envoyproxy.envoy.config.cluster.v3.Cluster
import io.envoyproxy.envoy.config.endpoint.v3.ClusterLoadAssignment
import io.envoyproxy.envoy.config.listener.v3.Listener
import io.envoyproxy.envoy.config.route.v3.RouteConfiguration


abstract class ConfigProvider(private val simpleCache: SimpleCache<Any>) {
    companion object {
        private const val GROUP = "key"
        private var version = 0
    }

    abstract fun getListeners(): List<Listener>
    abstract fun getClusters(): List<Cluster>
    abstract fun getRoutes(): List<RouteConfiguration>
    abstract fun getEndpoints(): List<ClusterLoadAssignment>
    abstract fun createOrUpdateListeners(listeners: Listeners): List<Listener>
    abstract fun createOrUpdateClusters(clusters: List<Clusters>): List<Cluster>
    abstract fun createOrUpdateRoutes(Routes: Routes): List<RouteConfiguration>

    fun updateCache() {
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