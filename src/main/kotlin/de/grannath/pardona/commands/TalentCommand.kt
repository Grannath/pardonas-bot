package de.grannath.pardona.commands

import de.grannath.pardona.Command
import de.grannath.pardona.commands.TalentCheckResult.CriticalTalentFailure
import de.grannath.pardona.commands.TalentCheckResult.CriticalTalentSuccess
import de.grannath.pardona.commands.TalentCheckResult.EpicTalentSuccess
import de.grannath.pardona.commands.TalentCheckResult.TalentFailure
import de.grannath.pardona.commands.TalentCheckResult.TalentSuccess
import org.springframework.stereotype.Component
import java.util.Random

@Component
class TalentCommand(private val random: Random = Random()) : Command {
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

}

private fun TalentCheckResult.toMessage() =
        when (this) {
            is EpicTalentSuccess ->
                "Episch! (1, 1, 1)! TaP* $tapStar"
            is CriticalTalentSuccess ->
                "Kritisch! ($firstRoll, $secondRoll, $thirdRoll)! TaP* $tapStar"
            is CriticalTalentFailure ->
                "Patzer! ($firstRoll, $secondRoll, $thirdRoll)!"
            is TalentSuccess ->
                "Erfolg! ($firstRoll, $secondRoll, $thirdRoll), TaP* $tapStar, max. Erschwernis $maxModifier"
            is TalentFailure ->
                "Fehlschlag! ($firstRoll, $secondRoll, $thirdRoll), max. Erschwernis $maxModifier"
        }

fun Random.talentCheck(first: Int,
                       second: Int,
                       third: Int,
                       taw: Int = 0,
                       modifier: Int = 0): TalentCheckResult {
    val effTaw = taw - modifier

    val firstRes = nextInt(19) + 1
    val secondRes = nextInt(19) + 1
    val thirdRes = nextInt(19) + 1

    if (firstRes == 1 && secondRes == 1 && thirdRes == 1) {
        return EpicTalentSuccess(taw, maxOf(minOf(taw, effTaw), 1))
    } else if (atLeast(2, firstRes, secondRes, thirdRes) { it == 1 }) {
        return CriticalTalentSuccess(taw,
                                     firstRes,
                                     secondRes,
                                     thirdRes,
                                     maxOf(minOf(taw, effTaw), 1))
    } else if (atLeast(2, firstRes, secondRes, thirdRes) { it == 20 }) {
        return CriticalTalentFailure(taw, firstRes, secondRes, thirdRes)
    }

    val tap = if (effTaw >= 0) {
        effTaw - usedTap(first, firstRes) -
        usedTap(second, secondRes) - usedTap(third, thirdRes)
    } else {
        -usedTap(first + effTaw, firstRes) -
        usedTap(second + effTaw, secondRes) - usedTap(third + effTaw, thirdRes)
    }

    val maxMod = if (first >= firstRes && second >= secondRes && third >= thirdRes) {
        taw + minOf(first - firstRes, second - secondRes, third - thirdRes)
    } else if (tap >= 0) {
        modifier + tap
    } else if (effTaw >= 0) {
        modifier + tap
    } else {
        taw - usedTap(first, firstRes) -
        usedTap(second, secondRes) - usedTap(third, thirdRes)
    }

    if (tap >= 0) {
        return TalentSuccess(taw,
                             firstRes,
                             secondRes,
                             thirdRes,
                             if (tap == 0) 1 else minOf(tap, taw),
                             maxMod)
    } else {
        return TalentFailure(taw, firstRes, secondRes, thirdRes, maxMod)
    }
}

private fun <T> atLeast(n: Int,
                        vararg values: T,
                        predicate: (T) -> Boolean) =
        values.filter(predicate).size >= n

private fun usedTap(value: Int,
                    result: Int) = if (result <= value) 0 else result - value

sealed class TalentCheckResult {
    data class EpicTalentSuccess(val taw: Int,
                                 val tapStar: Int) : TalentCheckResult()

    data class CriticalTalentSuccess(val taw: Int,
                                     val firstRoll: Int,
                                     val secondRoll: Int,
                                     val thirdRoll: Int,
                                     val tapStar: Int) : TalentCheckResult()

    data class CriticalTalentFailure(val taw: Int,
                                     val firstRoll: Int,
                                     val secondRoll: Int,
                                     val thirdRoll: Int) : TalentCheckResult()

    data class TalentSuccess(val taw: Int,
                             val firstRoll: Int,
                             val secondRoll: Int,
                             val thirdRoll: Int,
                             val tapStar: Int,
                             val maxModifier: Int) : TalentCheckResult()

    data class TalentFailure(val taw: Int,
                             val firstRoll: Int,
                             val secondRoll: Int,
                             val thirdRoll: Int,
                             val maxModifier: Int) : TalentCheckResult()
}