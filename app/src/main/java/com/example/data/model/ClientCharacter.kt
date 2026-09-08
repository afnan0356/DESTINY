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
    val happiness: Int,
    val fertility: Int,
    val energy: Int,
    val athleticPerformance: Int,
    val gender: String,
    val sexuality: String,
    val talent: String,
    val eyeStyle: String,
    val eyeColor: String,
    val skinTone: String,
    val browStyle: String,
    val facialHairStyle: String,
    val facialHairColor: String,
    val hairStyle: String,
    val hairColor: String,
    val geneticHealthModifier: Double? = null,
    val geneticIntelligenceModifier: Double? = null,
    val geneticLooksModifier: Double? = null,
    val birthCity: String,
    val birthCountry: String
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
    happiness = happiness,
    fertility = fertility,
    energy = energy,
    athleticPerformance = athleticPerformance,
    gender = gender,
    sexuality = sexuality,
    talent = talent,
    eyeStyle = eyeStyle,
    eyeColor = eyeColor,
    skinTone = skinTone,
    browStyle = browStyle,
    facialHairStyle = facialHairStyle,
    facialHairColor = facialHairColor,
    hairStyle = hairStyle,
    hairColor = hairColor,
    geneticHealthModifier = geneticHealthModifier,
    geneticIntelligenceModifier = geneticIntelligenceModifier,
    geneticLooksModifier = geneticLooksModifier,
    birthCity = birthCity,
    birthCountry = birthCountry
    // ARCHITECTURAL MANDATE: karma is intentionally and strictly NOT mapped here!
)
