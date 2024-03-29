package ayansen.playground.openapi.exemplar.server.controller

import ayansen.playground.openapi.exemplar.server.service.GreetingsService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/greetings")
class GreetingsController(private val greetingsService: GreetingsService) {

    @Operation(summary = "Greet by name", description = "Returns a greetings message with your name")
    @GetMapping("/{name}")
    suspend fun greet(@PathVariable name: String): String {
        return greetingsService.greetPerson(name)
    }
}