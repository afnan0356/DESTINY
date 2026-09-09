package com.example.services

import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * FORMULA ENGINE ARCHITECTURE (Prompt 01 of 20 Foundation)
 *
 * A single shared engine evaluating the 5 core layers of the Destiny simulation:
 * 1. Character Foundation (intrinsic character stats)
 * 2. Momentum (historical trajectory, recent momentum)
 * 3. Influence (social capital, wealth, network leverage)
 * 4. World Variables (macroeconomics, political stability, unrest)
 * 5. Luck (stochastic variance, fate roll)
 *
 * Future systems (Prompts 02-20: Family, Career, Politics, Military, etc.)
 * MUST route their resolution checks through this central engine rather
 * than implementing inline math.
 */
object FormulaEngine {

    data class CharacterFoundation(
        val intelligence: Double = 50.0,
        val discipline: Double = 50.0,
        val willpower: Double = 50.0,
        val ambition: Double = 50.0,
        val health: Double = 50.0,
        val looks: Double = 50.0,
        val smarts: Double = 50.0,
        val happiness: Double = 50.0
    ) {
        fun compositeAverage(): Double =
            (intelligence + discipline + willpower + ambition + health + looks + smarts + happiness) / 8.0
    }

    data class Momentum(
        val trajectoryScore: Double = 0.0,      // Normalized -1.0 to +1.0
        val recentStreak: Int = 0,               // Positive for wins, negative for setbacks
        val fatigueFactor: Double = 0.0         // 0.0 (fresh) to 1.0 (exhausted)
    )

    data class Influence(
        val socialCapital: Double = 0.0,        // 0.0 to 100.0
        val familyLeverage: Double = 0.0,       // 0.0 to 100.0
        val factionReputation: Double = 0.0     // -100.0 to +100.0
    )

    data class WorldVariables(
        val economicCycleIndex: Double = 1.0,   // 0.5 (depression) to 1.5 (boom)
        val stabilityIndex: Double = 1.0,       // 0.0 (chaos/war) to 1.0 (tranquility)
        val opportunityRating: Double = 1.0     // Sector/era opportunity multiplier
    )

    data class Luck(
        val rawRoll: Double = Random.nextDouble(0.0, 1.0),
        val karmaModifier: Double = 0.0         // Hidden internal nudge derived from hidden karma
    )

    data class Weights(
        val wFoundation: Double = 0.35,
        val wMomentum: Double = 0.20,
        val wInfluence: Double = 0.15,
        val wWorld: Double = 0.15,
        val wLuck: Double = 0.15
    )

    enum class OutcomeTier {
        CRITICAL_FAILURE,
        FAILURE,
        MARGINAL,
        SUCCESS,
        CRITICAL_SUCCESS
    }

    data class Outcome(
        val compositeScore: Double,
        val tier: OutcomeTier,
        val foundationContribution: Double,
        val momentumContribution: Double,
        val influenceContribution: Double,
        val worldContribution: Double,
        val luckContribution: Double,
        val isCritical: Boolean
    )

    /**
     * Executes the 5-layer formula engine stub.
     * Weights and specific curve modifiers will be refined in Prompts 02-20.
     */
    fun evaluate(
        characterFoundation: CharacterFoundation,
        momentum: Momentum = Momentum(),
        influence: Influence = Influence(),
        worldVariables: WorldVariables = WorldVariables(),
        luck: Luck = Luck(),
        weights: Weights = Weights()
    ): Outcome {
        // 1. Foundation: Normalized base (0..100)
        val foundationScore = characterFoundation.compositeAverage().coerceIn(0.0, 100.0)

        // 2. Momentum: Normalized from (-1..+1) onto (0..100) scale
        val momentumScore = ((momentum.trajectoryScore * 50.0) + 50.0 - (momentum.fatigueFactor * 20.0)).coerceIn(0.0, 100.0)

        // 3. Influence: Composite normalized to (0..100)
        val influenceScore = ((influence.socialCapital * 0.5) + (influence.familyLeverage * 0.3) + ((influence.factionReputation + 100.0) * 0.1)).coerceIn(0.0, 100.0)

        // 4. World: Macro environment scaling (0..100)
        val worldScore = ((worldVariables.economicCycleIndex * 40.0) + (worldVariables.stabilityIndex * 40.0) + (worldVariables.opportunityRating * 20.0)).coerceIn(0.0, 100.0)

        // 5. Luck: (0..100) with karma bias
        val luckScore = ((luck.rawRoll * 100.0) + luck.karmaModifier).coerceIn(0.0, 100.0)

        // Total weighted outcome
        val totalScore = (
            (foundationScore * weights.wFoundation) +
            (momentumScore * weights.wMomentum) +
            (influenceScore * weights.wInfluence) +
            (worldScore * weights.wWorld) +
            (luckScore * weights.wLuck)
        ).coerceIn(0.0, 100.0)

        val tier = when {
            totalScore >= 85.0 -> OutcomeTier.CRITICAL_SUCCESS
            totalScore >= 65.0 -> OutcomeTier.SUCCESS
            totalScore >= 45.0 -> OutcomeTier.MARGINAL
            totalScore >= 25.0 -> OutcomeTier.FAILURE
            else -> OutcomeTier.CRITICAL_FAILURE
        }

        return Outcome(
            compositeScore = totalScore,
            tier = tier,
            foundationContribution = foundationScore * weights.wFoundation,
            momentumContribution = momentumScore * weights.wMomentum,
            influenceContribution = influenceScore * weights.wInfluence,
            worldContribution = worldScore * weights.wWorld,
            luckContribution = luckScore * weights.wLuck,
            isCritical = tier == OutcomeTier.CRITICAL_SUCCESS || tier == OutcomeTier.CRITICAL_FAILURE
        )
    }

    /**
     * Calculates sabotage chance for Enemy relationship derived from enemy's Influence stat.
     * Sabotage chance ranges from 5.0% to 75.0% based on enemy influence.
     */
    fun calculateEnemySabotageChance(influence: Influence): Double {
        val influenceScore = ((influence.socialCapital * 0.5) + (influence.familyLeverage * 0.3) + ((influence.factionReputation + 100.0) * 0.1)).coerceIn(0.0, 100.0)
        return (5.0 + (influenceScore * 0.7)).coerceIn(5.0, 75.0)
    }

    /**
     * Blends genetic modifiers for a child from both parents' stats + Luck layer variance.
     * Evaluates a weighted blend of parents' corresponding stats, with a variance component
     * pulled from the FormulaEngine's Luck layer (using luck.rawRoll and luck.karmaModifier).
     */
    fun calculateGeneticModifier(
        parent1Stat: Double,
        parent2Stat: Double,
        luck: Luck = Luck()
    ): Double {
        val parentAvg = (parent1Stat + parent2Stat) / 2.0
        val luckVariance = ((luck.rawRoll - 0.5) * 20.0) + (luck.karmaModifier * 0.1)
        val modifier = ((parentAvg - 50.0) * 0.4) + luckVariance
        return modifier.coerceIn(-25.0, 25.0)
    }
}
