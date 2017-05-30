package de.grannath.pardona.commands

import de.grannath.pardona.Command
import de.grannath.pardona.logger
import org.springframework.stereotype.Component
import java.util.Random
import java.util.StringJoiner

/**
 * Created by johannes on 24.05.17.
 */
@Component
class RollCommand(private val random: Random = Random()) : Command {
    private val LOGGER by logger()

    val keywords = listOf("r", "roll")

    override fun canHandle(keyword: String) =
            keywords.any { it == keyword.toLowerCase() }

    override fun buildReply(args: List<String>): String {
        val tokens = args.flatMap { it.split(operatorPattern) }.map { it.trim() }.filter { it.isNotBlank() }

        val commandString = tokens.fold(StringJoiner(" ")) { j, st -> j.add(st) }.toString()
        LOGGER.debug("Received roll command of {}.",
                     commandString)

        with(tokens.iterator()) {
            val collector = RollCollector()
            while (hasNext()) {
                val expression = next()
                try {
                    collector.thenValue(random.roll(expression))
                } catch (e: Exception) {
                    return "Expected dice, but received $expression"
                }

                if (!hasNext()) return "$commandString\nErgebnis: ${collector.result}"

                val sign = next()
                try {
                    collector.thenOperation(operation(sign))
                } catch (e: Exception) {
                    return "Expected operator, but received $sign"
                }
            }
            return "Expected some dice, but command ended."
        }
    }

}

val operatorPattern = Regex("((?<=[+-])|(?=[+-]))")
val expressionPattern = Regex("(\\d*)[wd](\\d+)")
val constantPattern = Regex("(\\d+)")

fun Random.roll(expression: String): Int {
    if (expression.matches(constantPattern)) {
        return expression.toInt()
    } else if (expression.matches(expressionPattern)) {
        with(expressionPattern.find(expression)) {
            return if (this != null)
                roll(groupValues[1].toIntOrNull(), groupValues[2].toInt())
            else
                throw IllegalArgumentException("Not a valid pattern!")
        }
    } else {
        throw IllegalArgumentException("Not a valid pattern!")
    }
}

private fun Random.roll(count: Int?,
                        sides: Int) =
        (1..(count ?: 1)).map { nextInt(sides - 1) + 1 }.reduce(Int::plus)

private enum class Operation {
    PLUS, MINUS;
}

private fun operation(sign: String) =
        if (sign == "+") Operation.PLUS
        else if (sign == "-") Operation.MINUS
        else throw IllegalArgumentException("Not a sign.")

private class RollCollector {
    var result: Int = 0
        private set
    private var operation: Operation? = Operation.PLUS

    fun thenOperation(op: Operation): RollCollector {
        if (operation != null)
            throw IllegalStateException("Tried setting two operations.")

        operation = op
        return this
    }

    fun thenValue(value: Int): RollCollector {
        when (operation) {
            Operation.PLUS -> result += value
            Operation.MINUS -> result -= value
            null -> throw IllegalStateException("No operation set.")
        }
        operation = null

        return this
    }
}