package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "relationships",
    indices = [
        Index(value = ["characterId"]),
        Index(value = ["relatedCharacterId"])
    ]
)
data class RelationshipEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val characterId: String,
    val relatedCharacterId: String,
    val type: String,
    val relationshipStrength: Int,
    val status: String = "Active",
    val startedAt: Int,
    val endedAt: Int? = null,
    val sabotageChance: Double? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
