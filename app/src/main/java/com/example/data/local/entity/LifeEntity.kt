package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "lives",
    foreignKeys = [
        ForeignKey(
            entity = SaveSlotEntity::class,
            parentColumns = ["id"],
            childColumns = ["saveSlotId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["saveSlotId"]), Index(value = ["isDormant"])]
)
data class LifeEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val saveSlotId: String,
    val name: String,
    val birthYear: Int,
    val currentAge: Int = 0,
    val isDormant: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
