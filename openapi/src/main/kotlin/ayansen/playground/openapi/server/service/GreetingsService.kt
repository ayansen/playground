package ayansen.playground.openapi.server.service

import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping

class GreetingsService {

    @Operation(summary = "Greet by name", description = "Returns a greetings message with your name")
    @GetMapping("/{name}")
    fun greetPerson(name:String) : String {
        return "Hello $name"
    }
}