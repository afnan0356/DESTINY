/**
 * FAMILY SERVICE & RELATIONSHIP ORCHESTRATOR
 * Master Build Prompt 03 of 20: FAMILY SYSTEM
 *
 * Core service managing:
 * 1. Relationship collection in Firestore (Spouse, Ex, Parent, Child, Sibling, Friend, BestFriend, Enemy)
 * 2. Marriage & Divorce with Dormant -> Active NPC promotion & asset splitting (bankBalance)
 * 3. Children creation with true 5-layer FormulaEngine genetic blending
 * 4. Inheritance hook distribution on death
 * 5. Family drama event logging (Firestore collection: familyEvents)
 * 6. Strict Karma privacy enforcement
 */

import {
  collection,
  doc,
  getDoc,
  getDocs,
  query,
  setDoc,
  updateDoc,
  where,
  serverTimestamp,
  orderBy,
} from 'firebase/firestore';
import { db } from '../lib/firebase';
import {
  Character,
  ChildBirthResult,
  ClientCharacter,
  DivorceResult,
  FamilyEvent,
  InheritanceResult,
  MarriageResult,
  Relationship,
  RelationshipType,
} from '../types';
import { calculateEnemySabotageChance, calculateGeneticModifier } from '../lib/formulaEngine';
import { DeathService } from '../lib/deathService';
import { LifeService } from './lifeService';

export class FamilyService {
  /**
   * Sanitizes a Character to ClientCharacter (stripping karma)
   */
  public static sanitize(character: Character): ClientCharacter {
    return LifeService.toClientCharacter(character);
  }

  /**
   * Retrieves all relationships for a given character,
   * hydrating related character metadata with karma strictly omitted.
   */
  public static async getRelationships(characterId: string): Promise<Relationship[]> {
    const q = query(
      collection(db, 'relationships'),
      where('characterId', '==', characterId)
    );
    const snap = await getDocs(q);

    const relationships: Relationship[] = [];
    for (const docSnap of snap.docs) {
      const data = docSnap.data() as Relationship;

      // Hydrate related character data
      let relatedChar: ClientCharacter | undefined = undefined;
      if (data.relatedCharacterId) {
        const charRef = doc(db, 'characters', data.relatedCharacterId);
        const charSnap = await getDoc(charRef);
        if (charSnap.exists()) {
          relatedChar = this.sanitize(charSnap.data() as Character);
        }
      }

      relationships.push({
        ...data,
        id: docSnap.id,
        relatedCharacter: relatedChar,
      });
    }

    return relationships;
  }

  /**
   * Retrieves family event log for a character.
   */
  public static async getFamilyEvents(characterId: string): Promise<FamilyEvent[]> {
    const q = query(
      collection(db, 'familyEvents'),
      where('characterId', '==', characterId)
    );
    const snap = await getDocs(q);

    return snap.docs.map((d) => ({
      ...(d.data() as FamilyEvent),
      id: d.id,
    }));
  }

  /**
   * Logs a family drama event to the Firestore familyEvents collection.
   */
  public static async logFamilyEvent(
    characterId: string,
    relatedCharacterId: string | null,
    eventType: string,
    gameYear: number,
    description: string
  ): Promise<FamilyEvent> {
    const eventId = `fevent_${Date.now()}_${Math.random().toString(36).substring(2, 6)}`;
    const eventRef = doc(db, 'familyEvents', eventId);
    const eventData: FamilyEvent = {
      id: eventId,
      characterId,
      relatedCharacterId,
      eventType,
      gameYear,
      description,
      createdAt: new Date().toISOString(),
    };

    await setDoc(eventRef, {
      ...eventData,
      createdAt: serverTimestamp(),
    });

    return eventData;
  }

  /**
   * MARRIAGE FLOW:
   * 1. Creates a Spouse relationship between character and partner.
   * 2. If partner is a dormant NPC, promotes them to active state.
   * 3. Logs a Marriage familyEvent.
   */
  public static async marry(
    characterId: string,
    partnerCharacterId: string,
    gameYear: number,
    initialStrength = 85
  ): Promise<MarriageResult> {
    // Check partner dormancy state
    const partnerRef = doc(db, 'characters', partnerCharacterId);
    const partnerSnap = await getDoc(partnerRef);
    let promoted = false;

    if (partnerSnap.exists()) {
      const partnerData = partnerSnap.data() as Character;
      if (partnerData.isDormant) {
        await updateDoc(partnerRef, {
          isDormant: false,
          updatedAt: serverTimestamp(),
        });
        if (partnerData.lifeId) {
          const lifeRef = doc(db, 'lives', partnerData.lifeId);
          await updateDoc(lifeRef, {
            isDormant: false,
            updatedAt: serverTimestamp(),
          });
        }
        promoted = true;
      }
    }

    // Create or update character -> partner Spouse relationship
    const relId = `rel_${characterId}_${partnerCharacterId}`;
    const relRef = doc(db, 'relationships', relId);
    const relData: Relationship = {
      id: relId,
      characterId,
      relatedCharacterId: partnerCharacterId,
      type: 'Spouse',
      relationshipStrength: initialStrength,
      status: 'Active',
      startedAt: gameYear,
      endedAt: null,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    await setDoc(relRef, {
      ...relData,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    });

    // Create reciprocal partner -> character Spouse relationship
    const recipId = `rel_${partnerCharacterId}_${characterId}`;
    const recipRef = doc(db, 'relationships', recipId);
    await setDoc(recipRef, {
      id: recipId,
      characterId: partnerCharacterId,
      relatedCharacterId: characterId,
      type: 'Spouse',
      relationshipStrength: initialStrength,
      status: 'Active',
      startedAt: gameYear,
      endedAt: null,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    });

    const familyEvent = await this.logFamilyEvent(
      characterId,
      partnerCharacterId,
      'Marriage',
      gameYear,
      `Entered a holy matrimony with their partner in year ${gameYear}.`
    );

    return {
      spouseRelationship: relData,
      promotedFromDormant: promoted,
      familyEvent,
    };
  }

  /**
   * DIVORCE FLOW:
   * 1. Ends the Spouse relationship (status: Ended, endedAt: gameYear).
   * 2. Creates an Ex relationship (status: Active).
   * 3. Splits assets: (character.bankBalance + partner.bankBalance) / 2.
   * 4. Logs a Divorce familyEvent.
   */
  public static async divorce(
    characterId: string,
    partnerCharacterId: string,
    gameYear: number
  ): Promise<DivorceResult> {
    const relId = `rel_${characterId}_${partnerCharacterId}`;
    const relRef = doc(db, 'relationships', relId);
    const existingSnap = await getDoc(relRef);
    const prevStrength = existingSnap.exists() ? existingSnap.data().relationshipStrength ?? 50 : 50;

    // Mark previous Spouse as Ended
    await updateDoc(relRef, {
      status: 'Ended',
      endedAt: gameYear,
      updatedAt: serverTimestamp(),
    });

    // Create Ex relationship
    const exRelId = `rel_ex_${characterId}_${partnerCharacterId}`;
    const exRelRef = doc(db, 'relationships', exRelId);
    const exRelData: Relationship = {
      id: exRelId,
      characterId,
      relatedCharacterId: partnerCharacterId,
      type: 'Ex',
      relationshipStrength: Math.max(0, prevStrength - 40),
      status: 'Active',
      startedAt: gameYear,
      endedAt: null,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    await setDoc(exRelRef, {
      ...exRelData,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    });

    // Also update reciprocal relationship
    const recipRelId = `rel_${partnerCharacterId}_${characterId}`;
    const recipRef = doc(db, 'relationships', recipRelId);
    const recipSnap = await getDoc(recipRef);
    if (recipSnap.exists()) {
      await updateDoc(recipRef, {
        status: 'Ended',
        endedAt: gameYear,
        updatedAt: serverTimestamp(),
      });
    }

    // Split assets between characters
    const charRef = doc(db, 'characters', characterId);
    const partnerRef = doc(db, 'characters', partnerCharacterId);

    const [charSnap, partnerCharSnap] = await Promise.all([
      getDoc(charRef),
      getDoc(partnerRef),
    ]);

    const balanceA = charSnap.exists() ? (charSnap.data().bankBalance ?? 1000) : 1000;
    const balanceB = partnerCharSnap.exists() ? (partnerCharSnap.data().bankBalance ?? 1000) : 1000;
    const totalWealth = balanceA + balanceB;
    const splitShare = Math.floor(totalWealth / 2);

    await Promise.all([
      updateDoc(charRef, { bankBalance: splitShare, updatedAt: serverTimestamp() }),
      updateDoc(partnerRef, { bankBalance: splitShare, updatedAt: serverTimestamp() }),
    ]);

    const familyEvent = await this.logFamilyEvent(
      characterId,
      partnerCharacterId,
      'Divorce',
      gameYear,
      `Divorced in year ${gameYear}. Marital assets of $${totalWealth.toLocaleString()} split equally ($${splitShare.toLocaleString()} each).`
    );

    return {
      exRelationship: exRelData,
      characterBalance: splitShare,
      partnerBalance: splitShare,
      familyEvent,
    };
  }

  /**
   * CHILDREN & GENETICS FLOW:
   * 1. Creates a new Character document with real weighted blend of parents' stats
   *    and FormulaEngine Luck-based genetic modifiers.
   * 2. New child defaults to active (isDormant = false).
   * 3. Creates Parent/Child relationships between both parents and child.
   * 4. Logs a Birth familyEvent.
   */
  public static async haveChild(
    parent1Id: string,
    parent2Id: string,
    childName: string,
    gameYear: number,
    forcedGender?: 'Male' | 'Female'
  ): Promise<ChildBirthResult> {
    const parent1Ref = doc(db, 'characters', parent1Id);
    const parent2Ref = doc(db, 'characters', parent2Id);
    const [p1Snap, p2Snap] = await Promise.all([getDoc(parent1Ref), getDoc(parent2Ref)]);

    if (!p1Snap.exists() || !p2Snap.exists()) {
      throw new Error('Both parents must exist to have a child.');
    }

    const p1 = p1Snap.data() as Character;
    const p2 = p2Snap.data() as Character;

    // Real weighted blend of both parents' stats + FormulaEngine Luck layer variance
    const gHealthMod = calculateGeneticModifier(p1.health, p2.health);
    const gIntMod = calculateGeneticModifier(p1.intelligence, p2.intelligence);
    const gLooksMod = calculateGeneticModifier(p1.looks, p2.looks);

    const childGender = forcedGender || (Math.random() > 0.5 ? 'Male' : 'Female');
    const childId = `char_child_${Date.now()}_${Math.random().toString(36).substring(2, 6)}`;
    const childLifeId = `life_child_${Date.now()}_${Math.random().toString(36).substring(2, 6)}`;

    // Blend intrinsic foundation stats
    const childHealth = Math.max(10, Math.min(100, Math.round(((p1.health + p2.health) / 2) + gHealthMod)));
    const childInt = Math.max(10, Math.min(100, Math.round(((p1.intelligence + p2.intelligence) / 2) + gIntMod)));
    const childLooks = Math.max(10, Math.min(100, Math.round(((p1.looks + p2.looks) / 2) + gLooksMod)));
    const childSmarts = Math.max(10, Math.min(100, Math.round((p1.smarts + p2.smarts) / 2)));
    const childDiscipline = Math.max(10, Math.min(100, Math.round((p1.discipline + p2.discipline) / 2)));
    const childWillpower = Math.max(10, Math.min(100, Math.round((p1.willpower + p2.willpower) / 2)));
    const childAmbition = Math.max(10, Math.min(100, Math.round((p1.ambition + p2.ambition) / 2)));

    // Create child Life document
    const lifeRef = doc(db, 'lives', childLifeId);
    await setDoc(lifeRef, {
      id: childLifeId,
      saveSlotId: p1.lifeId || 'default',
      name: childName,
      birthYear: gameYear,
      currentAge: 0,
      isDormant: false, // Default active per prompt requirements
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    });

    // Create child Character document
    const childCharRef = doc(db, 'characters', childId);
    const childData: Character = {
      id: childId,
      lifeId: childLifeId,
      intelligence: childInt,
      discipline: childDiscipline,
      willpower: childWillpower,
      ambition: childAmbition,
      health: childHealth,
      looks: childLooks,
      smarts: childSmarts,
      happiness: 90,
      fertility: 80,
      energy: 100,
      athleticPerformance: Math.round((p1.athleticPerformance + p2.athleticPerformance) / 2),
      gender: childGender,
      sexuality: 'Heterosexual',
      talent: 'None',
      eyeStyle: p1.eyeStyle || 'Almond',
      eyeColor: Math.random() > 0.5 ? p1.eyeColor : p2.eyeColor,
      skinTone: Math.random() > 0.5 ? p1.skinTone : p2.skinTone,
      browStyle: p1.browStyle || 'Straight',
      facialHairStyle: 'Clean Shaven',
      facialHairColor: p1.hairColor || 'Black',
      hairStyle: childGender === 'Male' ? 'Short Crop' : 'Long Waves',
      hairColor: Math.random() > 0.5 ? p1.hairColor : p2.hairColor,
      geneticHealthModifier: gHealthMod,
      geneticIntelligenceModifier: gIntMod,
      geneticLooksModifier: gLooksMod,
      birthCity: p1.birthCity || 'New York',
      birthCountry: p1.birthCountry || 'United States',
      bankBalance: 0,
      karma: Math.floor(Math.random() * 50) + 30, // Stripped from client
      createdAt: new Date(),
      updatedAt: new Date(),
    };

    await setDoc(childCharRef, {
      ...childData,
      isDormant: false,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    });

    // Create Parent -> Child relationships
    const p1ToChildRelId = `rel_${parent1Id}_${childId}`;
    const p1ToChildRel: Relationship = {
      id: p1ToChildRelId,
      characterId: parent1Id,
      relatedCharacterId: childId,
      type: 'Child',
      relationshipStrength: 95,
      status: 'Active',
      startedAt: gameYear,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    await setDoc(doc(db, 'relationships', p1ToChildRelId), {
      ...p1ToChildRel,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    });

    const childToP1RelId = `rel_${childId}_${parent1Id}`;
    const childToP1Rel: Relationship = {
      id: childToP1RelId,
      characterId: childId,
      relatedCharacterId: parent1Id,
      type: 'Parent',
      relationshipStrength: 100,
      status: 'Active',
      startedAt: gameYear,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    await setDoc(doc(db, 'relationships', childToP1RelId), {
      ...childToP1Rel,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    });

    // Parent 2 relationships
    const p2ToChildRelId = `rel_${parent2Id}_${childId}`;
    await setDoc(doc(db, 'relationships', p2ToChildRelId), {
      id: p2ToChildRelId,
      characterId: parent2Id,
      relatedCharacterId: childId,
      type: 'Child',
      relationshipStrength: 95,
      status: 'Active',
      startedAt: gameYear,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    });

    const childToP2RelId = `rel_${childId}_${parent2Id}`;
    await setDoc(doc(db, 'relationships', childToP2RelId), {
      id: childToP2RelId,
      characterId: childId,
      relatedCharacterId: parent2Id,
      type: 'Parent',
      relationshipStrength: 100,
      status: 'Active',
      startedAt: gameYear,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    });

    const familyEvent = await this.logFamilyEvent(
      parent1Id,
      childId,
      'Birth',
      gameYear,
      `Welcomed their child, ${childName}, into the world in year ${gameYear}.`
    );

    return {
      child: this.sanitize(childData),
      parentToChildRel: p1ToChildRel,
      childToParentRel: childToP1Rel,
      familyEvent,
    };
  }

  /**
   * INHERITANCE HOOK ON DEATH:
   * Splits deceased character's bankBalance equally among living Child relationships.
   * If no living children, inherits to spouse.
   * Otherwise logs to placeholder "Estate".
   */
  public static async executeInheritance(
    deceasedCharacterId: string,
    gameYear: number
  ): Promise<InheritanceResult> {
    const charRef = doc(db, 'characters', deceasedCharacterId);
    const charSnap = await getDoc(charRef);
    if (!charSnap.exists()) {
      throw new Error('Deceased character not found.');
    }

    const charData = charSnap.data() as Character;
    const bankBalance = charData.bankBalance ?? 0;

    // Fetch relationships
    const rels = await this.getRelationships(deceasedCharacterId);

    // Call DeathService inheritance hook
    const result = DeathService.resolveInheritance(deceasedCharacterId, bankBalance, rels);

    // Apply distribution in Firestore
    for (const beneficiary of result.beneficiaries) {
      const bRef = doc(db, 'characters', beneficiary.characterId);
      const bSnap = await getDoc(bRef);
      if (bSnap.exists()) {
        const currentBal = bSnap.data().bankBalance ?? 0;
        await updateDoc(bRef, {
          bankBalance: currentBal + beneficiary.amount,
          updatedAt: serverTimestamp(),
        });
      }
    }

    // Zero out deceased character's bank balance
    await updateDoc(charRef, {
      bankBalance: 0,
      updatedAt: serverTimestamp(),
    });

    // Log Death / Inheritance event
    await this.logFamilyEvent(
      deceasedCharacterId,
      null,
      'Death',
      gameYear,
      result.description
    );

    return result;
  }

  /**
   * Creates an Enemy relationship with sabotageChance derived from FormulaEngine.
   */
  public static async addEnemy(
    characterId: string,
    enemyCharacterId: string,
    gameYear: number,
    enemySocialCapital = 50,
    enemyFamilyLeverage = 50
  ): Promise<Relationship> {
    const sabotageChance = calculateEnemySabotageChance({
      socialCapital: enemySocialCapital,
      familyLeverage: enemyFamilyLeverage,
    });

    const relId = `rel_${characterId}_${enemyCharacterId}`;
    const relRef = doc(db, 'relationships', relId);
    const relData: Relationship = {
      id: relId,
      characterId,
      relatedCharacterId: enemyCharacterId,
      type: 'Enemy',
      relationshipStrength: 10,
      status: 'Active',
      startedAt: gameYear,
      sabotageChance,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };

    await setDoc(relRef, {
      ...relData,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    });

    return relData;
  }
}
