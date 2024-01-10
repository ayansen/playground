/**
 * Copyright 2020.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ayansen.playground.kafka

import ayansen.playground.avro.SampleEvent
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.Network
import org.testcontainers.utility.DockerImageName
import java.util.*

data class KeyValueTimestamp<T, U>(val key: T, val value: U, val timestamp: Long)


object Fixtures {

    private fun initializeKafka(): KafkaContainer {
        val network: Network = Network.newNetwork()
        val kafka = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0")).withKraft()
        kafka.withNetwork(network).start()
        return kafka
    }

    fun createTopics(topics:List<String>) {
        val newTopics = topics.map { NewTopic(it, 1, 1) }
        val adminClient = AdminClient.create(mapOf(BOOTSTRAP_SERVERS_CONFIG to kafkaContainer.bootstrapServers))
        adminClient.createTopics(newTopics)
    }

    val kafkaContainer = initializeKafka()
    val schemaRegistryContainer = SchemaRegistryContainer().withKafka(kafkaContainer)
    private const val APP_GROUP_ID = "test_application"

    fun generateSampleEvents(numberOfEvents: Int, eventName: String): List<KeyValueTimestamp<String, SampleEvent>> =
        (1..numberOfEvents).map {
            val event = SampleEvent.newBuilder()
                .setId(it.toString())
                .setEventName(eventName)
                .build()
            KeyValueTimestamp(eventName, event, it * 100L)
        }


    fun getConsumerProperties(): Properties {
        val config = Properties()
        config[ConsumerConfig.GROUP_ID_CONFIG] = APP_GROUP_ID
        config[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaContainer.bootstrapServers
        config["schema.registry.url"] = schemaRegistryContainer.schemaRegistryUrl()
        config[KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG] = true
        config[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest"
        config[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = "true"
        config[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        config[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = KafkaAvroDeserializer::class.java
        return config
    }

    fun getProducerProperties(): Properties {
        val config = Properties()
        config["bootstrap.servers"] = kafkaContainer.bootstrapServers
        config["schema.registry.url"] = schemaRegistryContainer.schemaRegistryUrl()
        config["acks"] = "all"
        config[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        config[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaAvroSerializer::class.java
        return config
    }
}