package ayansen.playground.openapi.server

import ayansen.playground.openapi.server.service.GreetingsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SpringConfiguration {

    @Bean
    open fun setupGreetingsService(): GreetingsService {
        return GreetingsService()
    }
}