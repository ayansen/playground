package ayansen.playground.envoy.entity


import ayansen.playground.envoy.Fixtures.parseYamlFile
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertNotNull

class ProxyTest {

    @Test
    fun `generates a unique cluster for each route in the proxy`() {
        val proxy:Proxy = parseYamlFile(File("src/test/resources/proxies/proxy_with_multiple_routes.yaml"))
        val clusters = proxy.toProtoClusters()

        assert(clusters.size == 2)
        assert(clusters[0].name != clusters[1].name)
    }

    @Test
    fun `generates multiple endpoints with the right cluster name for each route in the proxy`() {
        val proxy:Proxy = parseYamlFile(File("src/test/resources/proxies/proxy_with_multiple_routes.yaml"))
        val clusters = proxy.toProtoClusters()
        val endpoints = proxy.toProtoEndpoints()

        assert(clusters.size == 2)
        assert(endpoints.size == 2)
        assert(clusters[0].name != clusters[1].name)
        assert(endpoints[0].clusterName == clusters[0].name)
        assert(endpoints[1].clusterName == clusters[1].name)
    }

    @Test
    fun `generates a single route configuration for a unique domain name`() {
        val proxy:Proxy = parseYamlFile(File("src/test/resources/proxies/proxy_with_multiple_routes.yaml"))

        val routeConfiguration = proxy.toProtoRoute()
        assertNotNull(routeConfiguration)
    }

}