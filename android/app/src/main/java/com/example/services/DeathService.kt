package com.example.services

import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * DEATH CHECK SERVICE (Prompt 02 of 20: CHARACTER SYSTEM)
 *
 * Computes annual mortality probability on yearly ticks using:
 * - Age
 * - Health
 * - Lifestyle modifier (default neutral 0.0)
 * - Luck & Foundation through FormulaEngine
 *
 * ARCHITECTURAL MANDATE:
 * Returns isDead: Boolean and cause: String?
 * Wire it into the yearly tick loop (logs the result rather than ending game).
 */
data class DeathCheckResult(
    val isDead: Boolean,
    val cause: String?,
    val mortalityProbability: Double,
    val roll: Double
)

data class RelationshipSnapshot(
    val characterId: String,
    val relatedCharacterId: String,
    val type: String,
    val status: String
)

data class InheritanceBeneficiary(
    val characterId: String,
    val amount: Long,
    val role: String
)

data class InheritanceResult(
    val deceasedCharacterId: String,
    val totalDistributed: Long,
    val perChildShare: Long = 0L,
    val livingChildrenCount: Int = 0,
    val inheritedBySpouse: Boolean = false,
    val transferredToEstate: Boolean = false,
    val beneficiaries: List<InheritanceBeneficiary> = emptyList(),
    val description: String
)

object DeathService {

    fun calculateMortalityProbability(
        age: Int,
        health: Int,
        lifestyleModifier: Double = 0.0
    ): Double {
        // 1. Base Age Risk
        val baseAgeRisk = when {
            age > 95 -> 0.55 + (age - 95) * 0.08
            age > 85 -> 0.22 + (age - 85) * 0.033
            age > 75 -> 0.07 + (age - 75) * 0.015
            age > 60 -> 0.018 + (age - 60) * 0.0035
            age > 40 -> 0.002 + (age - 40) * 0.0008
            else -> 0.0004 + (age * 0.00004)
        }

        // 2. Health Multiplier
        val healthMultiplier = when {
            health <= 5 -> 9.0
            health <= 15 -> 5.0
            health <= 30 -> 3.0
            health <= 50 -> 1.8
            health <= 70 -> 1.0
            health <= 90 -> 0.55
            else -> 0.25 // Maximum health confers highest survival
        }

        val rawProb = (baseAgeRisk * healthMultiplier) * (1.0 + lifestyleModifier)
        return rawProb.coerceIn(0.0001, 0.99)
    }

    fun checkMortality(
        age: Int,
        health: Int,
        intelligence: Int = 50,
        discipline: Int = 50,
        willpower: Int = 50,
        ambition: Int = 50,
        smarts: Int = 50,
        happiness: Int = 50,
        lifestyleModifier: Double = 0.0,
        stabilityIndex: Double = 1.0,
        forcedLuckRoll: Double? = null
    ): DeathCheckResult {
        val baseProbability = calculateMortalityProbability(age, health, lifestyleModifier)

        // Evaluate luck & resilience via FormulaEngine
        val formulaOutcome = FormulaEngine.evaluate(
            characterFoundation = FormulaEngine.CharacterFoundation(
                intelligence = intelligence.toDouble(),
                discipline = discipline.toDouble(),
                willpower = willpower.toDouble(),
                ambition = ambition.toDouble(),
                health = health.toDouble(),
                smarts = smarts.toDouble(),
                happiness = happiness.toDouble()
            ),
            worldVariables = FormulaEngine.WorldVariables(stabilityIndex = stabilityIndex),
            luck = forcedLuckRoll?.let { FormulaEngine.Luck(rawRoll = it) } ?: FormulaEngine.Luck()
        )

        var adjustedProbability = baseProbability
        if (formulaOutcome.tier == FormulaEngine.OutcomeTier.CRITICAL_SUCCESS) {
            adjustedProbability *= 0.5 // Fortunate survival
        } else if (formulaOutcome.tier == FormulaEngine.OutcomeTier.CRITICAL_FAILURE) {
            adjustedProbability *= 1.4 // Unfortunate spike
        }
        adjustedProbability = adjustedProbability.coerceIn(0.0001, 0.999)

        val roll = forcedLuckRoll ?: Random.nextDouble(0.0, 1.0)
        val isDead = roll < adjustedProbability

        val cause = if (isDead) {
            when {
                health <= 10 -> "Critical Organ Failure & Systemic Collapse"
                age >= 85 -> "Natural Complications of Advanced Age"
                age >= 60 -> "Acute Cardiovascular Arrest"
                age < 35 && formulaOutcome.tier == FormulaEngine.OutcomeTier.CRITICAL_FAILURE -> "Fatal Sudden Accident"
                else -> "Sudden Medical Complications"
            }
        } else null

        return DeathCheckResult(
            isDead = isDead,
            cause = cause,
            mortalityProbability = adjustedProbability,
            roll = roll
        )
    }

    /**
     * INHERITANCE HOOK (Prompt 03)
     * Resolves distribution of deceased's bankBalance:
     * - Splits equally among living Child relationships
     * - If no living children, inherits to living spouse
     * - Otherwise transfers to placeholder "Estate"
     */
    fun resolveInheritance(
        deceasedCharacterId: String,
        bankBalance: Long,
        relationships: List<RelationshipSnapshot>
    ): InheritanceResult {
        val livingChildren = relationships.filter { it.type == "Child" && it.status == "Active" }
        val livingSpouse = relationships.find { it.type == "Spouse" && it.status == "Active" }

        return when {
            livingChildren.isNotEmpty() -> {
                val perChild = bankBalance / livingChildren.size
                val beneficiaries = livingChildren.map {
                    InheritanceBeneficiary(
                        characterId = it.relatedCharacterId,
                        amount = perChild,
                        role = "Child"
                    )
                }
                InheritanceResult(
                    deceasedCharacterId = deceasedCharacterId,
                    totalDistributed = perChild * livingChildren.size,
                    perChildShare = perChild,
                    livingChildrenCount = livingChildren.size,
                    inheritedBySpouse = false,
                    transferredToEstate = false,
                    beneficiaries = beneficiaries,
                    description = "Estate of $$bankBalance divided equally among ${livingChildren.size} living children ($$perChild each)."
                )
            }
            livingSpouse != null -> {
                InheritanceResult(
                    deceasedCharacterId = deceasedCharacterId,
                    totalDistributed = bankBalance,
                    perChildShare = 0L,
                    livingChildrenCount = 0,
                    inheritedBySpouse = true,
                    transferredToEstate = false,
                    beneficiaries = listOf(
                        InheritanceBeneficiary(
                            characterId = livingSpouse.relatedCharacterId,
                            amount = bankBalance,
                            role = "Spouse"
                        )
                    ),
                    description = "Estate of $$bankBalance inherited entirely by surviving spouse."
                )
            }
            else -> {
                InheritanceResult(
                    deceasedCharacterId = deceasedCharacterId,
                    totalDistributed = 0L,
                    perChildShare = 0L,
                    livingChildrenCount = 0,
                    inheritedBySpouse = false,
                    transferredToEstate = true,
                    beneficiaries = emptyList(),
                    description = "[Estate Placeholder] No living heirs. $$bankBalance transferred to Estate holding."
                )
            }
        }
    }
}
