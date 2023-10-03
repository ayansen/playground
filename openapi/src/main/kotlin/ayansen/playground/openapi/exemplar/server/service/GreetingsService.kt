package ayansen.playground.openapi.exemplar.server.service

class GreetingsService {


    fun greetPerson(name:String) : String {
        return "Hello $name"
    }
}