package de.grannath.pardona.commands

import de.grannath.pardona.Command
import org.springframework.stereotype.Component
import java.util.Random

@Component
class TalentCommand : Command {
    private val random = Random()

    val keywords = listOf("t", "talent")

    override fun canHandle(keyword: String) =
            keywords.any { it == keyword.toLowerCase() }

    override fun buildReply(args: List<String>): String {
        if (args.size < 3) {
            return "Too few arguments."
        }

        val ints: List<Int>
        try {
            ints = args.map { it.toInt() }
        } catch (e: Exception) {
            return "Only integers as parameters!"
        }

        return random.talentCheck(ints[0],
                                  ints[1],
                                  ints[2],
                                  ints.getOrNull(3) ?: 0,
                                  ints.getOrNull(4) ?: 0)
                .toMessage()
    }

    private fun TalentCheckResult.toMessage(): String {
        if (tapStar == null) {
            return "Fehlgeschlagen! ($first, $second, $third)"
        } else {
            return "Erfolg! ($first, $second, $third) TaP* $tapStar"
        }
    }
}

fun Random.talentCheck(first: Int,
                       second: Int,
                       third: Int,
                       taw: Int = 0,
                       modifier: Int = 0): TalentCheckResult {
    val effTaw = taw - modifier
    if (effTaw < 0) {
        return talentCheck(first + effTaw, second + effTaw, third + effTaw)
    }

    val firstRes = nextInt(19) + 1
    val secondRes = nextInt(19) + 1
    val thirdRes = nextInt(19) + 1

    val tap = effTaw - usedTap(first, firstRes) -
              usedTap(second, secondRes) - usedTap(third, thirdRes)
    if (tap >= 0) {
        return TalentCheckResult(firstRes,
                                 secondRes,
                                 thirdRes,
                                 if (tap == 0) 1 else minOf(tap, taw))
    } else {
        return TalentCheckResult(firstRes, secondRes, thirdRes, null)
    }
}

private fun usedTap(taw: Int,
                    res: Int) = if (res <= taw) 0 else res - taw

data class TalentCheckResult(val first: Int,
                             val second: Int,
                             val third: Int,
                             val tapStar: Int?)