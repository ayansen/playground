package ayansen.playground.envoy

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File

object Fixtures {

    val mapper: ObjectMapper = ObjectMapper(YAMLFactory()).apply {
        registerModule(KotlinModule.Builder().build())
    }
    inline fun <reified T> parseYamlFile(file: File): T {
        return mapper.readValue(file, T::class.java)
    }
}