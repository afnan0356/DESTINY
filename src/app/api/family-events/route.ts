import { NextRequest, NextResponse } from 'next/server';
import { FamilyService } from '@/services/familyService';

/**
 * GET /api/family-events?characterId=...
 */
export async function GET(request: NextRequest) {
  try {
    const { searchParams } = new URL(request.url);
    const characterId = searchParams.get('characterId');

    if (!characterId) {
      return NextResponse.json({ error: 'characterId is required.' }, { status: 400 });
    }

    const events = await FamilyService.getFamilyEvents(characterId);
    return NextResponse.json({ events });
  } catch (error: any) {
    return NextResponse.json(
      { error: error?.message || 'Failed to fetch family events' },
      { status: 500 }
    );
  }
}
