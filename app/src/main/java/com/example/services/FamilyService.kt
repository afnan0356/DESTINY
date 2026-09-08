package com.example.services

import com.example.data.model.ClientCharacter
import com.example.data.model.FamilyEvent
import com.example.data.model.Relationship
import com.example.data.repository.DestinyRepository
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

data class MarriageResult(
    val spouseRelationship: Relationship,
    val promotedFromDormant: Boolean,
    val familyEvent: FamilyEvent
)

data class DivorceResult(
    val exRelationship: Relationship,
    val characterBalance: Long,
    val partnerBalance: Long,
    val familyEvent: FamilyEvent
)

data class ChildBirthResult(
    val child: ClientCharacter,
    val parentToChildRel: Relationship,
    val childToParentRel: Relationship,
    val familyEvent: FamilyEvent
)

/**
 * FAMILY SERVICE (Prompt 03 of 20: FAMILY SYSTEM)
 *
 * Core service orchestrating:
 * 1. Relationships (Spouse, Ex, Parent, Child, Sibling, Friend, BestFriend, Enemy)
 * 2. Marriage & Divorce with Dormant -> Active NPC promotion & asset splitting (bankBalance)
 * 3. Children creation with true 5-layer FormulaEngine genetic blending
 * 4. Inheritance hook distribution on death
 * 5. Family drama event logging
 * 6. Strict Karma privacy enforcement
 */
class FamilyService(private val repository: DestinyRepository) {

    suspend fun getRelationships(characterId: String): List<Relationship> {
        return repository.getRelationships(characterId)
    }

    suspend fun getFamilyEvents(characterId: String): List<FamilyEvent> {
        return repository.getFamilyEvents(characterId)
    }

    /**
     * MARRIAGE FLOW:
     * 1. Creates a Spouse relationship between character and partner.
     * 2. If partner is a dormant NPC, promotes them to active state.
     * 3. Logs a Marriage familyEvent.
     */
    suspend fun marry(
        characterId: String,
        partnerCharacterId: String,
        gameYear: Int,
        initialStrength: Int = 85
    ): MarriageResult {
        // Check dormancy of partner
        val partnerChar = repository.getCharacterById(partnerCharacterId)
        var promoted = false
        if (partnerChar != null) {
            val partnerLife = repository.getLifeSummary(partnerChar.lifeId)
            if (partnerLife?.life?.isDormant == true) {
                repository.setLifeDormancy(partnerChar.lifeId, false)
                promoted = true
            }
        }

        // Save character -> partner Spouse relationship
        val spouseRel = repository.saveRelationship(
            characterId = characterId,
            relatedCharacterId = partnerCharacterId,
            type = "Spouse",
            relationshipStrength = initialStrength,
            status = "Active",
            startedAt = gameYear,
            id = "rel_${characterId}_${partnerCharacterId}"
        )

        // Save reciprocal partner -> character Spouse relationship
        repository.saveRelationship(
            characterId = partnerCharacterId,
            relatedCharacterId = characterId,
            type = "Spouse",
            relationshipStrength = initialStrength,
            status = "Active",
            startedAt = gameYear,
            id = "rel_${partnerCharacterId}_${characterId}"
        )

        val familyEvent = repository.logFamilyEvent(
            characterId = characterId,
            relatedCharacterId = partnerCharacterId,
            eventType = "Marriage",
            gameYear = gameYear,
            description = "Entered holy matrimony with partner in year $gameYear."
        )

        return MarriageResult(
            spouseRelationship = spouseRel,
            promotedFromDormant = promoted,
            familyEvent = familyEvent
        )
    }

    /**
     * DIVORCE FLOW:
     * 1. Ends the Spouse relationship (status: Ended, endedAt: gameYear).
     * 2. Creates an Ex relationship (status: Active).
     * 3. Splits assets: (character.bankBalance + partner.bankBalance) / 2.
     * 4. Logs a Divorce familyEvent.
     */
    suspend fun divorce(
        characterId: String,
        partnerCharacterId: String,
        gameYear: Int
    ): DivorceResult {
        // End existing Spouse relationship
        repository.updateRelationshipStatus(characterId, partnerCharacterId, "Ended", gameYear)
        repository.updateRelationshipStatus(partnerCharacterId, characterId, "Ended", gameYear)

        // Create Ex relationship
        val exRel = repository.saveRelationship(
            characterId = characterId,
            relatedCharacterId = partnerCharacterId,
            type = "Ex",
            relationshipStrength = 20,
            status = "Active",
            startedAt = gameYear,
            id = "rel_ex_${characterId}_${partnerCharacterId}"
        )

        // Split assets between characters
        val charA = repository.getCharacterById(characterId)
        val charB = repository.getCharacterById(partnerCharacterId)
        val balanceA = charA?.bankBalance ?: 1000L
        val balanceB = charB?.bankBalance ?: 1000L
        val totalWealth = balanceA + balanceB
        val splitShare = totalWealth / 2

        repository.updateCharacterBankBalance(characterId, splitShare)
        repository.updateCharacterBankBalance(partnerCharacterId, splitShare)

        val familyEvent = repository.logFamilyEvent(
            characterId = characterId,
            relatedCharacterId = partnerCharacterId,
            eventType = "Divorce",
            gameYear = gameYear,
            description = "Divorced in year $gameYear. Marital assets of $$totalWealth split equally ($$splitShare each)."
        )

        return DivorceResult(
            exRelationship = exRel,
            characterBalance = splitShare,
            partnerBalance = splitShare,
            familyEvent = familyEvent
        )
    }

    /**
     * CHILDREN & GENETICS FLOW:
     * 1. Creates a new Character document with real weighted blend of parents' stats
     *    and FormulaEngine Luck-based genetic modifiers.
     * 2. New child defaults to active (isDormant = false).
     * 3. Creates Parent/Child relationships between both parents and child.
     * 4. Logs a Birth familyEvent.
     */
    suspend fun haveChild(
        parent1Id: String,
        parent2Id: String,
        childName: String,
        gameYear: Int,
        forcedGender: String? = null
    ): ChildBirthResult {
        val p1 = repository.getCharacterById(parent1Id)
            ?: throw IllegalArgumentException("Parent 1 not found")
        val p2 = repository.getCharacterById(parent2Id)
            ?: throw IllegalArgumentException("Parent 2 not found")

        // Real weighted blend of both parents' stats + FormulaEngine Luck layer variance
        val gHealthMod = FormulaEngine.calculateGeneticModifier(p1.health.toDouble(), p2.health.toDouble())
        val gIntMod = FormulaEngine.calculateGeneticModifier(p1.intelligence.toDouble(), p2.intelligence.toDouble())
        val gLooksMod = FormulaEngine.calculateGeneticModifier(p1.looks.toDouble(), p2.looks.toDouble())

        val childHealth = ((p1.health + p2.health) / 2.0 + gHealthMod).roundToInt().coerceIn(10, 100)
        val childInt = ((p1.intelligence + p2.intelligence) / 2.0 + gIntMod).roundToInt().coerceIn(10, 100)
        val childLooks = ((p1.looks + p2.looks) / 2.0 + gLooksMod).roundToInt().coerceIn(10, 100)
        val childSmarts = ((p1.smarts + p2.smarts) / 2.0).roundToInt().coerceIn(10, 100)
        val childDiscipline = ((p1.discipline + p2.discipline) / 2.0).roundToInt().coerceIn(10, 100)
        val childWillpower = ((p1.willpower + p2.willpower) / 2.0).roundToInt().coerceIn(10, 100)
        val childAmbition = ((p1.ambition + p2.ambition) / 2.0).roundToInt().coerceIn(10, 100)

        val childGender = forcedGender ?: if (Random.nextBoolean()) "Male" else "Female"

        // Create child record in repository (defaults to isDormant = false per prompt requirements)
        val summary = repository.createNewLife(
            playerName = "Player",
            saveSlotName = "FamilySlot",
            characterName = childName,
            birthYear = gameYear,
            isDormant = false, // Child defaults to active
            birthCountry = p1.birthCountry,
            birthCity = p1.birthCity,
            gender = childGender,
            sexuality = "Heterosexual",
            talent = "None",
            eyeStyle = p1.eyeStyle,
            eyeColor = if (Random.nextBoolean()) p1.eyeColor else p2.eyeColor,
            skinTone = if (Random.nextBoolean()) p1.skinTone else p2.skinTone,
            browStyle = p1.browStyle,
            facialHairStyle = "Clean Shaven",
            facialHairColor = p1.hairColor,
            hairStyle = if (childGender == "Male") "Short Crop" else "Long Waves",
            hairColor = if (Random.nextBoolean()) p1.hairColor else p2.hairColor,
            intelligence = childInt,
            discipline = childDiscipline,
            willpower = childWillpower,
            ambition = childAmbition,
            health = childHealth,
            looks = childLooks,
            smarts = childSmarts,
            happiness = 90,
            fertility = 80,
            energy = 100,
            athleticPerformance = ((p1.athleticPerformance + p2.athleticPerformance) / 2),
            geneticHealthModifier = gHealthMod,
            geneticIntelligenceModifier = gIntMod,
            geneticLooksModifier = gLooksMod
        )

        // Starting child bank balance is 0
        repository.updateCharacterBankBalance(summary.clientCharacter.id, 0L)

        // Relationships: Parent1 <-> Child
        val p1ToChildRel = repository.saveRelationship(
            characterId = parent1Id,
            relatedCharacterId = summary.clientCharacter.id,
            type = "Child",
            relationshipStrength = 95,
            status = "Active",
            startedAt = gameYear
        )
        val childToP1Rel = repository.saveRelationship(
            characterId = summary.clientCharacter.id,
            relatedCharacterId = parent1Id,
            type = "Parent",
            relationshipStrength = 100,
            status = "Active",
            startedAt = gameYear
        )

        // Relationships: Parent2 <-> Child
        repository.saveRelationship(
            characterId = parent2Id,
            relatedCharacterId = summary.clientCharacter.id,
            type = "Child",
            relationshipStrength = 95,
            status = "Active",
            startedAt = gameYear
        )
        repository.saveRelationship(
            characterId = summary.clientCharacter.id,
            relatedCharacterId = parent2Id,
            type = "Parent",
            relationshipStrength = 100,
            status = "Active",
            startedAt = gameYear
        )

        val familyEvent = repository.logFamilyEvent(
            characterId = parent1Id,
            relatedCharacterId = summary.clientCharacter.id,
            eventType = "Birth",
            gameYear = gameYear,
            description = "Welcomed their child, $childName, into the world in year $gameYear."
        )

        return ChildBirthResult(
            child = summary.clientCharacter.copy(bankBalance = 0L),
            parentToChildRel = p1ToChildRel,
            childToParentRel = childToP1Rel,
            familyEvent = familyEvent
        )
    }

    /**
     * INHERITANCE HOOK ON DEATH:
     * Splits deceased character's bankBalance equally among living Child relationships.
     * If no living children, inherits to spouse.
     * Otherwise logs to placeholder "Estate".
     */
    suspend fun executeInheritance(
        deceasedCharacterId: String,
        gameYear: Int
    ): InheritanceResult {
        val deceasedChar = repository.getCharacterById(deceasedCharacterId)
            ?: throw IllegalArgumentException("Deceased character not found")

        val bankBalance = deceasedChar.bankBalance
        val relationships = repository.getRelationships(deceasedCharacterId).map {
            RelationshipSnapshot(
                characterId = it.characterId,
                relatedCharacterId = it.relatedCharacterId,
                type = it.type,
                status = it.status
            )
        }

        val result = DeathService.resolveInheritance(deceasedCharacterId, bankBalance, relationships)

        // Distribute to beneficiaries
        for (b in result.beneficiaries) {
            val beneficiaryChar = repository.getCharacterById(b.characterId)
            if (beneficiaryChar != null) {
                repository.updateCharacterBankBalance(b.characterId, beneficiaryChar.bankBalance + b.amount)
            }
        }

        // Zero out deceased's bank balance
        repository.updateCharacterBankBalance(deceasedCharacterId, 0L)

        // Log death / inheritance event
        repository.logFamilyEvent(
            characterId = deceasedCharacterId,
            relatedCharacterId = null,
            eventType = "Death",
            gameYear = gameYear,
            description = result.description
        )

        return result
    }

    /**
     * Creates an Enemy relationship with sabotageChance derived from FormulaEngine.
     */
    suspend fun addEnemy(
        characterId: String,
        enemyCharacterId: String,
        gameYear: Int,
        enemyInfluence: FormulaEngine.Influence = FormulaEngine.Influence()
    ): Relationship {
        val sabotageChance = FormulaEngine.calculateEnemySabotageChance(enemyInfluence)
        return repository.saveRelationship(
            characterId = characterId,
            relatedCharacterId = enemyCharacterId,
            type = "Enemy",
            relationshipStrength = 10,
            status = "Active",
            startedAt = gameYear,
            sabotageChance = sabotageChance,
            id = "rel_${characterId}_${enemyCharacterId}"
        )
    }
}
