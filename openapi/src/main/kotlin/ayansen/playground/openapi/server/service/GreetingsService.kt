package ayansen.playground.openapi.server.service

class GreetingsService {

    fun greetPerson(name:String) : String {
        return "Hello $name"
    }
}