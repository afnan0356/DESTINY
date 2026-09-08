package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "family_events",
    indices = [
        Index(value = ["characterId"]),
        Index(value = ["gameYear"])
    ]
)
data class FamilyEventEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val characterId: String,
    val relatedCharacterId: String? = null,
    val eventType: String,
    val gameYear: Int,
    val description: String,
    val createdAt: Long = System.currentTimeMillis()
)
