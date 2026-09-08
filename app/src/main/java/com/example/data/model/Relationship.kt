package com.example.data.model

/**
 * Domain representation of a Character Relationship.
 * Stored in Firestore collection: "relationships"
 */
data class Relationship(
    val id: String = "",
    val characterId: String = "",
    val relatedCharacterId: String = "",
    val type: String = "Friend", // Spouse, Ex, Parent, Child, Sibling, Friend, BestFriend, Enemy
    val relationshipStrength: Int = 50, // 0-100
    val status: String = "Active", // Active, Ended, Deceased
    val startedAt: Int = 2024,
    val endedAt: Int? = null,
    val sabotageChance: Double? = null, // Derived from enemy's Influence via FormulaEngine
    val relatedCharacter: ClientCharacter? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Derives UI friend tier label from relationship strength + type.
 * Not stored as a separate database field.
 */
fun deriveFriendTier(type: String, strength: Int): String {
    return when {
        type in listOf("Parent", "Child", "Sibling", "Spouse") -> "Close Family"
        strength >= 80 -> "Best Friend"
        strength >= 40 -> "Friend"
        else -> "Acquaintance"
    }
}
