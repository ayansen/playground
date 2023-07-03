package ayansen.playground.openapi.server.controller

import ayansen.playground.openapi.server.service.GreetingsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/greetings")
class GreetingsController(private val greetingsService: GreetingsService) {

    @GetMapping("/{id}")
    suspend fun getEmployeeById(@PathVariable name: String): String {
        return greetingsService.greetPerson(name)
    }
}