package ayansen.playground.envoy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
/**
 * This is the main entry point for the application
 */
@SpringBootApplication
open class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}
