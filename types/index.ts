/**
 * DESTINY SIMULATION ENGINE — TYPE DEFINITIONS
 * Master Build Prompt 01 of 20: FOUNDATION
 */

export interface User {
  id: string;
  username: string;
  email?: string | null;
  createdAt: Date;
  updatedAt: Date;
}

export interface SaveSlot {
  id: string;
  userId: string;
  slotName: string;
  createdAt: Date;
  updatedAt: Date;
}

export interface Life {
  id: string;
  saveSlotId: string;
  name: string;
  birthYear: number;
  currentAge: number;
  isDormant: boolean;
  createdAt: Date;
  updatedAt: Date;
}

export interface Character {
  id: string;
  lifeId: string;
  intelligence: number;
  discipline: number;
  willpower: number;
  ambition: number;
  health: number;
  looks: number;
  smarts: number;
  happiness: number;
  karma: number; // Internal database only
  createdAt: Date;
  updatedAt: Date;
}

/**
 * CLIENT CHARACTER CONTRACT
 *
 * ARCHITECTURAL RULE:
 * Karma is stored in the database layer, but NEVER exposed to the client.
 * Use ClientCharacter for all API responses and client-facing components.
 */
export type ClientCharacter = Omit<Character, 'karma'>;

export interface LifeSummaryResponse {
  user: User;
  saveSlot: SaveSlot;
  life: Life;
  character: ClientCharacter; // Enforces karma exclusion
}

// ---------------------------------------------------------
// Formula Engine Types
// ---------------------------------------------------------

export interface CharacterFoundationInputs {
  intelligence: number;
  discipline: number;
  willpower: number;
  ambition: number;
  health: number;
  looks: number;
  smarts: number;
  happiness: number;
}

export interface MomentumInputs {
  trajectoryScore: number; // Normalized -1.0 to 1.0
  recentStreak: number;
  fatigueFactor: number;   // 0.0 (fresh) to 1.0 (exhausted)
}

export interface InfluenceInputs {
  socialCapital: number;   // 0 to 100
  familyLeverage: number;  // 0 to 100
  factionStanding: number; // -100 to 100
}

export interface WorldVariablesInputs {
  economicCycleIndex: number; // 0.5 (depression) to 1.5 (boom)
  stabilityIndex: number;     // 0.0 (chaos/war) to 1.0 (peace)
  opportunityRating: number;
}

export interface LuckInputs {
  rawRoll?: number; // 0.0 to 1.0
  karmaModifier?: number;
}

export interface FormulaWeights {
  wFoundation: number;
  wMomentum: number;
  wInfluence: number;
  wWorld: number;
  wLuck: number;
}

export type OutcomeTier =
  | 'CRITICAL_FAILURE'
  | 'FAILURE'
  | 'MARGINAL'
  | 'SUCCESS'
  | 'CRITICAL_SUCCESS';

export interface FormulaOutcome {
  compositeScore: number;
  tier: OutcomeTier;
  contributions: {
    foundation: number;
    momentum: number;
    influence: number;
    world: number;
    luck: number;
  };
  isCritical: boolean;
}

// ---------------------------------------------------------
// Game Clock Types
// ---------------------------------------------------------

export type TickResolution = 'YEARLY' | 'MONTHLY' | 'WEEKLY' | 'DAILY';

export interface ClockState {
  year: number;
  subTick: number;
  resolution: TickResolution;
  isZoomedIn: boolean;
  totalTicksElapsed: number;
}

export interface CreateLifeInput {
  username: string;
  slotName: string;
  characterName: string;
  birthYear?: number;
  isDormant?: boolean;
}
