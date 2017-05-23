package de.grannath.pardona

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.api.events.Event
import sx.blah.discord.api.events.IListener
import sx.blah.discord.handle.impl.events.ReadyEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageEvent
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import java.util.regex.Pattern


@Configuration
@EnableConfigurationProperties
class BotConfiguration {
    private val LOGGER by logger()

    @Bean(destroyMethod = "logout")
    fun discordClient(properties: DiscordProperties): IDiscordClient {
        with(properties.token) {
            LOGGER.info("Logging in at discord with token {}.",
                        replaceRange(5, length, "*".repeat(length - 5)))
        }

        val readyListener = IListener<ReadyEvent> {
            LOGGER.info("I'm ready!")
        }
        val debugListener = IListener<Event> {
            LOGGER.debug("Received event of type {}.", it.javaClass.name)
        }
        return ClientBuilder().withToken(properties.token)
                .registerListeners(debugListener, readyListener)
                .login()!!
    }

    @Bean
    fun pingPongListener(client: IDiscordClient): IListener<MessageReceivedEvent> {
        val listener = IListener<MessageReceivedEvent> {
            if (it.message.content == "ping") {
                it.message.reply("pong")
            }
        }
        client.dispatcher.registerListener(listener)
        return listener
    }
}

@Component
class CommandListener(client: IDiscordClient,
                      val commands: List<Command>) {
    init {
        client.dispatcher.registerListener(getMentionListener())
        client.dispatcher.registerListener(getPrivateListener())
    }

    private val LOGGER by logger()
    private val splitPattern = Pattern.compile("\\p{Blank}+")

    private fun getMentionListener() = IListener<MentionEvent> {
        if (!it.channel.isPrivate) {
            handleIfCommand(it)
        }
    }

    private fun getPrivateListener() = IListener<MessageReceivedEvent> {
        if (it.channel.isPrivate) {
            handleIfCommand(it)
        }
    }

    private fun handleIfCommand(event: MessageEvent) {
        LOGGER.debug("I'm mentioned in {}.", event.message.content)

        val split = event.message.content.split(splitPattern).filterIndexed { index, string ->
            index != 0 || !string.startsWith("<@")
        }

        commands.find { it.canHandle(split[0]) }.apply {
            if (this == null)
                return

            LOGGER.debug("Found keyword {}.", split[0])
            if (event.channel.isPrivate) {
                event.channel.sendMessage(buildReply(split.subList(1, split.size)))
            } else {
                event.message.reply(buildReply(split.subList(1, split.size)))
            }
        }
    }
}

interface Command {
    fun canHandle(keyword: String): Boolean

    fun buildReply(args: List<String>): String
}

@ConfigurationProperties("discord")
@Component
class DiscordProperties(var token: String = "",
                        var clientId: String = "",
                        var permissions: Int = 0)