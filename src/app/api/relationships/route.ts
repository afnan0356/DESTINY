import { NextRequest, NextResponse } from 'next/server';
import { FamilyService } from '@/services/familyService';

/**
 * GET /api/relationships?characterId=...
 */
export async function GET(request: NextRequest) {
  try {
    const { searchParams } = new URL(request.url);
    const characterId = searchParams.get('characterId');

    if (!characterId) {
      return NextResponse.json({ error: 'characterId is required.' }, { status: 400 });
    }

    const relationships = await FamilyService.getRelationships(characterId);
    return NextResponse.json({ relationships });
  } catch (error: any) {
    return NextResponse.json(
      { error: error?.message || 'Failed to fetch relationships' },
      { status: 500 }
    );
  }
}

/**
 * POST /api/relationships
 * Actions: 'marry', 'divorce', 'haveChild', 'inherit', 'addEnemy'
 */
export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const { action } = body;

    if (action === 'marry') {
      const { characterId, partnerCharacterId, gameYear } = body;
      if (!characterId || !partnerCharacterId) {
        return NextResponse.json({ error: 'characterId and partnerCharacterId are required' }, { status: 400 });
      }
      const result = await FamilyService.marry(characterId, partnerCharacterId, gameYear || 2024);
      return NextResponse.json(result, { status: 200 });
    }

    if (action === 'divorce') {
      const { characterId, partnerCharacterId, gameYear } = body;
      if (!characterId || !partnerCharacterId) {
        return NextResponse.json({ error: 'characterId and partnerCharacterId are required' }, { status: 400 });
      }
      const result = await FamilyService.divorce(characterId, partnerCharacterId, gameYear || 2024);
      return NextResponse.json(result, { status: 200 });
    }

    if (action === 'haveChild') {
      const { parent1Id, parent2Id, childName, gameYear } = body;
      if (!parent1Id || !parent2Id || !childName) {
        return NextResponse.json({ error: 'parent1Id, parent2Id, and childName are required' }, { status: 400 });
      }
      const result = await FamilyService.haveChild(parent1Id, parent2Id, childName, gameYear || 2024);
      return NextResponse.json(result, { status: 201 });
    }

    if (action === 'inherit') {
      const { deceasedCharacterId, gameYear } = body;
      if (!deceasedCharacterId) {
        return NextResponse.json({ error: 'deceasedCharacterId is required' }, { status: 400 });
      }
      const result = await FamilyService.executeInheritance(deceasedCharacterId, gameYear || 2024);
      return NextResponse.json(result, { status: 200 });
    }

    if (action === 'addEnemy') {
      const { characterId, enemyCharacterId, gameYear, enemySocialCapital, enemyFamilyLeverage } = body;
      if (!characterId || !enemyCharacterId) {
        return NextResponse.json({ error: 'characterId and enemyCharacterId are required' }, { status: 400 });
      }
      const result = await FamilyService.addEnemy(
        characterId,
        enemyCharacterId,
        gameYear || 2024,
        enemySocialCapital,
        enemyFamilyLeverage
      );
      return NextResponse.json(result, { status: 201 });
    }

    return NextResponse.json({ error: `Unknown action: ${action}` }, { status: 400 });
  } catch (error: any) {
    return NextResponse.json(
      { error: error?.message || 'Failed to process relationship action' },
      { status: 500 }
    );
  }
}
