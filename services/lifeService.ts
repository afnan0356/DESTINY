/**
 * LIFE SERVICE & FIRESTORE PERSISTENCE ORCHESTRATOR
 * Master Build Prompts 01-03: FOUNDATION, CHARACTER SYSTEM, FAMILY SYSTEM
 *
 * Core service managing:
 * 1. Life creation pipeline (User -> SaveSlot -> Life -> Character) in Firestore
 * 2. Unlimited save slots per user
 * 3. NPC Simulation Model:
 *    - 'dormant': Lightweight. Stats stored in DB, but not actively ticked year-to-year.
 *    - 'active': Full simulation active because player is currently interacting.
 * 4. Karma Privacy Mandate: Strips karma from all client-facing responses.
 */

import {
  doc,
  getDoc,
  setDoc,
  updateDoc,
  collection,
  query,
  where,
  getDocs,
  serverTimestamp,
} from 'firebase/firestore';
import { db } from '../lib/firebase';
import {
  Character,
  ClientCharacter,
  CreateLifeInput,
  Life,
  LifeSummaryResponse,
  SaveSlot,
  User,
} from '../types';

export class LifeService {
  /**
   * Sanitizes a Character entity into a ClientCharacter,
   * strictly omitting the internal karma stat.
   */
  public static toClientCharacter(character: Character): ClientCharacter {
    const { karma, ...clientSafe } = character;
    return clientSafe;
  }

  /**
   * Atomic creation flow in Firestore:
   * User → SaveSlot → Life → Character
   */
  public static async createNewLife(input: CreateLifeInput): Promise<LifeSummaryResponse> {
    const {
      username,
      slotName,
      characterName,
      birthYear = 2000,
      isDormant = false,
      birthCountry = 'United States',
      birthCity = 'New York',
      gender = 'Male',
      sexuality = 'Heterosexual',
      talent = 'None',
      eyeStyle = 'Almond',
      eyeColor = 'Brown',
      skinTone = 'Fair',
      browStyle = 'Straight',
      facialHairStyle = 'Clean Shaven',
      facialHairColor = 'Black',
      hairStyle = 'Short Crop',
      hairColor = 'Black',
      intelligence = randomStat(45, 95),
      discipline = randomStat(40, 90),
      willpower = randomStat(40, 90),
      ambition = randomStat(45, 95),
      health = randomStat(60, 99),
      looks = randomStat(40, 90),
      smarts = randomStat(45, 95),
      happiness = randomStat(50, 90),
      fertility = randomStat(70, 95),
      energy = randomStat(80, 100),
      athleticPerformance = randomStat(40, 85),
      geneticHealthModifier = null,
      geneticIntelligenceModifier = null,
      geneticLooksModifier = null,
    } = input;

    const userId = `user_${username.toLowerCase().replace(/[^a-z0-9]/g, '_')}`;
    const userRef = doc(db, 'users', userId);

    // 1. Ensure User entity
    const userSnap = await getDoc(userRef);
    let userData: User;
    const now = new Date();

    if (userSnap.exists()) {
      userData = userSnap.data() as User;
    } else {
      userData = {
        id: userId,
        username,
        email: `${username.toLowerCase()}@destiny.game`,
        createdAt: now,
        updatedAt: now,
      };
      await setDoc(userRef, {
        ...userData,
        createdAt: serverTimestamp(),
        updatedAt: serverTimestamp(),
      });
    }

    // 2. SaveSlot Entity
    const saveSlotId = `slot_${Date.now()}_${Math.random().toString(36).substring(2, 7)}`;
    const saveSlotRef = doc(db, 'saveSlots', saveSlotId);
    const saveSlotData: SaveSlot = {
      id: saveSlotId,
      userId: userData.id,
      slotName: slotName || `Save Slot ${Date.now()}`,
      createdAt: now,
      updatedAt: now,
    };
    await setDoc(saveSlotRef, {
      ...saveSlotData,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    });

    // 3. Life Entity
    const lifeId = `life_${Date.now()}_${Math.random().toString(36).substring(2, 7)}`;
    const lifeRef = doc(db, 'lives', lifeId);
    const lifeData: Life = {
      id: lifeId,
      saveSlotId: saveSlotData.id,
      name: characterName,
      birthYear,
      currentAge: 0,
      isDormant,
      createdAt: now,
      updatedAt: now,
    };
    await setDoc(lifeRef, {
      ...lifeData,
      userId: userData.id,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    });

    // 4. Character Entity
    const characterId = `char_${Date.now()}_${Math.random().toString(36).substring(2, 7)}`;
    const charRef = doc(db, 'characters', characterId);
    const characterData: Character = {
      id: characterId,
      lifeId: lifeData.id,
      intelligence,
      discipline,
      willpower,
      ambition,
      health,
      looks,
      smarts,
      happiness,
      fertility,
      energy,
      athleticPerformance,
      gender,
      sexuality,
      talent,
      eyeStyle,
      eyeColor,
      skinTone,
      browStyle,
      facialHairStyle,
      facialHairColor,
      hairStyle,
      hairColor,
      geneticHealthModifier,
      geneticIntelligenceModifier,
      geneticLooksModifier,
      birthCity,
      birthCountry,
      bankBalance: 1000, // Starting baseline bank balance
      karma: randomStat(30, 80), // HIDDEN server-side
      createdAt: now,
      updatedAt: now,
    };

    await setDoc(charRef, {
      ...characterData,
      userId: userData.id,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    });

    return {
      user: userData,
      saveSlot: saveSlotData,
      life: lifeData,
      character: this.toClientCharacter(characterData),
    };
  }

  /**
   * Updates character stats (e.g. from AgingService or Family events).
   */
  public static async updateCharacterStats(
    characterId: string,
    updates: Partial<Character>
  ): Promise<ClientCharacter> {
    const { karma, ...allowedUpdates } = updates;
    const charRef = doc(db, 'characters', characterId);
    await updateDoc(charRef, {
      ...allowedUpdates,
      updatedAt: serverTimestamp(),
    });

    const updatedSnap = await getDoc(charRef);
    const character = updatedSnap.data() as Character;
    return this.toClientCharacter(character);
  }

  /**
   * Updates life age.
   */
  public static async updateLifeAge(lifeId: string, currentAge: number): Promise<void> {
    const lifeRef = doc(db, 'lives', lifeId);
    await updateDoc(lifeRef, {
      currentAge,
      updatedAt: serverTimestamp(),
    });
  }

  /**
   * Retrieves life details with karma stripped.
   */
  public static async getLifeById(lifeId: string): Promise<LifeSummaryResponse | null> {
    const lifeRef = doc(db, 'lives', lifeId);
    const lifeSnap = await getDoc(lifeRef);
    if (!lifeSnap.exists()) return null;
    const life = lifeSnap.data() as Life;

    // Fetch saveSlot
    const slotRef = doc(db, 'saveSlots', life.saveSlotId);
    const slotSnap = await getDoc(slotRef);
    const saveSlot = slotSnap.exists()
      ? (slotSnap.data() as SaveSlot)
      : ({ id: life.saveSlotId, userId: '', slotName: 'Default', createdAt: new Date(), updatedAt: new Date() });

    // Fetch user
    const userRef = doc(db, 'users', saveSlot.userId);
    const userSnap = await getDoc(userRef);
    const user = userSnap.exists()
      ? (userSnap.data() as User)
      : ({ id: saveSlot.userId, username: 'Player', createdAt: new Date(), updatedAt: new Date() });

    // Fetch character by lifeId
    const charQ = query(collection(db, 'characters'), where('lifeId', '==', lifeId));
    const charDocs = await getDocs(charQ);
    if (charDocs.empty) return null;

    const charDoc = charDocs.docs[0];
    const character = charDoc.data() as Character;

    return {
      user,
      saveSlot,
      life,
      character: this.toClientCharacter(character),
    };
  }

  /**
   * Transitions NPC between Dormant and Active simulation states.
   */
  public static async setNpcDormancy(lifeId: string, isDormant: boolean) {
    const lifeRef = doc(db, 'lives', lifeId);
    await updateDoc(lifeRef, {
      isDormant,
      updatedAt: serverTimestamp(),
    });

    // Also update associated character isDormant if exists
    const charQ = query(collection(db, 'characters'), where('lifeId', '==', lifeId));
    const charDocs = await getDocs(charQ);
    for (const docItem of charDocs.docs) {
      await updateDoc(docItem.ref, {
        isDormant,
        updatedAt: serverTimestamp(),
      });
    }
  }

  /**
   * Resumes any life at any saved age.
   */
  public static async resumeLife(lifeId: string) {
    return this.getLifeById(lifeId);
  }
}

function randomStat(min: number, max: number): number {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}
