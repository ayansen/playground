package ayansen.playground.envoy.provider

import ayansen.playground.envoy.FileProviderConfiguration
import ayansen.playground.envoy.entity.Clusters
import ayansen.playground.envoy.entity.Listeners
import ayansen.playground.envoy.entity.Routes
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.envoyproxy.controlplane.cache.v3.SimpleCache
import io.envoyproxy.envoy.config.cluster.v3.Cluster
import io.envoyproxy.envoy.config.endpoint.v3.ClusterLoadAssignment
import io.envoyproxy.envoy.config.listener.v3.Listener
import io.envoyproxy.envoy.config.route.v3.RouteConfiguration
import java.nio.file.Path

class FileConfigProvider(simpleCache: SimpleCache<Any>, private val fileProviderConfiguration: FileProviderConfiguration) : ConfigProvider(simpleCache) {


    private val mapper = ObjectMapper(YAMLFactory()).apply {
        registerModule(KotlinModule.Builder().build())
    }
    override fun getListeners(): List<Listener> {
        val listenerConfigPath = Path.of(fileProviderConfiguration.path, "listeners.yaml")
        val listeners = parseYamlFile<Listeners>(listenerConfigPath)
        return listeners.toProtoListeners()
    }

    override fun getClusters(): List<Cluster> {
        val clusterConfigPath = Path.of(fileProviderConfiguration.path, "clusters.yaml")
        val clusters = parseYamlFile<Clusters>(clusterConfigPath)
        return clusters.toProtoClusters()
    }

    override fun getRoutes(): List<RouteConfiguration> {
        val routeConfigPath = Path.of(fileProviderConfiguration.path, "routes.yaml")
        val clusters = parseYamlFile<Routes>(routeConfigPath)
        return clusters.toProtoRoutes()
    }

    override fun getEndpoints(): List<ClusterLoadAssignment> {
        val clusterConfigPath = Path.of(fileProviderConfiguration.path, "clusters.yaml")
        val clusters = parseYamlFile<Clusters>(clusterConfigPath)
        return clusters.toProtoEndpoints()
    }


    private inline fun <reified T> parseYamlFile(path: Path): T {
        return mapper.readValue(path.toFile(), T::class.java)
    }
}