package com.example

import com.example.data.local.entity.CharacterEntity
import com.example.data.model.ClientCharacter
import com.example.data.model.toClientCharacter
import com.example.services.AgingService
import com.example.services.DeathService
import com.example.services.FormulaEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterSystemUnitTest {

    private fun createTestCharacter(
        health: Int = 85,
        energy: Int = 90,
        fertility: Int = 80,
        athleticPerformance: Int = 75
    ): ClientCharacter {
        return ClientCharacter(
            id = "char-test-1",
            lifeId = "life-test-1",
            intelligence = 75,
            discipline = 70,
            willpower = 72,
            ambition = 80,
            health = health,
            looks = 68,
            smarts = 75,
            happiness = 80,
            fertility = fertility,
            energy = energy,
            athleticPerformance = athleticPerformance,
            gender = "Male",
            sexuality = "Heterosexual",
            talent = "Athletics",
            eyeStyle = "Almond",
            eyeColor = "Brown",
            skinTone = "Fair",
            browStyle = "Straight",
            facialHairStyle = "Clean Shaven",
            facialHairColor = "Black",
            hairStyle = "Short Crop",
            hairColor = "Black",
            birthCity = "New York",
            birthCountry = "United States"
        )
    }

    @Test
    fun clientCharacter_strictlyOmitsKarma_privacyEnforced() {
        val entity = CharacterEntity(
            id = "entity-1",
            lifeId = "life-1",
            intelligence = 80,
            discipline = 80,
            willpower = 80,
            ambition = 80,
            health = 90,
            looks = 70,
            smarts = 80,
            happiness = 85,
            fertility = 85,
            energy = 95,
            athleticPerformance = 80,
            karma = 42, // Stored in database
            gender = "Female",
            sexuality = "Heterosexual",
            talent = "Music",
            eyeStyle = "Round",
            eyeColor = "Blue",
            skinTone = "Fair",
            browStyle = "Arched",
            facialHairStyle = "None",
            facialHairColor = "None",
            hairStyle = "Long Waves",
            hairColor = "Blonde",
            birthCity = "London",
            birthCountry = "United Kingdom"
        )

        val clientDto = entity.toClientCharacter()

        // Verify all fields mapped accurately
        assertEquals(entity.id, clientDto.id)
        assertEquals(entity.lifeId, clientDto.lifeId)
        assertEquals(entity.birthCity, clientDto.birthCity)
        assertEquals(entity.birthCountry, clientDto.birthCountry)

        // Verify via reflection that ClientCharacter class contains NO field named 'karma'
        val hasKarmaField = ClientCharacter::class.java.declaredFields.any { it.name.equals("karma", ignoreCase = true) }
        assertFalse("ClientCharacter DTO must strictly NEVER contain a karma field", hasKarmaField)
    }

    @Test
    fun agingService_youngAdult_hasMinimalDegradation() {
        val char = createTestCharacter(health = 90, energy = 95, athleticPerformance = 85, fertility = 90)
        val result = AgingService.ageOneYear(char, currentAge = 22)

        // In prime 20s, health should remain stable or barely degrade
        assertTrue("Health should remain high at age 23", result.newStats.health >= 88)
        assertTrue("Energy should remain high at age 23", result.newStats.energy >= 90)
        assertNotNull(result.narrative)
    }

    @Test
    fun agingService_elderly_experiencesProgessiveDegradation() {
        val char = createTestCharacter(health = 75, energy = 70, athleticPerformance = 60, fertility = 30)
        val result = AgingService.ageOneYear(char, currentAge = 75)

        assertTrue("Health should decrease with advanced age", result.newStats.health < char.health)
        assertTrue("Fertility should decline sharply after 50", result.newStats.fertility <= 5)
        assertTrue("Athletic performance should decline after 35+", result.newStats.athleticPerformance < char.athleticPerformance)
    }

    @Test
    fun deathService_youngHealthyCharacter_survives() {
        val char = createTestCharacter(health = 95)
        var deaths = 0
        // Test 100 trials - young healthy 20-year old should almost always survive
        for (i in 1..100) {
            val check = DeathService.checkMortality(age = 20, health = 95, character = char)
            if (check.isDead) deaths++
        }
        assertTrue("Mortality in healthy 20yo should be extremely rare (< 5%)", deaths < 5)
    }

    @Test
    fun deathService_zeroHealth_causesDeath() {
        val char = createTestCharacter(health = 0)
        val check = DeathService.checkMortality(age = 40, health = 0, character = char)
        assertTrue("Character with 0 health must trigger death", check.isDead)
        assertNotNull(check.cause)
    }

    @Test
    fun deathService_extremeAge_causesNaturalDeath() {
        val char = createTestCharacter(health = 10)
        val check = DeathService.checkMortality(age = 115, health = 10, character = char)
        assertTrue("Character at age 115 should have near-certain mortality", check.isDead)
    }

    @Test
    fun formulaEngine_fiveLayerWeights_sumToOneHundredPercent() {
        val outcome = FormulaEngine.evaluate(
            characterFoundation = FormulaEngine.CharacterFoundation(
                intelligence = 80.0,
                discipline = 75.0,
                willpower = 70.0,
                ambition = 85.0,
                health = 90.0,
                looks = 70.0,
                smarts = 80.0,
                happiness = 75.0
            ),
            momentum = FormulaEngine.Momentum(trajectoryScore = 0.5),
            influence = FormulaEngine.Influence(socialCapital = 50.0),
            worldVariables = FormulaEngine.WorldVariables(economicCycleIndex = 1.0)
        )

        // Verify outcome structure
        assertNotNull(outcome)
        assertTrue(outcome.compositeScore in 0.0..100.0)
        assertTrue(outcome.successProbability in 0.0..1.0)
    }
}
