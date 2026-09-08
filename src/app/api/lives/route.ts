import { NextRequest, NextResponse } from 'next/server';
import { LifeService } from '@/services/lifeService';

/**
 * POST /api/lives
 * Creates User → SaveSlot → Life → Character atomically.
 * Returns confirmation with karma strictly omitted.
 */
export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const { username, slotName, characterName } = body;

    if (!username || !characterName) {
      return NextResponse.json(
        { error: 'username and characterName are required fields.' },
        { status: 400 }
      );
    }

    const summary = await LifeService.createNewLife({
      ...body,
      birthYear: body.birthYear ? Number(body.birthYear) : 2000,
      isDormant: Boolean(body.isDormant),
    });

    return NextResponse.json(summary, { status: 201 });
  } catch (error: any) {
    return NextResponse.json(
      { error: error?.message || 'Failed to create life' },
      { status: 500 }
    );
  }
}

/**
 * PATCH /api/lives
 * Updates life age or character stats (e.g. from AgingService).
 */
export async function PATCH(request: NextRequest) {
  try {
    const body = await request.json();
    const { lifeId, characterId, age, stats } = body;

    if (lifeId && typeof age === 'number') {
      await LifeService.updateLifeAge(lifeId, age);
    }

    if (characterId && stats) {
      const updatedCharacter = await LifeService.updateCharacterStats(characterId, stats);
      return NextResponse.json({ success: true, character: updatedCharacter });
    }

    return NextResponse.json({ success: true });
  } catch (error: any) {
    return NextResponse.json(
      { error: error?.message || 'Failed to update life record' },
      { status: 500 }
    );
  }
}

/**
 * GET /api/lives?id=...
 */
export async function GET(request: NextRequest) {
  try {
    const { searchParams } = new URL(request.url);
    const id = searchParams.get('id');

    if (!id) {
      return NextResponse.json({ error: 'Life ID is required.' }, { status: 400 });
    }

    const summary = await LifeService.getLifeById(id);
    if (!summary) {
      return NextResponse.json({ error: 'Life not found.' }, { status: 404 });
    }

    return NextResponse.json(summary);
  } catch (error: any) {
    return NextResponse.json(
      { error: error?.message || 'Failed to retrieve life' },
      { status: 500 }
    );
  }
}
