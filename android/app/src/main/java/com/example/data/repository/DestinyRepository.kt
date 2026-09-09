package com.example.data.repository

import com.example.data.local.dao.DestinyDao
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.LifeEntity
import com.example.data.local.entity.SaveSlotEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.ClientCharacter
import com.example.data.model.toClientCharacter
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import kotlin.random.Random

data class LifeRecordSummary(
    val user: UserEntity,
    val saveSlot: SaveSlotEntity,
    val life: LifeEntity,
    val clientCharacter: ClientCharacter
)

data class DatabaseEntityCount(
    val usersCount: Int,
    val saveSlotsCount: Int,
    val livesCount: Int,
    val charactersCount: Int
)

class DestinyRepository(private val dao: DestinyDao) {

    val allLives: Flow<List<LifeEntity>> = dao.getAllLives()
    val activeLives: Flow<List<LifeEntity>> = dao.getActiveLives()
    val dormantLives: Flow<List<LifeEntity>> = dao.getDormantLives()

    /**
     * Executes an end-to-end "New Life" flow creating:
     * User -> SaveSlot -> Life -> Character
     * Atomic transaction guarantees full relational integrity.
     */
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
        intelligence: Int = Random.nextInt(40, 95),
        discipline: Int = Random.nextInt(35, 90),
        willpower: Int = Random.nextInt(40, 90),
        ambition: Int = Random.nextInt(45, 95),
        health: Int = Random.nextInt(50, 98),
        looks: Int = Random.nextInt(40, 90),
        smarts: Int = Random.nextInt(45, 95),
        happiness: Int = Random.nextInt(50, 90),
        fertility: Int = Random.nextInt(70, 95),
        energy: Int = Random.nextInt(80, 100),
        athleticPerformance: Int = Random.nextInt(40, 85),
        geneticHealthModifier: Double? = null,
        geneticIntelligenceModifier: Double? = null,
        geneticLooksModifier: Double? = null,
        internalKarma: Int = Random.nextInt(30, 80)
    ): LifeRecordSummary {
        val user = UserEntity(
            id = "usr_" + UUID.randomUUID().toString().take(8),
            username = playerName.ifBlank { "Player_${Random.nextInt(1000, 9999)}" }
        )

        val saveSlot = SaveSlotEntity(
            id = "slot_" + UUID.randomUUID().toString().take(8),
            userId = user.id,
            slotName = saveSlotName.ifBlank { "Save Slot ${Random.nextInt(1, 100)}" }
        )

        val life = LifeEntity(
            id = "life_" + UUID.randomUUID().toString().take(8),
            saveSlotId = saveSlot.id,
            name = characterName.ifBlank { "Subject_${Random.nextInt(100, 999)}" },
            birthYear = birthYear,
            currentAge = 0,
            isDormant = isDormant
        )

        val character = CharacterEntity(
            id = "chr_" + UUID.randomUUID().toString().take(8),
            lifeId = life.id,
            intelligence = intelligence,
            discipline = discipline,
            willpower = willpower,
            ambition = ambition,
            health = health,
            looks = looks,
            smarts = smarts,
            happiness = happiness,
            fertility = fertility,
            energy = energy,
            athleticPerformance = athleticPerformance,
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
            geneticHealthModifier = geneticHealthModifier,
            geneticIntelligenceModifier = geneticIntelligenceModifier,
            geneticLooksModifier = geneticLooksModifier,
            birthCity = birthCity,
            birthCountry = birthCountry,
            karma = internalKarma // Stored internally, never exposed to client
        )

        dao.createNewLifeCompound(user, saveSlot, life, character)

        return LifeRecordSummary(
            user = user,
            saveSlot = saveSlot,
            life = life,
            clientCharacter = character.toClientCharacter()
        )
    }

    suspend fun updateCharacterAgingStats(
        characterId: String,
        health: Int,
        fertility: Int,
        energy: Int,
        athleticPerformance: Int
    ) {
        dao.updateCharacterAgingStats(characterId, health, fertility, energy, athleticPerformance)
    }

    suspend fun updateLifeAge(lifeId: String, newAge: Int) {
        dao.updateLifeAge(lifeId, newAge)
    }

    suspend fun getLifeSummary(lifeId: String): LifeRecordSummary? {
        val life = dao.getLifeById(lifeId) ?: return null
        val saveSlot = dao.getSaveSlotById(life.saveSlotId) ?: return null
        val user = dao.getUserById(saveSlot.userId) ?: return null
        val character = dao.getCharacterByLifeId(life.id) ?: return null

        return LifeRecordSummary(
            user = user,
            saveSlot = saveSlot,
            life = life,
            clientCharacter = character.toClientCharacter()
        )
    }

    suspend fun setLifeDormancy(lifeId: String, isDormant: Boolean) {
        dao.setLifeDormancy(lifeId, isDormant)
    }

    suspend fun getCounts(): DatabaseEntityCount {
        val slots = dao.getSaveSlotCount()
        val lives = dao.getLifeCount()
        return DatabaseEntityCount(
            usersCount = slots, // In minimal schema each slot was linked to user
            saveSlotsCount = slots,
            livesCount = lives,
            charactersCount = lives
        )
    }

    suspend fun getCharacterById(characterId: String): CharacterEntity? {
        return dao.getCharacterById(characterId)
    }

    suspend fun getClientCharacterById(characterId: String): ClientCharacter? {
        return dao.getCharacterById(characterId)?.toClientCharacter()
    }

    suspend fun updateCharacterBankBalance(characterId: String, newBalance: Long) {
        dao.updateCharacterBankBalance(characterId, newBalance)
    }

    suspend fun saveRelationship(
        characterId: String,
        relatedCharacterId: String,
        type: String,
        relationshipStrength: Int,
        status: String = "Active",
        startedAt: Int,
        endedAt: Int? = null,
        sabotageChance: Double? = null,
        id: String = "rel_${characterId}_${relatedCharacterId}"
    ): com.example.data.model.Relationship {
        val entity = com.example.data.local.entity.RelationshipEntity(
            id = id,
            characterId = characterId,
            relatedCharacterId = relatedCharacterId,
            type = type,
            relationshipStrength = relationshipStrength,
            status = status,
            startedAt = startedAt,
            endedAt = endedAt,
            sabotageChance = sabotageChance
        )
        dao.insertRelationship(entity)
        return com.example.data.model.Relationship(
            id = entity.id,
            characterId = entity.characterId,
            relatedCharacterId = entity.relatedCharacterId,
            type = entity.type,
            relationshipStrength = entity.relationshipStrength,
            status = entity.status,
            startedAt = entity.startedAt,
            endedAt = entity.endedAt,
            sabotageChance = entity.sabotageChance,
            relatedCharacter = getClientCharacterById(entity.relatedCharacterId)
        )
    }

    suspend fun updateRelationshipStatus(
        characterId: String,
        relatedCharacterId: String,
        newStatus: String,
        endedAt: Int?
    ) {
        val existing = dao.getRelationship(characterId, relatedCharacterId)
        if (existing != null) {
            val updated = existing.copy(
                status = newStatus,
                endedAt = endedAt,
                updatedAt = System.currentTimeMillis()
            )
            dao.updateRelationship(updated)
        }
    }

    suspend fun getRelationships(characterId: String): List<com.example.data.model.Relationship> {
        val entities = dao.getRelationshipsForCharacterList(characterId)
        return entities.map { entity ->
            com.example.data.model.Relationship(
                id = entity.id,
                characterId = entity.characterId,
                relatedCharacterId = entity.relatedCharacterId,
                type = entity.type,
                relationshipStrength = entity.relationshipStrength,
                status = entity.status,
                startedAt = entity.startedAt,
                endedAt = entity.endedAt,
                sabotageChance = entity.sabotageChance,
                relatedCharacter = getClientCharacterById(entity.relatedCharacterId),
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }

    suspend fun logFamilyEvent(
        characterId: String,
        relatedCharacterId: String?,
        eventType: String,
        gameYear: Int,
        description: String
    ): com.example.data.model.FamilyEvent {
        val entity = com.example.data.local.entity.FamilyEventEntity(
            id = "fevent_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
            characterId = characterId,
            relatedCharacterId = relatedCharacterId,
            eventType = eventType,
            gameYear = gameYear,
            description = description
        )
        dao.insertFamilyEvent(entity)
        return com.example.data.model.FamilyEvent(
            id = entity.id,
            characterId = entity.characterId,
            relatedCharacterId = entity.relatedCharacterId,
            eventType = entity.eventType,
            gameYear = entity.gameYear,
            description = entity.description,
            createdAt = entity.createdAt
        )
    }

    suspend fun getFamilyEvents(characterId: String): List<com.example.data.model.FamilyEvent> {
        val entities = dao.getFamilyEventsForCharacterList(characterId)
        return entities.map { entity ->
            com.example.data.model.FamilyEvent(
                id = entity.id,
                characterId = entity.characterId,
                relatedCharacterId = entity.relatedCharacterId,
                eventType = entity.eventType,
                gameYear = entity.gameYear,
                description = entity.description,
                createdAt = entity.createdAt
            )
        }
    }

    // --- Portable Save Export / Import (Zero Backend) ---
    suspend fun exportSaveBackup(): String {
        return com.example.services.SaveBackupService.exportSaveData(dao)
    }

    suspend fun importSaveBackup(jsonString: String): Result<Int> {
        return com.example.services.SaveBackupService.importSaveData(dao, jsonString)
    }
}
