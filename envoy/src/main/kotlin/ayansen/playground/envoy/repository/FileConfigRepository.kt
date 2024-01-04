package ayansen.playground.envoy.repository

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
import kotlinx.coroutines.Dispatchers
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory

/***
 * The FileConfigRepository class is a subclass of the ConfigRepository abstract class. It is responsible for managing the configuration files stored in a specific directory. It watches for changes in the directory and updates the cache accordingly.
 * Example Usage
val simpleCache = SimpleCache<Any>()
val fileConfigRepository = FileConfigRepository(simpleCache)
fileConfigRepository.init()
In this example, we create an instance of SimpleCache and FileConfigRepository. We then call the init() method of FileConfigRepository to start watching for changes in the configuration directory.

 * Main functionalities
Watches for changes in the configuration directory and updates the cache accordingly.
Implements the abstract methods defined in the ConfigRepository class.
 */
class FileConfigRepository(private val simpleCache: SimpleCache<Any>) : ConfigRepository(simpleCache) {
    companion object {
        private const val CONFIG_DIR = "/Users/ayansen/SrcCode/github/ayansen/playground/envoy/local_configurations"
        private val LOGGER = LoggerFactory.getLogger(FileConfigRepository::class.java)
    }

    private val mapper = ObjectMapper(YAMLFactory()).apply {
        registerModule(KotlinModule.Builder().build())
    }




    override fun getListeners(): List<Listener> {
        val listenerConfigPath = Path.of(CONFIG_DIR, "listeners.yaml")
        val listeners = parseYamlFile<Listeners>(listenerConfigPath)
        return listeners.toProtoListeners()
    }

    override fun getClusters(): List<Cluster> {
        val clusterConfigPath = Path.of(CONFIG_DIR, "clusters.yaml")
        val clusters = parseYamlFile<Clusters>(clusterConfigPath)
        return clusters.toProtoClusters()
    }

    override fun getRoutes(): List<RouteConfiguration> {
        val routeConfigPath = Path.of(CONFIG_DIR, "routes.yaml")
        val clusters = parseYamlFile<Routes>(routeConfigPath)
        return clusters.toProtoRoutes()
    }

    override fun getEndpoints(): List<ClusterLoadAssignment> {
        val clusterConfigPath = Path.of(CONFIG_DIR, "clusters.yaml")
        val clusters = parseYamlFile<Clusters>(clusterConfigPath)
        return clusters.toProtoEndpoints()
    }

    override fun init() {
        val path = Path.of(CONFIG_DIR)
        if (path.toFile().exists()) {
            updateCache()
            watchForChanges(path)
        } else {
            LOGGER.error("Configuration directory $CONFIG_DIR not found")
        }
    }

    private fun watchForChanges(path: Path) {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        scope.launch {
            try {
                FileSystems.getDefault().newWatchService().use { watchService ->
                    path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)
                    var isRunning = true
                    while (isRunning) {
                        LOGGER.info("Looking for changes in $path")
                        val wk: WatchKey = watchService.take()
                        for (event in wk.pollEvents()) {
                            val changed: Path = event.context() as Path
                            LOGGER.info("File changed: $changed")
                            updateCache()
                        }
                        val valid = wk.reset()
                        if (!valid) {
                            LOGGER.info("Key has been unregistered")
                            isRunning = false
                        }
                    }
                }
            } catch (e: Exception) {
                LOGGER.error("Error while watching for changes in $CONFIG_DIR", e)
            }
        }
    }


    private inline fun <reified T> parseYamlFile(path: Path): T {
        return mapper.readValue(path.toFile(), T::class.java)
    }
}