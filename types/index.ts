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

export type TalentType =
  | 'None'
  | 'Acting'
  | 'Crime'
  | 'Dealing'
  | 'Modeling'
  | 'Music'
  | 'Sports';

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
  fertility: number;
  energy: number;
  athleticPerformance: number;
  gender: string;
  sexuality: string;
  talent: TalentType | string;
  eyeStyle: string;
  eyeColor: string;
  skinTone: string;
  browStyle: string;
  facialHairStyle: string;
  facialHairColor: string;
  hairStyle: string;
  hairColor: string;
  geneticHealthModifier?: number | null;
  geneticIntelligenceModifier?: number | null;
  geneticLooksModifier?: number | null;
  birthCity: string;
  birthCountry: string;
  bankBalance: number;
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
// Family System & Relationships (Prompt 03)
// ---------------------------------------------------------

export type RelationshipType =
  | 'Spouse'
  | 'Ex'
  | 'Parent'
  | 'Child'
  | 'Sibling'
  | 'Friend'
  | 'BestFriend'
  | 'Enemy';

export type RelationshipStatus = 'Active' | 'Ended' | 'Deceased';

export type FriendTier =
  | 'Acquaintance'
  | 'Friend'
  | 'Best Friend'
  | 'Close Family';

export interface Relationship {
  id: string;
  characterId: string;
  relatedCharacterId: string;
  type: RelationshipType;
  relationshipStrength: number; // 0-100
  status: RelationshipStatus;
  startedAt: number; // game year
  endedAt?: number | null;
  sabotageChance?: number | null; // For Enemy, derived from Influence stat via FormulaEngine
  relatedCharacter?: ClientCharacter; // Hydrated details
  createdAt: string | Date;
  updatedAt: string | Date;
}

export interface FamilyEvent {
  id: string;
  characterId: string;
  relatedCharacterId?: string | null;
  eventType: 'Marriage' | 'Divorce' | 'Birth' | 'Death' | string;
  gameYear: number;
  description: string;
  createdAt: string | Date;
}

export interface MarriageResult {
  spouseRelationship: Relationship;
  promotedFromDormant: boolean;
  familyEvent: FamilyEvent;
}

export interface DivorceResult {
  exRelationship: Relationship;
  characterBalance: number;
  partnerBalance: number;
  familyEvent: FamilyEvent;
}

export interface ChildBirthResult {
  child: ClientCharacter;
  parentToChildRel: Relationship;
  childToParentRel: Relationship;
  familyEvent: FamilyEvent;
}

export interface InheritanceResult {
  deceasedCharacterId: string;
  totalDistributed: number;
  perChildShare?: number;
  livingChildrenCount: number;
  inheritedBySpouse?: boolean;
  transferredToEstate?: boolean;
  beneficiaries: Array<{ characterId: string; amount: number; role: string }>;
  description: string;
}

/**
 * Derives the UI friend tier label from relationship strength + type.
 * Not separately stored in the database.
 */
export function deriveFriendTier(type: RelationshipType, strength: number): FriendTier {
  if (type === 'Parent' || type === 'Child' || type === 'Sibling' || type === 'Spouse') {
    return 'Close Family';
  }
  if (strength >= 80) return 'Best Friend';
  if (strength >= 40) return 'Friend';
  return 'Acquaintance';
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
  slotName?: string;
  characterName: string;
  birthYear?: number;
  isDormant?: boolean;
  birthCountry?: string;
  birthCity?: string;
  gender?: string;
  sexuality?: string;
  talent?: TalentType | string;
  eyeStyle?: string;
  eyeColor?: string;
  skinTone?: string;
  browStyle?: string;
  facialHairStyle?: string;
  facialHairColor?: string;
  hairStyle?: string;
  hairColor?: string;
  intelligence?: number;
  discipline?: number;
  willpower?: number;
  ambition?: number;
  health?: number;
  looks?: number;
  smarts?: number;
  happiness?: number;
  fertility?: number;
  energy?: number;
  athleticPerformance?: number;
  geneticHealthModifier?: number | null;
  geneticIntelligenceModifier?: number | null;
  geneticLooksModifier?: number | null;
}

export interface AgingResult {
  newAge: number;
  previousStats: {
    health: number;
    fertility: number;
    energy: number;
    athleticPerformance: number;
  };
  newStats: {
    health: number;
    fertility: number;
    energy: number;
    athleticPerformance: number;
  };
  deltas: {
    health: number;
    fertility: number;
    energy: number;
    athleticPerformance: number;
  };
  narrative: string;
}

export interface DeathCheckResult {
  isDead: boolean;
  cause: string | null;
  mortalityProbability: number;
  roll: number;
}
