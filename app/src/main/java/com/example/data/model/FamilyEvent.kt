package com.example.data.model

/**
 * Domain representation of a Family Drama Milestone Event.
 * Stored in Firestore collection: "familyEvents"
 */
data class FamilyEvent(
    val id: String = "",
    val characterId: String = "",
    val relatedCharacterId: String? = null,
    val eventType: String = "Birth", // Marriage, Divorce, Birth, Death
    val gameYear: Int = 2024,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
