package com.example.data.model

import com.example.data.local.entity.CharacterEntity

/**
 * Client-facing representation of a Character.
 *
 * ARCHITECTURAL MANDATE:
 * Karma is stored in the database layer for world calculations, but is
 * STRICTLY EXCLUDED from any client response or UI representation.
 */
data class ClientCharacter(
    val id: String,
    val lifeId: String,
    val intelligence: Int,
    val discipline: Int,
    val willpower: Int,
    val ambition: Int,
    val health: Int,
    val looks: Int,
    val smarts: Int,
    val happiness: Int
)

fun CharacterEntity.toClientCharacter(): ClientCharacter = ClientCharacter(
    id = id,
    lifeId = lifeId,
    intelligence = intelligence,
    discipline = discipline,
    willpower = willpower,
    ambition = ambition,
    health = health,
    looks = looks,
    smarts = smarts,
    happiness = happiness
    // Note: karma is intentionally NOT mapped here.
)
