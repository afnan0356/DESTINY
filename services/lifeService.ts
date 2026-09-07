/**
 * LIFE SERVICE & PERSISTENCE ORCHESTRATOR (Prompt 01 of 20: FOUNDATION)
 *
 * Core service managing:
 * 1. Life creation pipeline (User -> SaveSlot -> Life -> Character)
 * 2. Unlimited save slots per user
 * 3. NPC Simulation Model:
 *    - 'dormant': Lightweight. Stats stored in DB, but not actively ticked year-to-year.
 *    - 'active': Full simulation active because player is currently interacting.
 * 4. Karma Privacy Mandate: Strips karma from all client-facing responses.
 */

import { prisma } from '../lib/prisma';
import {
  Character,
  ClientCharacter,
  CreateLifeInput,
  LifeSummaryResponse,
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
   * Atomic creation flow:
   * User → SaveSlot → Life → Character
   */
  public static async createNewLife(input: CreateLifeInput): Promise<LifeSummaryResponse> {
    const { username, slotName, characterName, birthYear = 2000, isDormant = false } = input;

    // Use Prisma interactive transaction for atomic execution across all 4 models
    const result = await prisma.$transaction(async (tx) => {
      // 1. Ensure or create User account
      const user = await tx.user.upsert({
        where: { username },
        update: {},
        create: { username },
      });

      // 2. Create SaveSlot under User (unlimited save slots supported)
      const saveSlot = await tx.saveSlot.create({
        data: {
          userId: user.id,
          slotName: slotName || `Save Slot ${Date.now()}`,
        },
      });

      // 3. Create Life entity attached to SaveSlot
      const life = await tx.life.create({
        data: {
          saveSlotId: saveSlot.id,
          name: characterName,
          birthYear,
          currentAge: 0,
          isDormant,
        },
      });

      // 4. Create Character entity attached to Life
      // Generates baseline intrinsic stats and internal hidden karma
      const character = await tx.character.create({
        data: {
          lifeId: life.id,
          intelligence: randomStat(45, 95),
          discipline: randomStat(40, 90),
          willpower: randomStat(40, 90),
          ambition: randomStat(45, 95),
          health: randomStat(60, 99),
          looks: randomStat(40, 90),
          smarts: randomStat(45, 95),
          happiness: randomStat(50, 90),
          karma: randomStat(30, 80), // Stored in DB, never exposed to client
        },
      });

      return { user, saveSlot, life, character };
    });

    return {
      user: result.user,
      saveSlot: result.saveSlot,
      life: result.life,
      character: this.toClientCharacter(result.character),
    };
  }

  /**
   * Retrieves life details with karma stripped.
   */
  public static async getLifeById(lifeId: string): Promise<LifeSummaryResponse | null> {
    const life = await prisma.life.findUnique({
      where: { id: lifeId },
      include: {
        saveSlot: {
          include: { user: true },
        },
        character: true,
      },
    });

    if (!life || !life.character) return null;

    return {
      user: life.saveSlot.user,
      saveSlot: life.saveSlot,
      life,
      character: this.toClientCharacter(life.character),
    };
  }

  /**
   * Transitions NPC between Dormant and Active simulation states.
   */
  public static async setNpcDormancy(lifeId: string, isDormant: boolean) {
    return prisma.life.update({
      where: { id: lifeId },
      data: { isDormant },
    });
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
