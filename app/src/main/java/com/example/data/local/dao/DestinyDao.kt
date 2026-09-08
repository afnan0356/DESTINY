package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.LifeEntity
import com.example.data.local.entity.SaveSlotEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DestinyDao {

    // --- User Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    // --- SaveSlot Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaveSlot(slot: SaveSlotEntity)

    @Query("SELECT * FROM save_slots WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getSaveSlotsForUser(userId: String): Flow<List<SaveSlotEntity>>

    @Query("SELECT * FROM save_slots WHERE id = :slotId LIMIT 1")
    suspend fun getSaveSlotById(slotId: String): SaveSlotEntity?

    @Query("SELECT COUNT(*) FROM save_slots")
    suspend fun getSaveSlotCount(): Int

    // --- Life Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLife(life: LifeEntity)

    @Update
    suspend fun updateLife(life: LifeEntity)

    @Query("SELECT * FROM lives WHERE id = :lifeId LIMIT 1")
    suspend fun getLifeById(lifeId: String): LifeEntity?

    @Query("SELECT * FROM lives WHERE saveSlotId = :saveSlotId ORDER BY createdAt DESC")
    fun getLivesForSlot(saveSlotId: String): Flow<List<LifeEntity>>

    @Query("SELECT * FROM lives ORDER BY createdAt DESC")
    fun getAllLives(): Flow<List<LifeEntity>>

    @Query("SELECT * FROM lives WHERE isDormant = 0")
    fun getActiveLives(): Flow<List<LifeEntity>>

    @Query("SELECT * FROM lives WHERE isDormant = 1")
    fun getDormantLives(): Flow<List<LifeEntity>>

    @Query("UPDATE lives SET isDormant = :isDormant WHERE id = :lifeId")
    suspend fun setLifeDormancy(lifeId: String, isDormant: Boolean)

    @Query("UPDATE lives SET currentAge = :newAge WHERE id = :lifeId")
    suspend fun updateLifeAge(lifeId: String, newAge: Int)

    @Query("SELECT COUNT(*) FROM lives")
    suspend fun getLifeCount(): Int

    // --- Character Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity)

    @Update
    suspend fun updateCharacter(character: CharacterEntity)

    @Query("UPDATE characters SET health = :health, fertility = :fertility, energy = :energy, athleticPerformance = :athleticPerformance WHERE id = :characterId")
    suspend fun updateCharacterAgingStats(
        characterId: String,
        health: Int,
        fertility: Int,
        energy: Int,
        athleticPerformance: Int
    )

    @Query("SELECT * FROM characters WHERE lifeId = :lifeId LIMIT 1")
    suspend fun getCharacterByLifeId(lifeId: String): CharacterEntity?

    @Query("SELECT * FROM characters WHERE id = :characterId LIMIT 1")
    suspend fun getCharacterById(characterId: String): CharacterEntity?

    @Query("UPDATE characters SET bankBalance = :newBalance WHERE id = :characterId")
    suspend fun updateCharacterBankBalance(characterId: String, newBalance: Long)

    // --- Relationship Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelationship(relationship: com.example.data.local.entity.RelationshipEntity)

    @Update
    suspend fun updateRelationship(relationship: com.example.data.local.entity.RelationshipEntity)

    @Query("SELECT * FROM relationships WHERE characterId = :characterId")
    fun getRelationshipsForCharacter(characterId: String): Flow<List<com.example.data.local.entity.RelationshipEntity>>

    @Query("SELECT * FROM relationships WHERE characterId = :characterId")
    suspend fun getRelationshipsForCharacterList(characterId: String): List<com.example.data.local.entity.RelationshipEntity>

    @Query("SELECT * FROM relationships WHERE characterId = :characterId AND relatedCharacterId = :relatedId LIMIT 1")
    suspend fun getRelationship(characterId: String, relatedId: String): com.example.data.local.entity.RelationshipEntity?

    // --- Family Event Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyEvent(event: com.example.data.local.entity.FamilyEventEntity)

    @Query("SELECT * FROM family_events WHERE characterId = :characterId ORDER BY gameYear DESC, createdAt DESC")
    fun getFamilyEventsForCharacter(characterId: String): Flow<List<com.example.data.local.entity.FamilyEventEntity>>

    @Query("SELECT * FROM family_events WHERE characterId = :characterId ORDER BY gameYear DESC, createdAt DESC")
    suspend fun getFamilyEventsForCharacterList(characterId: String): List<com.example.data.local.entity.FamilyEventEntity>

    // --- Atomic Compound Creation ---
    @Transaction
    suspend fun createNewLifeCompound(
        user: UserEntity,
        saveSlot: SaveSlotEntity,
        life: LifeEntity,
        character: CharacterEntity
    ) {
        insertUser(user)
        insertSaveSlot(saveSlot)
        insertLife(life)
        insertCharacter(character)
    }
}
