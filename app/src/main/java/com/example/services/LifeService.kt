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
        isDormant: Boolean = false,
        birthCountry: String = "United States",
        birthCity: String = "New York",
        gender: String = "Male",
        sexuality: String = "Heterosexual",
        talent: String = "None",
        eyeStyle: String = "Almond",
        eyeColor: String = "Brown",
        skinTone: String = "Fair",
        browStyle: String = "Straight",
        facialHairStyle: String = "Clean Shaven",
        facialHairColor: String = "Black",
        hairStyle: String = "Short Crop",
        hairColor: String = "Black",
        intelligence: Int? = null,
        discipline: Int? = null,
        willpower: Int? = null,
        ambition: Int? = null,
        health: Int? = null,
        looks: Int? = null,
        smarts: Int? = null,
        happiness: Int? = null,
        fertility: Int? = null,
        energy: Int? = null,
        athleticPerformance: Int? = null
    ): LifeRecordSummary {
        return repository.createNewLife(
            playerName = playerName,
            saveSlotName = saveSlotName,
            characterName = characterName,
            birthYear = birthYear,
            isDormant = isDormant,
            birthCountry = birthCountry,
            birthCity = birthCity,
            gender = gender,
            sexuality = sexuality,
            talent = talent,
            eyeStyle = eyeStyle,
            eyeColor = eyeColor,
            skinTone = skinTone,
            browStyle = browStyle,
            facialHairStyle = facialHairStyle,
            facialHairColor = facialHairColor,
            hairStyle = hairStyle,
            hairColor = hairColor,
            intelligence = intelligence ?: (45..90).random(),
            discipline = discipline ?: (40..85).random(),
            willpower = willpower ?: (40..85).random(),
            ambition = ambition ?: (45..90).random(),
            health = health ?: (60..98).random(),
            looks = looks ?: (40..90).random(),
            smarts = smarts ?: (45..90).random(),
            happiness = happiness ?: (50..90).random(),
            fertility = fertility ?: (70..95).random(),
            energy = energy ?: (80..100).random(),
            athleticPerformance = athleticPerformance ?: (40..85).random()
        )
    }

    suspend fun updateCharacterAgingStats(
        characterId: String,
        health: Int,
        fertility: Int,
        energy: Int,
        athleticPerformance: Int
    ) {
        repository.updateCharacterAgingStats(characterId, health, fertility, energy, athleticPerformance)
    }

    suspend fun updateLifeAge(lifeId: String, newAge: Int) {
        repository.updateLifeAge(lifeId, newAge)
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
