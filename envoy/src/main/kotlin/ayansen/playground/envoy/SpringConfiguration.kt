package ayansen.playground.envoy

import DiscoveryServer
import ayansen.playground.envoy.provider.ConfigProvider
import ayansen.playground.envoy.provider.FileConfigProvider
import ayansen.playground.envoy.repository.FileConfigRepository
import io.envoyproxy.controlplane.cache.v3.SimpleCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SpringConfiguration {

    @Bean
    open fun setupCache(): SimpleCache<Any> = SimpleCache<Any> { "key" }

    @Bean
    open fun setupProviderConfigurations(): ProviderConfigurations {
        return ProviderConfigurations()
    }

    @Bean
    open fun setupConfigProvider(simpleCache: SimpleCache<Any>, providerConfigurations: ProviderConfigurations): ConfigProvider = FileConfigProvider(simpleCache, providerConfigurations.file)

    @Bean
    open fun setupConfigRepository(configProvider: ConfigProvider, providerConfigurations: ProviderConfigurations): FileConfigRepository = FileConfigRepository(configProvider, providerConfigurations.file)

    @Bean
    open fun setupDiscoveryServer(simpleCache: SimpleCache<Any>): DiscoveryServer =
        DiscoveryServer(simpleCache)
}