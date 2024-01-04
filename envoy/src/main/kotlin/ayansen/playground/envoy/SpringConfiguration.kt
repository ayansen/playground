package ayansen.playground.envoy

import EnvoyConfigurationServer
import ayansen.playground.envoy.repository.ConfigRepository
import ayansen.playground.envoy.repository.FileConfigRepository
import io.envoyproxy.controlplane.cache.v3.SimpleCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SpringConfiguration {

    @Bean
    open fun cacheSetup(): SimpleCache<Any> = SimpleCache<Any> { "key" }

    @Bean(initMethod = "init")
    open fun setConfigProvider(simpleCache: SimpleCache<Any>): ConfigRepository = FileConfigRepository(simpleCache)

    @Bean(initMethod = "start")
    open fun setupDiscoveryServer(simpleCache: SimpleCache<Any>): EnvoyConfigurationServer =
        EnvoyConfigurationServer(simpleCache)
}