/**
 * FORMULA ENGINE ARCHITECTURE (Prompt 01 of 20: FOUNDATION)
 *
 * Centralized calculation hub for all Destiny simulation outcomes.
 * Evaluates the five fundamental simulation layers:
 * 1. Character Foundation (Intrinsic biological & intellectual attributes)
 * 2. Momentum (Recent historical trajectory, velocity, streaks)
 * 3. Influence (Social capital, institutional leverage, family status)
 * 4. World Variables (Macroeconomics, political order, historical era)
 * 5. Luck (Stochastic variance bounded by internal karma)
 *
 * All future systems (Family, Education, Career, Business, Politics, Military, etc.)
 * MUST route their event checks through this engine.
 */

import {
  CharacterFoundationInputs,
  MomentumInputs,
  InfluenceInputs,
  WorldVariablesInputs,
  LuckInputs,
  FormulaWeights,
  FormulaOutcome,
  OutcomeTier,
} from '../types';

export const DEFAULT_WEIGHTS: FormulaWeights = {
  wFoundation: 0.35,
  wMomentum: 0.20,
  wInfluence: 0.15,
  wWorld: 0.15,
  wLuck: 0.15,
};

export interface EvaluateFormulaParams {
  characterFoundation: CharacterFoundationInputs;
  momentum?: Partial<MomentumInputs>;
  influence?: Partial<InfluenceInputs>;
  worldVariables?: Partial<WorldVariablesInputs>;
  luck?: Partial<LuckInputs>;
  weights?: Partial<FormulaWeights>;
}

/**
 * Shared evaluation stub.
 * Prompts 02-20 will add domain-specific modifier curves into these placeholders.
 */
export function evaluateFormula({
  characterFoundation,
  momentum = {},
  influence = {},
  worldVariables = {},
  luck = {},
  weights = {},
}: EvaluateFormulaParams): FormulaOutcome {
  const finalWeights: FormulaWeights = {
    ...DEFAULT_WEIGHTS,
    ...weights,
  };

  // 1. Layer 1: Character Foundation (0..100)
  const foundationSum =
    characterFoundation.intelligence +
    characterFoundation.discipline +
    characterFoundation.willpower +
    characterFoundation.ambition +
    characterFoundation.health +
    characterFoundation.looks +
    characterFoundation.smarts +
    characterFoundation.happiness;
  const foundationScore = clamp(foundationSum / 8, 0, 100);

  // 2. Layer 2: Momentum (Trajectory normalized to 0..100)
  // TODO [Prompts 04-06]: Integrate Career streak and Academic momentum curves
  const trajectory = momentum.trajectoryScore ?? 0.0;
  const fatigue = momentum.fatigueFactor ?? 0.0;
  const momentumScore = clamp((trajectory * 50) + 50 - (fatigue * 20), 0, 100);

  // 3. Layer 3: Influence (Social & faction capital 0..100)
  // TODO [Prompts 03, 08]: Integrate Family prestige and Political faction leverage
  const socialCap = influence.socialCapital ?? 0;
  const familyLev = influence.familyLeverage ?? 0;
  const faction = influence.factionStanding ?? 0;
  const influenceScore = clamp((socialCap * 0.5) + (familyLev * 0.3) + ((faction + 100) * 0.1), 0, 100);

  // 4. Layer 4: World Variables (Macro backdrop 0..100)
  // TODO [Prompt 14]: Integrate World Simulation economy index and geopolitical war/peace state
  const econCycle = worldVariables.economicCycleIndex ?? 1.0;
  const stability = worldVariables.stabilityIndex ?? 1.0;
  const opportunity = worldVariables.opportunityRating ?? 1.0;
  const worldScore = clamp((econCycle * 40) + (stability * 40) + (opportunity * 20), 0, 100);

  // 5. Layer 5: Luck (Stochastic roll with internal karma offset)
  // ARCHITECTURAL MANDATE: Raw karma is injected internally here, never exposed to client.
  const rawRoll = luck.rawRoll ?? Math.random();
  const karmaMod = luck.karmaModifier ?? 0;
  const luckScore = clamp((rawRoll * 100) + karmaMod, 0, 100);

  // Weighted Composite
  const compositeScore = clamp(
    foundationScore * finalWeights.wFoundation +
    momentumScore * finalWeights.wMomentum +
    influenceScore * finalWeights.wInfluence +
    worldScore * finalWeights.wWorld +
    luckScore * finalWeights.wLuck,
    0,
    100
  );

  let tier: OutcomeTier;
  if (compositeScore >= 85) tier = 'CRITICAL_SUCCESS';
  else if (compositeScore >= 65) tier = 'SUCCESS';
  else if (compositeScore >= 45) tier = 'MARGINAL';
  else if (compositeScore >= 25) tier = 'FAILURE';
  else tier = 'CRITICAL_FAILURE';

  return {
    compositeScore: Number(compositeScore.toFixed(2)),
    tier,
    contributions: {
      foundation: Number((foundationScore * finalWeights.wFoundation).toFixed(2)),
      momentum: Number((momentumScore * finalWeights.wMomentum).toFixed(2)),
      influence: Number((influenceScore * finalWeights.wInfluence).toFixed(2)),
      world: Number((worldScore * finalWeights.wWorld).toFixed(2)),
      luck: Number((luckScore * finalWeights.wLuck).toFixed(2)),
    },
    isCritical: tier === 'CRITICAL_SUCCESS' || tier === 'CRITICAL_FAILURE',
  };
}

/**
 * Derives Enemy sabotage chance from the enemy's Influence stat via FormulaEngine.
 * Returns a percentage probability (5% to 75%).
 */
export function calculateEnemySabotageChance(enemyInfluenceInputs: Partial<InfluenceInputs>): number {
  const socialCap = enemyInfluenceInputs.socialCapital ?? 50;
  const familyLev = enemyInfluenceInputs.familyLeverage ?? 50;
  const faction = enemyInfluenceInputs.factionStanding ?? 0;
  const influenceScore = clamp((socialCap * 0.5) + (familyLev * 0.3) + ((faction + 100) * 0.1), 0, 100);

  // Sabotage chance scales directly with enemy influence
  const sabotageChance = clamp(5.0 + (influenceScore * 0.7), 5.0, 75.0);
  return Number(sabotageChance.toFixed(2));
}

/**
 * Calculates a child's genetic modifier using a real weighted blend of both parents' stats
 * plus a random variance component pulled from the FormulaEngine's Luck layer.
 */
export function calculateGeneticModifier(
  parent1Stat: number,
  parent2Stat: number,
  luckInputs: Partial<LuckInputs> = {}
): number {
  const parentAvg = (parent1Stat + parent2Stat) / 2;
  const rawRoll = luckInputs.rawRoll ?? Math.random();
  const karmaMod = luckInputs.karmaModifier ?? 0;
  
  // Luck layer variance: -10 to +10 range
  const luckVariance = ((rawRoll - 0.5) * 20) + (karmaMod * 0.1);
  const modifier = ((parentAvg - 50) * 0.4) + luckVariance;
  return Number(clamp(modifier, -25, 25).toFixed(2));
}

function clamp(value: number, min: number, max: number): number {
  return Math.max(min, Math.min(max, value));
}
