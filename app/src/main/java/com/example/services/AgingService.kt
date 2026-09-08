package com.example.services

import com.example.data.model.ClientCharacter
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * AGING SERVICE (Prompt 02 of 20: CHARACTER SYSTEM)
 *
 * Implements biological aging and progressive degradation:
 * - Health (gradual degradation, scaling in senior years)
 * - Fertility (steady in youth, steep drop in late 30s/40s)
 * - Energy (gradual systemic decline)
 * - Athletic Performance (mild until 30, SHARP drop past age 30)
 *
 * ARCHITECTURAL MANDATE:
 * Must route degradation adjustments through the central FormulaEngine rather
 * than executing inline math.
 */
data class AgingResult(
    val newAge: Int,
    val previousHealth: Int,
    val previousFertility: Int,
    val previousEnergy: Int,
    val previousAthleticPerformance: Int,
    val newHealth: Int,
    val newFertility: Int,
    val newEnergy: Int,
    val newAthleticPerformance: Int,
    val narrative: String
)

object AgingService {

    fun ageOneYear(
        character: ClientCharacter,
        currentAge: Int,
        worldStability: Double = 1.0,
        economicCycle: Double = 1.0,
        luckRoll: Double? = null
    ): AgingResult {
        val nextAge = currentAge + 1

        // 1. Evaluate biological resilience via central FormulaEngine
        val foundation = FormulaEngine.CharacterFoundation(
            intelligence = character.intelligence.toDouble(),
            discipline = character.discipline.toDouble(),
            willpower = character.willpower.toDouble(),
            ambition = character.ambition.toDouble(),
            health = character.health.toDouble(),
            looks = character.looks.toDouble(),
            smarts = character.smarts.toDouble(),
            happiness = character.happiness.toDouble()
        )

        val momentum = FormulaEngine.Momentum(
            trajectoryScore = if (character.health > 70) 0.2 else -0.2,
            fatigueFactor = max(0.0, (100 - character.energy) / 100.0)
        )

        val world = FormulaEngine.WorldVariables(
            economicCycleIndex = economicCycle,
            stabilityIndex = worldStability
        )

        val luck = luckRoll?.let { FormulaEngine.Luck(rawRoll = it) } ?: FormulaEngine.Luck()

        val formulaOutcome = FormulaEngine.evaluate(
            characterFoundation = foundation,
            momentum = momentum,
            worldVariables = world,
            luck = luck
        )

        // Composite resilience factor: 0.65 (high willpower/discipline/health) to 1.35
        val resilienceScore = formulaOutcome.compositeScore
        val resilienceFactor = (1.45 - (resilienceScore / 100.0)).coerceIn(0.65, 1.35)

        // 2. Health degradation
        val baseHealthLoss = when {
            nextAge > 80 -> 4.5
            nextAge > 65 -> 3.0
            nextAge > 45 -> 1.6
            nextAge > 25 -> 0.7
            else -> 0.1
        }
        val healthLoss = (baseHealthLoss * resilienceFactor).roundToInt()
        val newHealth = (character.health - healthLoss).coerceIn(0, 100)

        // 3. Fertility degradation
        val baseFertilityLoss = when {
            nextAge > 45 -> 8.0
            nextAge > 36 -> 4.0
            nextAge > 28 -> 1.5
            else -> 0.2
        }
        val fertilityLoss = (baseFertilityLoss * resilienceFactor).roundToInt()
        val newFertility = (character.fertility - fertilityLoss).coerceIn(0, 100)

        // 4. Energy degradation
        val baseEnergyLoss = when {
            nextAge > 70 -> 3.5
            nextAge > 50 -> 2.0
            nextAge > 30 -> 1.0
            else -> 0.4
        }
        val energyLoss = (baseEnergyLoss * resilienceFactor).roundToInt()
        val newEnergy = (character.energy - energyLoss).coerceIn(0, 100)

        // 5. Athletic Performance degradation (Prompt 02: SHARP DROP past age 30)
        val baseAthleticLoss = when {
            nextAge > 55 -> 8.0
            nextAge > 40 -> 6.0
            nextAge > 30 -> 4.5 // Sharp degradation past 30
            nextAge > 25 -> 1.2
            else -> 0.2
        }
        val athleticLoss = (baseAthleticLoss * resilienceFactor).roundToInt()
        val newAthletic = (character.athleticPerformance - athleticLoss).coerceIn(0, 100)

        val narrativeBuilder = StringBuilder("Reached age $nextAge.")
        if (nextAge > 30 && athleticLoss >= 4) {
            narrativeBuilder.append(" Athletic speed and stamina experienced a sharp post-30 decline (-$athleticLoss ATH).")
        }
        if (healthLoss > 2) {
            narrativeBuilder.append(" Natural aging reduced baseline vitality (-$healthLoss HLT).")
        }
        if (resilienceScore >= 75.0) {
            narrativeBuilder.append(" Strong foundation and discipline cushioned physical degradation.")
        }

        return AgingResult(
            newAge = nextAge,
            previousHealth = character.health,
            previousFertility = character.fertility,
            previousEnergy = character.energy,
            previousAthleticPerformance = character.athleticPerformance,
            newHealth = newHealth,
            newFertility = newFertility,
            newEnergy = newEnergy,
            newAthleticPerformance = newAthletic,
            narrative = narrativeBuilder.toString()
        )
    }
}
