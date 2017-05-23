package de.grannath.pardona.commands

import de.grannath.pardona.Command
import org.springframework.stereotype.Component
import java.util.Random

/**
 * Created by johannes on 23.05.17.
 */
@Component
class EigenschaftCommand : Command {
    private val random = Random()

    val keywords = listOf("e", "eigenschaft")

    override fun canHandle(keyword: String) =
            keywords.any { it == keyword.toLowerCase() }

    override fun buildReply(args: List<String>): String {
        if (args.size < 1) {
            return "Too few arguments."
        }

        val ints: List<Int>
        try {
            ints = args.map { it.toInt() }
        } catch (e: Exception) {
            return "Only integers as parameters!"
        }

        return random.eigenschaftCheck(ints[0],
                                       ints.getOrNull(4) ?: 0)
                .toMessage()
    }

    private fun EigenschaftCheckResult.toMessage(): String {
        if (success) {
            return "Erfolg! ($result), max. Erschwernis $maxModifier"
        } else {
            return "Fehlgeschlagen! ($result), max. Erschwernis $maxModifier"
        }
    }
}

fun Random.eigenschaftCheck(value: Int,
                            modifier: Int = 0): EigenschaftCheckResult {
    val res = nextInt(19) + 1

    val success = res < value - modifier
    val maxModifier = value - res

    return EigenschaftCheckResult(res, success, maxModifier)
}

data class EigenschaftCheckResult(val result: Int,
                                  val success: Boolean,
                                  val maxModifier: Int)