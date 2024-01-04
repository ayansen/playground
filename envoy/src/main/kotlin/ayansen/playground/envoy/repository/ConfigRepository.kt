package ayansen.playground.envoy.repository

import ayansen.playground.envoy.entity.Clusters
import ayansen.playground.envoy.entity.Listeners
import ayansen.playground.envoy.entity.Routes

interface ConfigRepository {
    fun createOrUpdateListeners(listeners: Listeners)
    fun createOrUpdateClusters(clusters: List<Clusters>)
    fun createOrUpdateRoutes(Routes: Routes)
}