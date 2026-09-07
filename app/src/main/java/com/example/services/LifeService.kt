package com.example.services

import com.example.data.local.entity.LifeEntity
import com.example.data.model.ClientCharacter
import com.example.data.repository.DestinyRepository
import com.example.data.repository.LifeRecordSummary
import kotlinx.coroutines.flow.Flow

/**
 * LIFE SERVICE (Prompt 01 of 20 Foundation)
 *
 * Core service managing:
 * 1. Life creation lifecycle (User -> SaveSlot -> Life -> Character)
 * 2. NPC Simulation Model:
 *    - "dormant": NPC is lightweight. Attributes and stats persist in the database,
 *      but no annual simulation logic, relationship updates, or event checks are executed.
 *    - "active": NPC has entered player interaction horizon. The simulation engine
 *      actively calculates events, momentum shifts, and decisions.
 * 3. Safe client encapsulation (Karma privacy enforcement).
 */
class LifeService(private val repository: DestinyRepository) {

    val allLives: Flow<List<LifeEntity>> = repository.allLives
    val activeLives: Flow<List<LifeEntity>> = repository.activeLives
    val dormantLives: Flow<List<LifeEntity>> = repository.dormantLives

    suspend fun createNewLife(
        playerName: String,
        saveSlotName: String,
        characterName: String,
        birthYear: Int = 2000,
        isDormant: Boolean = false
    ): LifeRecordSummary {
        return repository.createNewLife(
            playerName = playerName,
            saveSlotName = saveSlotName,
            characterName = characterName,
            birthYear = birthYear,
            isDormant = isDormant
        )
    }

    suspend fun getLifeDetails(lifeId: String): LifeRecordSummary? {
        return repository.getLifeSummary(lifeId)
    }

    /**
     * Transition NPC between Dormant and Active states.
     * Architectural guarantee: Switching is lightweight and zero-data-loss.
     */
    suspend fun setNpcSimulationState(lifeId: String, isDormant: Boolean) {
        repository.setLifeDormancy(lifeId, isDormant)
    }

    /**
     * Tests a resolution check for a character using the shared FormulaEngine.
     * Extracts intrinsic foundation stats from the client character and runs through the 5-layer pipeline.
     */
    fun evaluateCharacterProspect(
        character: ClientCharacter,
        trajectoryMomentum: Double = 0.0,
        luckRoll: Double? = null
    ): FormulaEngine.Outcome {
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
            trajectoryScore = trajectoryMomentum,
            recentStreak = 0,
            fatigueFactor = 0.0
        )

        val luck = luckRoll?.let { FormulaEngine.Luck(rawRoll = it) } ?: FormulaEngine.Luck()

        return FormulaEngine.evaluate(
            characterFoundation = foundation,
            momentum = momentum,
            luck = luck
        )
    }
}
