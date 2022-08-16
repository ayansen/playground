package ayansen.playground.graphql.server.model

import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component

@Component
class HelloWorldQuery : Query {
    fun helloWorld() = "Hello World!"
}