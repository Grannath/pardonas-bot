package de.grannath.pardona.commands

import de.grannath.pardona.Command
import de.grannath.pardona.commands.EigenschaftCheckResult.CriticalChanceEigenschaftFailure
import de.grannath.pardona.commands.EigenschaftCheckResult.CriticalChanceEigenschaftSuccess
import de.grannath.pardona.commands.EigenschaftCheckResult.CriticalEigenschaftFailure
import de.grannath.pardona.commands.EigenschaftCheckResult.CriticalEigenschaftSuccess
import de.grannath.pardona.commands.EigenschaftCheckResult.EigenschaftFailure
import de.grannath.pardona.commands.EigenschaftCheckResult.EigenschaftSuccess
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
        if (args.isEmpty()) {
            return "Too few arguments."
        }

        val ints: List<Int>
        try {
            ints = args.map { it.toInt() }
        } catch (e: Exception) {
            return "Only integers as parameters!"
        }

        return random.eigenschaftCheck(ints[0],
                                       ints.getOrNull(1) ?: 0)
                .toMessage()
    }

    private fun EigenschaftCheckResult.toMessage() =
            when (this) {
                is CriticalEigenschaftFailure ->
                    "Patzer! (20, $secondRoll)! max. Erschwernis $maxModifier"
                is CriticalChanceEigenschaftFailure ->
                    "Fehlschlag! (20, $secondRoll)! max. Erschwernis $maxModifier"
                is CriticalEigenschaftSuccess ->
                    "Kritisch! (1, $secondRoll)! max. Erschwernis $maxModifier"
                is CriticalChanceEigenschaftSuccess ->
                    "Erfolg! (20, $secondRoll)! max. Erschwernis $maxModifier"
                is EigenschaftFailure ->
                    "Fehlschlag! ($roll)! max. Erschwernis $maxModifier"
                is EigenschaftSuccess ->
                    "Erfolg! ($roll)! max. Erschwernis $maxModifier"
            }
}

fun Random.eigenschaftCheck(value: Int,
                            modifier: Int = 0): EigenschaftCheckResult {
    val res = nextInt(19) + 1

    if (res == 20) {
        val res2 = nextInt(19) + 1
        return if (res2 <= value - modifier) {
            CriticalChanceEigenschaftFailure(value, res2, value - res2)
        } else {
            CriticalEigenschaftFailure(value, res2, value - res2)
        }
    } else if (res == 1) {
        val res2 = nextInt(19) + 1
        return if (res2 <= value - modifier) {
            CriticalEigenschaftSuccess(value, res2, value - res2)
        } else {
            CriticalChanceEigenschaftSuccess(value, res2, value - res2)
        }
    } else {
        return if (res <= value - modifier) {
            EigenschaftSuccess(value, res, value - res)
        } else {
            EigenschaftFailure(value, res, value - res)
        }
    }
}

sealed class EigenschaftCheckResult {
    data class CriticalEigenschaftSuccess(val value: Int,
                                          val secondRoll: Int,
                                          val maxModifier: Int) : EigenschaftCheckResult()

    data class CriticalChanceEigenschaftSuccess(val value: Int,
                                                val secondRoll: Int,
                                                val maxModifier: Int) : EigenschaftCheckResult()

    data class CriticalEigenschaftFailure(val value: Int,
                                          val secondRoll: Int,
                                          val maxModifier: Int) : EigenschaftCheckResult()

    data class CriticalChanceEigenschaftFailure(val value: Int,
                                                val secondRoll: Int,
                                                val maxModifier: Int) : EigenschaftCheckResult()

    data class EigenschaftSuccess(val value: Int,
                                  val roll: Int,
                                  val maxModifier: Int) : EigenschaftCheckResult()

    data class EigenschaftFailure(val value: Int,
                                  val roll: Int,
                                  val maxModifier: Int) : EigenschaftCheckResult()
}