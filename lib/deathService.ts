/**
 * DEATH CHECK SERVICE (Prompt 02 of 20: CHARACTER SYSTEM)
 *
 * Evaluates annual mortality hazard based on:
 * - Age
 * - Health
 * - Lifestyle modifier (default neutral)
 * - Luck via central FormulaEngine
 *
 * ARCHITECTURAL MANDATE:
 * Returns isDead: Boolean and cause: String?
 * Extreme input calibration:
 * - Age 100 + Health 5 -> high death likelihood (~95%+)
 * - Age 20 + Health 95 -> near zero death likelihood (<0.05%)
 */

import { evaluateFormula } from './formulaEngine';
import { DeathCheckResult, InheritanceResult } from '../types';

export interface DeathCheckOptions {
  lifestyleModifier?: number; // -0.2 (very healthy lifestyle) to +0.2 (reckless/hazardous)
  forcedLuckRoll?: number;     // 0.0 to 1.0 for deterministic testing
  stabilityIndex?: number;
}

export class DeathService {
  /**
   * Evaluates mortality probability for a character at their given age and health.
   */
  public static calculateMortalityProbability(
    age: number,
    health: number,
    lifestyleModifier = 0.0
  ): number {
    // 1. Base Age Risk
    let baseAgeRisk = 0.0005;
    if (age > 95) {
      baseAgeRisk = 0.55 + (age - 95) * 0.08;
    } else if (age > 85) {
      baseAgeRisk = 0.22 + (age - 85) * 0.033;
    } else if (age > 75) {
      baseAgeRisk = 0.07 + (age - 75) * 0.015;
    } else if (age > 60) {
      baseAgeRisk = 0.018 + (age - 60) * 0.0035;
    } else if (age > 40) {
      baseAgeRisk = 0.002 + (age - 40) * 0.0008;
    } else {
      baseAgeRisk = 0.0004 + (age * 0.00004);
    }

    // 2. Health Multiplier
    let healthMultiplier: number;
    if (health <= 5) {
      healthMultiplier = 9.0;
    } else if (health <= 15) {
      healthMultiplier = 5.0;
    } else if (health <= 30) {
      healthMultiplier = 3.0;
    } else if (health <= 50) {
      healthMultiplier = 1.8;
    } else if (health <= 70) {
      healthMultiplier = 1.0;
    } else if (health <= 90) {
      healthMultiplier = 0.55;
    } else {
      // High health (91-100) confers maximum biological longevity
      healthMultiplier = 0.25;
    }

    // 3. Combine with lifestyle modifier
    const rawProb = (baseAgeRisk * healthMultiplier) * (1.0 + lifestyleModifier);
    return Math.max(0.0001, Math.min(0.99, rawProb));
  }

  /**
   * Evaluates annual death check incorporating FormulaEngine luck and foundation.
   */
  public static checkMortality(
    age: number,
    health: number,
    characterFoundationStats = {
      intelligence: 50,
      discipline: 50,
      willpower: 50,
      ambition: 50,
      looks: 50,
      smarts: 50,
      happiness: 50,
    },
    options: DeathCheckOptions = {}
  ): DeathCheckResult {
    const baseProbability = this.calculateMortalityProbability(
      age,
      health,
      options.lifestyleModifier ?? 0.0
    );

    // Call FormulaEngine to integrate luck and resilience
    const formulaOutcome = evaluateFormula({
      characterFoundation: {
        ...characterFoundationStats,
        health,
      },
      worldVariables: {
        stabilityIndex: options.stabilityIndex ?? 1.0,
      },
      luck: {
        rawRoll: options.forcedLuckRoll,
      },
    });

    // Formula outcome luck adjustment
    let adjustedProbability = baseProbability;
    if (formulaOutcome.tier === 'CRITICAL_SUCCESS') {
      adjustedProbability *= 0.5; // Stroke of good fortune / narrow escape
    } else if (formulaOutcome.tier === 'CRITICAL_FAILURE') {
      adjustedProbability *= 1.4; // Stroke of misfortune
    }
    adjustedProbability = Math.max(0.0001, Math.min(0.999, adjustedProbability));

    const roll = options.forcedLuckRoll ?? Math.random();
    const isDead = roll < adjustedProbability;

    let cause: string | null = null;
    if (isDead) {
      if (health <= 10) {
        cause = 'Critical Organ Failure & Systemic Exhaustion';
      } else if (age >= 85) {
        cause = 'Natural Complications of Advanced Age';
      } else if (age >= 60) {
        cause = 'Acute Cardiovascular Episode';
      } else if (age < 35 && formulaOutcome.tier === 'CRITICAL_FAILURE') {
        cause = 'Unforeseen Fatal Accident';
      } else {
        cause = 'Sudden Medical Complications';
      }
    }

    return {
      isDead,
      cause,
      mortalityProbability: Number(adjustedProbability.toFixed(5)),
      roll: Number(roll.toFixed(5)),
    };
  }

  /**
   * INHERITANCE HOOK (Prompt 03)
   * Resolves asset distribution upon death:
   * - Splits bankBalance equally among living Child relationships.
   * - If no living children, inherits to living spouse if one exists.
   * - Otherwise logs to placeholder "Estate".
   */
  public static resolveInheritance(
    deceasedCharacterId: string,
    bankBalance: number,
    relationships: Array<{ characterId: string; relatedCharacterId: string; type: string; status: string }>
  ): InheritanceResult {
    const livingChildren = relationships.filter(
      (r) => r.type === 'Child' && r.status === 'Active'
    );
    const livingSpouse = relationships.find(
      (r) => r.type === 'Spouse' && r.status === 'Active'
    );

    if (livingChildren.length > 0) {
      const perChildShare = Math.floor(bankBalance / livingChildren.length);
      const beneficiaries = livingChildren.map((child) => ({
        characterId: child.relatedCharacterId,
        amount: perChildShare,
        role: 'Child',
      }));
      return {
        deceasedCharacterId,
        totalDistributed: perChildShare * livingChildren.length,
        perChildShare,
        livingChildrenCount: livingChildren.length,
        inheritedBySpouse: false,
        transferredToEstate: false,
        beneficiaries,
        description: `Estate of $${bankBalance.toLocaleString()} divided equally among ${livingChildren.length} living children ($${perChildShare.toLocaleString()} each).`,
      };
    }

    if (livingSpouse) {
      return {
        deceasedCharacterId,
        totalDistributed: bankBalance,
        livingChildrenCount: 0,
        inheritedBySpouse: true,
        transferredToEstate: false,
        beneficiaries: [
          {
            characterId: livingSpouse.relatedCharacterId,
            amount: bankBalance,
            role: 'Spouse',
          },
        ],
        description: `Estate of $${bankBalance.toLocaleString()} inherited entirely by surviving spouse.`,
      };
    }

    // No living children or spouse -> transfer to Estate
    return {
      deceasedCharacterId,
      totalDistributed: 0,
      livingChildrenCount: 0,
      inheritedBySpouse: false,
      transferredToEstate: true,
      beneficiaries: [],
      description: `[Estate Placeholder] No living heirs. $${bankBalance.toLocaleString()} transferred to Estate holding.`,
    };
  }
}
