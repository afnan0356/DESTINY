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
    val fertility: Int = 80,
    val energy: Int = 100,
    val athleticPerformance: Int = 50,
    val gender: String = "Male",
    val sexuality: String = "Heterosexual",
    val talent: String = "None",
    val eyeStyle: String = "Almond",
    val eyeColor: String = "Brown",
    val skinTone: String = "Fair",
    val browStyle: String = "Straight",
    val facialHairStyle: String = "Clean Shaven",
    val facialHairColor: String = "Black",
    val hairStyle: String = "Short Crop",
    val hairColor: String = "Black",
    val geneticHealthModifier: Double? = null,
    val geneticIntelligenceModifier: Double? = null,
    val geneticLooksModifier: Double? = null,
    val birthCity: String = "New York",
    val birthCountry: String = "United States",
    val bankBalance: Long = 1000L,
    // Hidden core stat: Persisted in database, but NEVER exposed to client / API models
    val karma: Int = 50
)
