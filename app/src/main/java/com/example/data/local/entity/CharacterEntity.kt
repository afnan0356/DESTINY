package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "characters",
    foreignKeys = [
        ForeignKey(
            entity = LifeEntity::class,
            parentColumns = ["id"],
            childColumns = ["lifeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["lifeId"], unique = true)]
)
data class CharacterEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val lifeId: String,
    val intelligence: Int,
    val discipline: Int,
    val willpower: Int,
    val ambition: Int,
    val health: Int,
    val looks: Int,
    val smarts: Int,
    val happiness: Int,
    // Hidden core stat: Persisted in database, but NEVER exposed to client / API models
    val karma: Int = 50
)
