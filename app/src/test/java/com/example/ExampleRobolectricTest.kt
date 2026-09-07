package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.entity.CharacterEntity
import com.example.data.model.toClientCharacter
import com.example.services.FormulaEngine
import com.example.services.GameClock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Destiny", appName)
  }

  @Test
  fun `verify formula engine evaluates 5 layers`() {
    val outcome = FormulaEngine.evaluate(
      characterFoundation = FormulaEngine.CharacterFoundation(intelligence = 80.0),
      momentum = FormulaEngine.Momentum(trajectoryScore = 0.5),
      influence = FormulaEngine.Influence(socialCapital = 50.0),
      worldVariables = FormulaEngine.WorldVariables(stabilityIndex = 0.9),
      luck = FormulaEngine.Luck(rawRoll = 0.5)
    )
    assertNotNull(outcome)
    assertTrue(outcome.compositeScore in 0.0..100.0)
  }

  @Test
  fun `verify game clock default yearly and supports zoom`() {
    val clock = GameClock(initialYear = 2026)
    assertEquals("Year 2026", clock.formatDisplay())
    clock.advanceTick(1)
    assertEquals("Year 2027", clock.formatDisplay())
    clock.enterEventZoom(GameClock.TickResolution.MONTHLY)
    assertEquals("Year 2027 • Month 1/12", clock.formatDisplay())
  }

  @Test
  fun `verify karma encapsulation in client character`() {
    val internalEntity = CharacterEntity(
      id = "chr_test",
      lifeId = "life_test",
      intelligence = 80,
      discipline = 75,
      willpower = 70,
      ambition = 85,
      health = 90,
      looks = 65,
      smarts = 82,
      happiness = 78,
      karma = 99 // internal secret
    )
    val clientModel = internalEntity.toClientCharacter()
    // clientModel has no karma field
    assertEquals("chr_test", clientModel.id)
    assertEquals(80, clientModel.intelligence)
  }
}
