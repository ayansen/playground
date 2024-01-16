package ayansen.playground.envoy.provider

import ayansen.playground.envoy.entity.ListenersConfiguration
import ayansen.playground.envoy.entity.Proxy
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.envoyproxy.controlplane.cache.v3.SimpleCache

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey

/**
 * This class is responsible for providing the proxy configuration from a folder of yaml files
 * @property proxyFolderPath
 * @property simpleCache
 * @property listenersConfiguration
 */
class FileProxyProvider(
    private val proxyFolderPath: String,
    simpleCache: SimpleCache<Any>,
    listenersConfiguration: ListenersConfiguration,
) : ProxyProvider(simpleCache, listenersConfiguration) {

    companion object {
        private val logger = LoggerFactory.getLogger(FileProxyProvider::class.java)
        private val mapper: ObjectMapper = ObjectMapper(YAMLFactory()).apply {
            registerModule(KotlinModule.Builder().build())
        }
    }

    init {
        val path = Path.of(proxyFolderPath)
        if (path.toFile().exists()) {
            updateCache()
            watchForChanges(path)
        } else {
            throw IllegalArgumentException("Configuration directory $proxyFolderPath not found")
        }
    }

    override fun getProxies(): List<Proxy> {
        return Path.of(proxyFolderPath).toFile().listFiles()?.map { file ->
            parseYamlFile(file)
        } ?: emptyList()
    }

    override fun createOrUpdateProxy(proxy: Proxy): Proxy {
        throw NotImplementedError("Updates to file can be done manually")
    }

    override fun deleteProxy(proxy: Proxy): Proxy {
        throw NotImplementedError("proxy deletion can be done by deleting the file from the folder")
    }

    private inline fun <reified T> parseYamlFile(file: File): T {
        return mapper.readValue(file, T::class.java)
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
                logger.error("Error while watching for changes in $proxyFolderPath", e)
            }
        }
    }
}