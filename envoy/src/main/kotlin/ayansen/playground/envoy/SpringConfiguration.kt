package ayansen.playground.envoy

import DiscoveryServer
import ayansen.playground.envoy.entity.ListenersConfiguration
import ayansen.playground.envoy.entity.ProxyProviderConfigurations
import ayansen.playground.envoy.provider.ProxyProvider
import ayansen.playground.envoy.provider.FileProxyProvider
import io.envoyproxy.controlplane.cache.v3.SimpleCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * This class is responsible for configuring the spring beans
 */
@Configuration
open class SpringConfiguration {
    @Bean
    open fun setupCache(): SimpleCache<Any> = SimpleCache<Any> { "key" }

    @Bean
    open fun setupProviderConfigurations(): ProxyProviderConfigurations {
        return ProxyProviderConfigurations()
    }

    @Bean
    open fun setupListenerConfigurations(): ListenersConfiguration {
        return ListenersConfiguration()
    }

    @Bean
    open fun setupFileConfigProvider(
        simpleCache: SimpleCache<Any>,
        proxyProviderConfigurations: ProxyProviderConfigurations,
        listenersConfiguration: ListenersConfiguration
    ): ProxyProvider {
        return FileProxyProvider(proxyProviderConfigurations.file.path, simpleCache, listenersConfiguration)
    }

    @Bean
    open fun setupDiscoveryServer(simpleCache: SimpleCache<Any>): DiscoveryServer =
        DiscoveryServer(simpleCache)
}