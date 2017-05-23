package de.grannath.pardona.commands

import de.grannath.pardona.commands.TalentCheckResult.CriticalTalentFailure
import de.grannath.pardona.commands.TalentCheckResult.CriticalTalentSuccess
import de.grannath.pardona.commands.TalentCheckResult.EpicTalentSuccess
import de.grannath.pardona.commands.TalentCheckResult.TalentFailure
import de.grannath.pardona.commands.TalentCheckResult.TalentSuccess
import org.assertj.core.api.Assertions
import org.junit.Test
import org.mockito.Mockito
import java.util.Random

/**
 * Created by johannes on 23.05.17.
 */
class TalentWithoutModifier {
    @Test
    fun givenNoTalentwert_whenRollsMatchExact() {
        val random = mockedRandom(10, 10, 10)
        Assertions.assertThat(random.talentCheck(10,
                                                 10,
                                                 10))
                .isEqualTo(TalentSuccess(0,
                                         10,
                                         10,
                                         10,
                                         1,
                                         0))
    }

    @Test
    fun givenNoTalentwert_whenRollsAreAllBelow() {
        val random = mockedRandom(4, 12, 12)
        Assertions.assertThat(random.talentCheck(8,
                                                 14,
                                                 16))
                .isEqualTo(TalentSuccess(0,
                                         4,
                                         12,
                                         12,
                                         1,
                                         2))
    }

    @Test
    fun givenNoTalentwert_whenRollsAreMixed() {
        val random = mockedRandom(10, 19, 8)
        Assertions.assertThat(random.talentCheck(12,
                                                 16,
                                                 7))
                .isEqualTo(TalentFailure(0,
                                         10,
                                         19,
                                         8,
                                         -4))
    }

    @Test
    fun givenNoTalentwert_whenTwoOnesAreRolled() {
        val random = mockedRandom(1, 19, 1)
        Assertions.assertThat(random.talentCheck(12,
                                                 16,
                                                 7))
                .isEqualTo(CriticalTalentSuccess(0,
                                                 1,
                                                 19,
                                                 1,
                                                 1))
    }

    @Test
    fun givenNoTalentwert_whenThreeOnesAreRolled() {
        val random = mockedRandom(1, 1, 1)
        Assertions.assertThat(random.talentCheck(12,
                                                 16,
                                                 7))
                .isEqualTo(EpicTalentSuccess(0,
                                             1))
    }

    @Test
    fun givenNoTalentwert_whenTwoTwentiesAreRolled() {
        val random = mockedRandom(20, 20, 1)
        Assertions.assertThat(random.talentCheck(12,
                                                 16,
                                                 7))
                .isEqualTo(CriticalTalentFailure(0,
                                                 20,
                                                 20,
                                                 1))
    }

    @Test
    fun givenPositiveTalentwert_whenAllRollsAreBelow() {
        val random = mockedRandom(8, 7, 12)
        Assertions.assertThat(random.talentCheck(12,
                                                 16,
                                                 14,
                                                 6))
                .isEqualTo(TalentSuccess(6,
                                         8,
                                         7,
                                         12,
                                         6,
                                         8))
    }

    @Test
    fun givenPositiveTalentwert_whenSomePointsAreNeeded() {
        val random = mockedRandom(14, 7, 12)
        Assertions.assertThat(random.talentCheck(12,
                                                 16,
                                                 14,
                                                 6))
                .isEqualTo(TalentSuccess(6,
                                         14,
                                         7,
                                         12,
                                         4,
                                         4))
    }

    @Test
    fun givenPositiveTalentwert_whenRollsAreTooHigh() {
        val random = mockedRandom(18, 7, 17)
        Assertions.assertThat(random.talentCheck(12,
                                                 16,
                                                 14,
                                                 6))
                .isEqualTo(TalentFailure(6,
                                         18,
                                         7,
                                         17,
                                         -3))
    }

    @Test
    fun givenNegativeTalentwert_whenAllRollsAreBelow() {
        val random = mockedRandom(8, 7, 10)
        Assertions.assertThat(random.talentCheck(12,
                                                 16,
                                                 14,
                                                 -4))
                .isEqualTo(TalentSuccess(-4,
                                         8,
                                         7,
                                         10,
                                         1,
                                         0))
    }

    @Test
    fun givenNegativeTalentwert_whenRollsAreTooHigh() {
        val random = mockedRandom(10, 7, 10)
        Assertions.assertThat(random.talentCheck(12,
                                                 16,
                                                 14,
                                                 -4))
                .isEqualTo(TalentFailure(-4,
                                         10,
                                         7,
                                         10,
                                         -2))
    }
}

class ModifiedTalent {
    @Test
    fun givenPositiveTalentAndLowerModifier_whenAllRollsAreBelow() {
        val random = mockedRandom(10, 7, 10)
        Assertions.assertThat(random.talentCheck(12,
                                                 16,
                                                 14,
                                                 6,
                                                 4))
                .isEqualTo(TalentSuccess(6,
                                         10,
                                         7,
                                         10,
                                         2,
                                         8))
    }

    @Test
    fun givenPositiveTalentAndLowerModifier_whenSomePointsAreNeeded() {
        val random = mockedRandom(13, 7, 10)
        Assertions.assertThat(random.talentCheck(12,
                                                 16,
                                                 14,
                                                 6,
                                                 4))
                .isEqualTo(TalentSuccess(6,
                                         13,
                                         7,
                                         10,
                                         1,
                                         5))
    }

    @Test
    fun givenPositiveTalentAndHigherModifier_whenAllRollsAreBelow() {
        val random = mockedRandom(10, 7, 10)
        Assertions.assertThat(random.talentCheck(12,
                                                 16,
                                                 14,
                                                 6,
                                                 8))
                .isEqualTo(TalentSuccess(6,
                                         10,
                                         7,
                                         10,
                                         1,
                                         8))
    }

    @Test
    fun givenPositiveTalentAndHigherModifier_whenRollsAreSlightlyAbove() {
        val random = mockedRandom(11, 7, 10)
        Assertions.assertThat(random.talentCheck(12,
                                                 16,
                                                 14,
                                                 6,
                                                 8))
                .isEqualTo(TalentFailure(6,
                                         11,
                                         7,
                                         10,
                                         7))
    }

    @Test
    fun givenPositiveTalentAndHigherModifier_whenRollsAreWellAbove() {
        val random = mockedRandom(15, 7, 10)
        Assertions.assertThat(random.talentCheck(12,
                                                 16,
                                                 14,
                                                 6,
                                                 8))
                .isEqualTo(TalentFailure(6,
                                         15,
                                         7,
                                         10,
                                         3))
    }

    @Test
    fun givenPositiveTalentAndHigherModifier_whenRollsAreHighAbove() {
        val random = mockedRandom(15, 20, 10)
        Assertions.assertThat(random.talentCheck(12,
                                                 16,
                                                 14,
                                                 6,
                                                 8))
                .isEqualTo(TalentFailure(6,
                                         15,
                                         20,
                                         10,
                                         -1))
    }

    @Test
    fun givenPositiveTalentAndNegativeModifier_whenAllRollsAreBelow() {
        val random = mockedRandom(10, 7, 10)
        Assertions.assertThat(random.talentCheck(12,
                                                 16,
                                                 14,
                                                 4,
                                                 -4))
                .isEqualTo(TalentSuccess(4,
                                         10,
                                         7,
                                         10,
                                         4,
                                         6))
    }

    @Test
    fun givenPositiveTalentAndNegativeModifier_whenModifierPointsAreNeeded() {
        val random = mockedRandom(15, 7, 10)
        Assertions.assertThat(random.talentCheck(12,
                                                 16,
                                                 14,
                                                 4,
                                                 -4))
                .isEqualTo(TalentSuccess(4,
                                         15,
                                         7,
                                         10,
                                         4,
                                         1))
    }

    @Test
    fun givenPositiveTalentAndNegativeModifier_whenTalentPointsAreNeeded() {
        val random = mockedRandom(15, 7, 16)
        Assertions.assertThat(random.talentCheck(12,
                                                 16,
                                                 14,
                                                 4,
                                                 -4))
                .isEqualTo(TalentSuccess(4,
                                         15,
                                         7,
                                         16,
                                         3,
                                         -1))
    }

    @Test
    fun givenPositiveTalentAndNegativeModifier_whenRollsAreAbove() {
        val random = mockedRandom(18, 7, 17)
        Assertions.assertThat(random.talentCheck(12,
                                                 16,
                                                 14,
                                                 4,
                                                 -4))
                .isEqualTo(TalentFailure(4,
                                         18,
                                         7,
                                         17,
                                         -5))
    }
}

fun mockedRandom(vararg values: Int): Random {
    val random = Mockito.mock(Random::class.java)
    values.fold(Mockito.`when`(random.nextInt(19))) { mock, value ->
        mock.thenReturn(value - 1)
    }
    return random
}
