/**
 * AGING SERVICE (Prompt 02 of 20: CHARACTER SYSTEM)
 *
 * Implements biological aging and progressive degradation for:
 * - Health (gradual degradation, scaling after middle age)
 * - Fertility (gradual in 20s/30s, steep decline in late 30s/40s)
 * - Energy (gradual systemic decline)
 * - Athletic Performance (mild until 30, SHARP drop past age 30)
 *
 * ARCHITECTURAL MANDATE:
 * Must call the central FormulaEngine rather than calculating degradation inline.
 * The FormulaEngine's composite evaluation (incorporating willpower, discipline,
 * foundation health, and world variables) provides stochastic biological resilience.
 */

import { evaluateFormula } from './formulaEngine';
import { AgingResult, ClientCharacter } from '../types';

export interface AgingOptions {
  worldStability?: number;
  economicCycle?: number;
  luckRoll?: number;
}

export class AgingService {
  /**
   * Evaluates one year of aging on a character.
   * Returns new stats, stat deltas, and narrative logging.
   */
  public static ageOneYear(
    character: Pick<
      ClientCharacter,
      | 'intelligence'
      | 'discipline'
      | 'willpower'
      | 'ambition'
      | 'health'
      | 'looks'
      | 'smarts'
      | 'happiness'
      | 'fertility'
      | 'energy'
      | 'athleticPerformance'
    >,
    currentAge: number,
    options: AgingOptions = {}
  ): AgingResult {
    const nextAge = currentAge + 1;

    // 1. Evaluate biological resilience via central FormulaEngine
    const formulaOutcome = evaluateFormula({
      characterFoundation: {
        intelligence: character.intelligence,
        discipline: character.discipline,
        willpower: character.willpower,
        ambition: character.ambition,
        health: character.health,
        looks: character.looks,
        smarts: character.smarts,
        happiness: character.happiness,
      },
      momentum: {
        trajectoryScore: character.health > 70 ? 0.2 : -0.2,
        fatigueFactor: Math.max(0, (100 - character.energy) / 100),
      },
      worldVariables: {
        stabilityIndex: options.worldStability ?? 1.0,
        economicCycleIndex: options.economicCycle ?? 1.0,
      },
      luck: {
        rawRoll: options.luckRoll,
      },
    });

    // High composite score mitigates degradation (resilience factor: 0.65 to 1.35)
    // Characters with high discipline, willpower, and foundation health preserve their stats better
    const resilienceScore = formulaOutcome.compositeScore;
    const resilienceFactor = Math.max(0.65, Math.min(1.35, 1.45 - (resilienceScore / 100)));

    // 2. Compute Health degradation
    let baseHealthLoss = 0;
    if (nextAge > 80) {
      baseHealthLoss = 4.5;
    } else if (nextAge > 65) {
      baseHealthLoss = 3.0;
    } else if (nextAge > 45) {
      baseHealthLoss = 1.6;
    } else if (nextAge > 25) {
      baseHealthLoss = 0.7;
    } else {
      baseHealthLoss = 0.1;
    }
    const healthLoss = Math.round(baseHealthLoss * resilienceFactor);
    const newHealth = Math.max(0, Math.min(100, character.health - healthLoss));

    // 3. Compute Fertility degradation
    let baseFertilityLoss = 0;
    if (nextAge > 45) {
      baseFertilityLoss = 8.0;
    } else if (nextAge > 36) {
      baseFertilityLoss = 4.0;
    } else if (nextAge > 28) {
      baseFertilityLoss = 1.5;
    } else {
      baseFertilityLoss = 0.2;
    }
    const fertilityLoss = Math.round(baseFertilityLoss * resilienceFactor);
    const newFertility = Math.max(0, Math.min(100, character.fertility - fertilityLoss));

    // 4. Compute Energy degradation
    let baseEnergyLoss = 0;
    if (nextAge > 70) {
      baseEnergyLoss = 3.5;
    } else if (nextAge > 50) {
      baseEnergyLoss = 2.0;
    } else if (nextAge > 30) {
      baseEnergyLoss = 1.0;
    } else {
      baseEnergyLoss = 0.4;
    }
    const energyLoss = Math.round(baseEnergyLoss * resilienceFactor);
    const newEnergy = Math.max(0, Math.min(100, character.energy - energyLoss));

    // 5. Compute Athletic Performance degradation (SHARP DROP past age 30)
    let baseAthleticLoss = 0;
    if (nextAge > 55) {
      baseAthleticLoss = 8.0;
    } else if (nextAge > 40) {
      baseAthleticLoss = 6.0;
    } else if (nextAge > 30) {
      // Prompt 02 Mandate: Sharp drop past age 30
      baseAthleticLoss = 4.5;
    } else if (nextAge > 25) {
      baseAthleticLoss = 1.2;
    } else {
      baseAthleticLoss = 0.2;
    }
    const athleticLoss = Math.round(baseAthleticLoss * resilienceFactor);
    const newAthletic = Math.max(0, Math.min(100, character.athleticPerformance - athleticLoss));

    // Narrative generation
    let narrative = `Turned age ${nextAge}.`;
    if (nextAge > 30 && athleticLoss >= 4) {
      narrative += ` Physical agility and athletic endurance took a noticeable post-30 dip (-${athleticLoss} ATH).`;
    }
    if (healthLoss > 2) {
      narrative += ` Aging toll reduced vitality (-${healthLoss} HLT).`;
    }
    if (resilienceScore >= 75) {
      narrative += ` High intrinsic discipline and resilience cushioned biological decline.`;
    }

    return {
      newAge: nextAge,
      previousStats: {
        health: character.health,
        fertility: character.fertility,
        energy: character.energy,
        athleticPerformance: character.athleticPerformance,
      },
      newStats: {
        health: newHealth,
        fertility: newFertility,
        energy: newEnergy,
        athleticPerformance: newAthletic,
      },
      deltas: {
        health: newHealth - character.health,
        fertility: newFertility - character.fertility,
        energy: newEnergy - character.energy,
        athleticPerformance: newAthletic - character.athleticPerformance,
      },
      narrative,
    };
  }
}
