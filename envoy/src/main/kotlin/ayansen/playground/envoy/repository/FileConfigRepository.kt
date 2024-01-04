package ayansen.playground.envoy.repository

import ayansen.playground.envoy.FileProviderConfiguration
import ayansen.playground.envoy.entity.Clusters
import ayansen.playground.envoy.entity.Listeners
import ayansen.playground.envoy.entity.Routes
import ayansen.playground.envoy.provider.ConfigProvider
import ayansen.playground.envoy.provider.FileConfigProvider
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey

class FileConfigRepository(private val configProvider: ConfigProvider, private val fileProviderConfiguration: FileProviderConfiguration) : ConfigRepository {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(FileConfigProvider::class.java)
    }

    init {
        val path = Path.of(fileProviderConfiguration.path)
        if (path.toFile().exists()) {
            configProvider.updateCache()
            watchForChanges(path)
        } else {
            LOGGER.error("Configuration directory ${fileProviderConfiguration.path} not found")
        }
    }

    override fun createOrUpdateListeners(listeners: Listeners) {
        throw NotImplementedError("Updates to file can be done manually")
    }

    override fun createOrUpdateClusters(clusters: List<Clusters>) {
        throw NotImplementedError("Updates to file can be done manually")
    }

    override fun createOrUpdateRoutes(Routes: Routes) {
        throw NotImplementedError("Updates to file can be done manually")
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
                        LOGGER.info("Looking for changes in $path")
                        val wk: WatchKey = watchService.take()
                        for (event in wk.pollEvents()) {
                            val changed: Path = event.context() as Path
                            LOGGER.info("File changed: $changed")
                            configProvider.updateCache()
                        }
                        val valid = wk.reset()
                        if (!valid) {
                            LOGGER.info("Key has been unregistered")
                            isRunning = false
                        }
                    }
                }
            } catch (e: Exception) {
                LOGGER.error("Error while watching for changes in ${fileProviderConfiguration.path}", e)
            }
        }
    }
}