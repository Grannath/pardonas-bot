package de.grannath.pardona.commands

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito
import java.util.Random

class RollExpressionTest {
    @Test
    fun rollingW6() {
        val random = mockedRandom(sides = 6,
                                  values = 3)

        assertThat(random.roll("1w6")).isEqualTo(3)
    }

    @Test
    fun rollingD6() {
        val random = mockedRandom(sides = 6,
                                  values = 3)

        assertThat(random.roll("1d6")).isEqualTo(3)
    }

    @Test
    fun rollingMultipleDice() {
        val random = mockedRandom(3, 4, 5, sides = 8)

        assertThat(random.roll("3w8")).isEqualTo(12)
    }

    @Test
    fun defaultsToOneDice() {
        val random = mockedRandom(2, sides = 10)

        assertThat(random.roll("d10")).isEqualTo(2)
    }
}

class rollCommandTest {
    @Test
    fun handlesSingleExpression() {
        val command = RollCommand(mockedRandom(2, sides = 10))

        assertThat(command.buildReply(listOf("d10"))).endsWith(" 2")
    }

    @Test
    fun handlesSingleConstant() {
        val command = RollCommand(mockedRandom(2, sides = 10))

        assertThat(command.buildReply(listOf("12"))).endsWith(" 12")
    }

    @Test
    fun handlesSimpleSum() {
        val random = Mockito.mock(Random::class.java)
        Mockito.`when`(random.nextInt(Mockito.anyInt()))
                .thenReturn(5)
                .thenReturn(2)
                .thenReturn(12)
        val command = RollCommand(random)

        assertThat(command.buildReply(listOf("2d6", "+", "w20"))).endsWith(" 22")
    }

    @Test
    fun handlesLongerSums() {
        val random = Mockito.mock(Random::class.java)
        Mockito.`when`(random.nextInt(Mockito.anyInt()))
                .thenReturn(5)
                .thenReturn(2)
                .thenReturn(12)
                .thenReturn(6)
                .thenReturn(9)
        val command = RollCommand(random)

        assertThat(command.buildReply(listOf("2d6", "+", "w20-2w10", "+5"))).endsWith(" 10")
    }
}