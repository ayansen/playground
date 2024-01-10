package ayansen.playground.kafka

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.Network

import org.testcontainers.containers.wait.strategy.Wait


class SchemaRegistryContainer :
    GenericContainer<SchemaRegistryContainer>("$SCHEMA_REGISTRY_IMAGE:$CONFLUENT_PLATFORM_VERSION") {
    companion object {
        const val SCHEMA_REGISTRY_IMAGE = "confluentinc/cp-schema-registry"
        const val SCHEMA_REGISTRY_PORT = 8081
        const val CONFLUENT_PLATFORM_VERSION = "5.5.1"
    }

    init {
        waitingFor(Wait.forHttp("/subjects").forStatusCode(200))
        withExposedPorts(SCHEMA_REGISTRY_PORT)
    }

    fun withKafka(kafka: KafkaContainer): SchemaRegistryContainer {
        return withKafka(kafka.network, kafka.networkAliases[0] + ":9092")
    }

    fun withKafka(network: Network?, bootstrapServers: String): SchemaRegistryContainer {
        withNetwork(network)
        withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
        withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:$SCHEMA_REGISTRY_PORT")
        withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "PLAINTEXT://$bootstrapServers")
        start()
        return self()
    }

    fun schemaRegistryUrl(): String = "http://$host:${getMappedPort(SCHEMA_REGISTRY_PORT)}"
}