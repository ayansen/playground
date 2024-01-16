package ayansen.playground.envoy.controller

import ayansen.playground.envoy.entity.Proxy
import ayansen.playground.envoy.provider.ProxyProvider
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/proxy")
class EnvoyConfigurationsController(private val proxyProvider: ProxyProvider) {
    @Operation(summary = "Get All Proxies", description = "Returns a list of Proxies configured in Envoy")
    @GetMapping("/all")
    suspend fun getProxies(): List<Proxy> {
        return proxyProvider.getProxies()
    }
}
