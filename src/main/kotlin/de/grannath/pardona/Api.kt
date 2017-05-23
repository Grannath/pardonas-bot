package de.grannath.pardona

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import java.net.URI

@Controller
class AddBotController(val properties: DiscordProperties) {
    @GetMapping("/addbot")
    fun addBot() =
            ResponseEntity.status(302)
                    .location(URI("https://discordapp.com/api/oauth2/authorize" +
                                  "?client_id=${properties.clientId}" +
                                  "&scope=bot" +
                                  "&permissions=${properties.permissions}"))
                    .body("Redirecting to discord servers...")
}
