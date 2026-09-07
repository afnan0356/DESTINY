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
    const { username, slotName, characterName, birthYear, isDormant } = body;

    if (!username || !characterName) {
      return NextResponse.json(
        { error: 'username and characterName are required fields.' },
        { status: 400 }
      );
    }

    const summary = await LifeService.createNewLife({
      username,
      slotName: slotName || `Slot_${Date.now()}`,
      characterName,
      birthYear: birthYear ? Number(birthYear) : 2000,
      isDormant: Boolean(isDormant),
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
