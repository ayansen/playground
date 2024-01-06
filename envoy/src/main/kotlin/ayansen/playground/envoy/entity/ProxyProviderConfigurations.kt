package ayansen.playground.envoy

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "config-provider")
data class ProxyProviderConfigurations(var file: FileProviderConfiguration = FileProviderConfiguration()) {
    data class FileProviderConfiguration(var path: String = "")
}