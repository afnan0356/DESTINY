package com.example.services

import com.example.data.local.dao.DestinyDao
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.FamilyEventEntity
import com.example.data.local.entity.LifeEntity
import com.example.data.local.entity.RelationshipEntity
import com.example.data.local.entity.SaveSlotEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

object SaveBackupService {

    private const val BACKUP_VERSION = 1
    private const val APP_IDENTIFIER = "DESTINY_SAVE_BACKUP"

    /**
     * Serializes all Room database tables into a single portable JSON string.
     */
    suspend fun exportSaveData(dao: DestinyDao): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("format", APP_IDENTIFIER)
        root.put("version", BACKUP_VERSION)
        root.put("exportedAt", System.currentTimeMillis())

        // 1. Users
        val users = dao.getAllUsersList()
        val usersArray = JSONArray()
        users.forEach { u ->
            val obj = JSONObject()
            obj.put("id", u.id)
            obj.put("name", u.name)
            obj.put("createdAt", u.createdAt)
            usersArray.put(obj)
        }
        root.put("users", usersArray)

        // 2. Save Slots
        val slots = dao.getAllSaveSlotsList()
        val slotsArray = JSONArray()
        slots.forEach { s ->
            val obj = JSONObject()
            obj.put("id", s.id)
            obj.put("userId", s.userId)
            obj.put("name", s.name)
            obj.put("createdAt", s.createdAt)
            obj.put("updatedAt", s.updatedAt)
            slotsArray.put(obj)
        }
        root.put("saveSlots", slotsArray)

        // 3. Lives
        val lives = dao.getAllLivesList()
        val livesArray = JSONArray()
        lives.forEach { l ->
            val obj = JSONObject()
            obj.put("id", l.id)
            obj.put("saveSlotId", l.saveSlotId)
            obj.put("name", l.name)
            obj.put("birthYear", l.birthYear)
            obj.put("currentAge", l.currentAge)
            obj.put("isDormant", l.isDormant)
            obj.put("createdAt", l.createdAt)
            livesArray.put(obj)
        }
        root.put("lives", livesArray)

        // 4. Characters
        val characters = dao.getAllCharactersList()
        val charactersArray = JSONArray()
        characters.forEach { c ->
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("lifeId", c.lifeId)
            obj.put("intelligence", c.intelligence)
            obj.put("discipline", c.discipline)
            obj.put("willpower", c.willpower)
            obj.put("ambition", c.ambition)
            obj.put("health", c.health)
            obj.put("looks", c.looks)
            obj.put("smarts", c.smarts)
            obj.put("happiness", c.happiness)
            obj.put("fertility", c.fertility)
            obj.put("energy", c.energy)
            obj.put("athleticPerformance", c.athleticPerformance)
            obj.put("karma", c.karma)
            obj.put("bankBalance", c.bankBalance)
            obj.put("gender", c.gender)
            obj.put("sexuality", c.sexuality)
            obj.put("talent", c.talent)
            obj.put("eyeStyle", c.eyeStyle)
            obj.put("eyeColor", c.eyeColor)
            obj.put("skinTone", c.skinTone)
            obj.put("browStyle", c.browStyle)
            obj.put("facialHairStyle", c.facialHairStyle)
            obj.put("facialHairColor", c.facialHairColor)
            obj.put("hairStyle", c.hairStyle)
            obj.put("hairColor", c.hairColor)
            obj.put("birthCity", c.birthCity)
            obj.put("birthCountry", c.birthCountry)
            c.geneticHealthModifier?.let { obj.put("geneticHealthModifier", it) }
            c.geneticIntelligenceModifier?.let { obj.put("geneticIntelligenceModifier", it) }
            c.geneticLooksModifier?.let { obj.put("geneticLooksModifier", it) }
            charactersArray.put(obj)
        }
        root.put("characters", charactersArray)

        // 5. Relationships
        val relationships = dao.getAllRelationshipsList()
        val relationshipsArray = JSONArray()
        relationships.forEach { r ->
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("characterId", r.characterId)
            obj.put("relatedCharacterId", r.relatedCharacterId)
            obj.put("type", r.type)
            obj.put("relationshipStrength", r.relationshipStrength)
            obj.put("status", r.status)
            obj.put("startedAt", r.startedAt)
            r.endedAt?.let { obj.put("endedAt", it) }
            r.sabotageChance?.let { obj.put("sabotageChance", it) }
            obj.put("createdAt", r.createdAt)
            relationshipsArray.put(obj)
        }
        root.put("relationships", relationshipsArray)

        // 6. Family Events
        val events = dao.getAllFamilyEventsList()
        val eventsArray = JSONArray()
        events.forEach { e ->
            val obj = JSONObject()
            obj.put("id", e.id)
            obj.put("characterId", e.characterId)
            obj.put("eventType", e.eventType)
            obj.put("gameYear", e.gameYear)
            obj.put("description", e.description)
            obj.put("createdAt", e.createdAt)
            eventsArray.put(obj)
        }
        root.put("familyEvents", eventsArray)

        root.toString(2)
    }

    /**
     * Parses and restores save data from a JSON string into Room atomically.
     * Returns the total count of entities successfully restored.
     */
    suspend fun importSaveData(dao: DestinyDao, jsonString: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            if (root.optString("format") != APP_IDENTIFIER) {
                return@withContext Result.failure(IllegalArgumentException("Invalid save backup format. Expected $APP_IDENTIFIER"))
            }

            val usersList = mutableListOf<UserEntity>()
            val usersArray = root.optJSONArray("users") ?: JSONArray()
            for (i in 0 until usersArray.length()) {
                val obj = usersArray.getJSONObject(i)
                usersList.add(
                    UserEntity(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }

            val slotsList = mutableListOf<SaveSlotEntity>()
            val slotsArray = root.optJSONArray("saveSlots") ?: JSONArray()
            for (i in 0 until slotsArray.length()) {
                val obj = slotsArray.getJSONObject(i)
                slotsList.add(
                    SaveSlotEntity(
                        id = obj.getString("id"),
                        userId = obj.getString("userId"),
                        name = obj.getString("name"),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                        updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())
                    )
                )
            }

            val livesList = mutableListOf<LifeEntity>()
            val livesArray = root.optJSONArray("lives") ?: JSONArray()
            for (i in 0 until livesArray.length()) {
                val obj = livesArray.getJSONObject(i)
                livesList.add(
                    LifeEntity(
                        id = obj.getString("id"),
                        saveSlotId = obj.getString("saveSlotId"),
                        name = obj.getString("name"),
                        birthYear = obj.getInt("birthYear"),
                        currentAge = obj.getInt("currentAge"),
                        isDormant = obj.optBoolean("isDormant", false),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }

            val charactersList = mutableListOf<CharacterEntity>()
            val charactersArray = root.optJSONArray("characters") ?: JSONArray()
            for (i in 0 until charactersArray.length()) {
                val obj = charactersArray.getJSONObject(i)
                charactersList.add(
                    CharacterEntity(
                        id = obj.getString("id"),
                        lifeId = obj.getString("lifeId"),
                        intelligence = obj.getInt("intelligence"),
                        discipline = obj.getInt("discipline"),
                        willpower = obj.getInt("willpower"),
                        ambition = obj.getInt("ambition"),
                        health = obj.getInt("health"),
                        looks = obj.getInt("looks"),
                        smarts = obj.getInt("smarts"),
                        happiness = obj.getInt("happiness"),
                        fertility = obj.getInt("fertility"),
                        energy = obj.getInt("energy"),
                        athleticPerformance = obj.getInt("athleticPerformance"),
                        karma = obj.optInt("karma", 50),
                        bankBalance = obj.optLong("bankBalance", 1000L),
                        gender = obj.optString("gender", "Non-binary"),
                        sexuality = obj.optString("sexuality", "Bisexual"),
                        talent = obj.optString("talent", "None"),
                        eyeStyle = obj.optString("eyeStyle", "Default"),
                        eyeColor = obj.optString("eyeColor", "Brown"),
                        skinTone = obj.optString("skinTone", "Medium"),
                        browStyle = obj.optString("browStyle", "Default"),
                        facialHairStyle = obj.optString("facialHairStyle", "None"),
                        facialHairColor = obj.optString("facialHairColor", "None"),
                        hairStyle = obj.optString("hairStyle", "Short"),
                        hairColor = obj.optString("hairColor", "Black"),
                        birthCity = obj.optString("birthCity", "Metro"),
                        birthCountry = obj.optString("birthCountry", "Global"),
                        geneticHealthModifier = if (obj.has("geneticHealthModifier")) obj.getDouble("geneticHealthModifier") else null,
                        geneticIntelligenceModifier = if (obj.has("geneticIntelligenceModifier")) obj.getDouble("geneticIntelligenceModifier") else null,
                        geneticLooksModifier = if (obj.has("geneticLooksModifier")) obj.getDouble("geneticLooksModifier") else null
                    )
                )
            }

            val relationshipsList = mutableListOf<RelationshipEntity>()
            val relationshipsArray = root.optJSONArray("relationships") ?: JSONArray()
            for (i in 0 until relationshipsArray.length()) {
                val obj = relationshipsArray.getJSONObject(i)
                relationshipsList.add(
                    RelationshipEntity(
                        id = obj.getString("id"),
                        characterId = obj.getString("characterId"),
                        relatedCharacterId = obj.getString("relatedCharacterId"),
                        type = obj.getString("type"),
                        relationshipStrength = obj.getInt("relationshipStrength"),
                        status = obj.optString("status", "Active"),
                        startedAt = obj.getInt("startedAt"),
                        endedAt = if (obj.has("endedAt")) obj.getInt("endedAt") else null,
                        sabotageChance = if (obj.has("sabotageChance")) obj.getDouble("sabotageChance") else null,
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }

            val eventsList = mutableListOf<FamilyEventEntity>()
            val eventsArray = root.optJSONArray("familyEvents") ?: JSONArray()
            for (i in 0 until eventsArray.length()) {
                val obj = eventsArray.getJSONObject(i)
                eventsList.add(
                    FamilyEventEntity(
                        id = obj.getString("id"),
                        characterId = obj.getString("characterId"),
                        eventType = obj.getString("eventType"),
                        gameYear = obj.getInt("gameYear"),
                        description = obj.getString("description"),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }

            // Atomically write all into Room
            dao.restoreAllData(
                users = usersList,
                slots = slotsList,
                lives = livesList,
                characters = charactersList,
                relationships = relationshipsList,
                familyEvents = eventsList
            )

            val totalRestored = usersList.size + slotsList.size + livesList.size +
                    charactersList.size + relationshipsList.size + eventsList.size
            Result.success(totalRestored)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
