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
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey

class FileConfigProvider(simpleCache: SimpleCache<Any>, private val fileProviderConfiguration: FileProviderConfiguration) : ConfigProvider(simpleCache) {

    companion object {
        private val logger = LoggerFactory.getLogger(FileConfigProvider::class.java)
        private val mapper: ObjectMapper = ObjectMapper(YAMLFactory()).apply {
            registerModule(KotlinModule.Builder().build())
        }
    }


    init {
        val path = Path.of(fileProviderConfiguration.path)
        if (path.toFile().exists()) {
            updateCache()
            watchForChanges(path)
        } else {
            throw IllegalArgumentException("Configuration directory ${fileProviderConfiguration.path} not found")
        }

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


    override fun createOrUpdateListeners(listeners: Listeners) : List<Listener> {
        throw NotImplementedError("Updates to file can be done manually")
    }

    override fun createOrUpdateClusters(clusters: List<Clusters>) : List<Cluster> {
        throw NotImplementedError("Updates to file can be done manually")
    }

    override fun createOrUpdateRoutes(Routes: Routes) : List<RouteConfiguration> {
        throw NotImplementedError("Updates to file can be done manually")
    }


    private inline fun <reified T> parseYamlFile(path: Path): T {
        return mapper.readValue(path.toFile(), T::class.java)
    }

    private fun watchForChanges(path: Path) {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    FileSystems.getDefault().newWatchService()
                }.use { watchService ->
                    path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)
                    var isRunning = true
                    while (isRunning) {
                        logger.info("Looking for changes in $path")
                        val wk: WatchKey = watchService.take()
                        for (event in wk.pollEvents()) {
                            val changed: Path = event.context() as Path
                            logger.info("File changed: $changed")
                            updateCache()
                        }
                        val valid = wk.reset()
                        if (!valid) {
                            logger.info("Key has been unregistered")
                            isRunning = false
                        }
                    }
                }
            } catch (e: Exception) {
                logger.error("Error while watching for changes in ${fileProviderConfiguration.path}", e)
            }
        }
    }
}