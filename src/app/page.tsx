'use client';

import React, { useState } from 'react';
import { evaluateFormula } from '@/lib/formulaEngine';
import { GameClockService } from '@/services/gameClock';
import { LifeSummaryResponse, TickResolution } from '@/types';

export default function DestinyLandingPage() {
  // GameClock Service Instance
  const [clockService] = useState(() => new GameClockService(2026, 'YEARLY'));
  const [clockDisplay, setClockDisplay] = useState(clockService.formatDisplay());

  // Form State for "New Life" Flow
  const [username, setUsername] = useState('Player_Zero');
  const [slotName, setSlotName] = useState('Slot Alpha');
  const [characterName, setCharacterName] = useState('Julian Vance');
  const [birthYear, setBirthYear] = useState(2000);
  const [isDormant, setIsDormant] = useState(false);
  const [loading, setLoading] = useState(false);
  const [summary, setSummary] = useState<LifeSummaryResponse | null>(null);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  // Probe formula engine evaluation
  const [probeOutcome] = useState(() =>
    evaluateFormula({
      characterFoundation: {
        intelligence: 75,
        discipline: 70,
        willpower: 70,
        ambition: 85,
        health: 80,
        looks: 65,
        smarts: 78,
        happiness: 72,
      },
      momentum: { trajectoryScore: 0.3 },
      influence: { socialCapital: 45 },
      worldVariables: { economicCycleIndex: 1.1, stabilityIndex: 0.9 },
    })
  );

  const handleAdvanceClock = () => {
    clockService.tick(1);
    setClockDisplay(clockService.formatDisplay());
  };

  const handleResolutionChange = (res: TickResolution) => {
    if (res === 'YEARLY') {
      clockService.exitEventZoom();
    } else {
      clockService.enterEventZoom(res as any);
    }
    setClockDisplay(clockService.formatDisplay());
  };

  const handleCreateLife = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setErrorMsg(null);

    try {
      const res = await fetch('/api/lives', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username,
          slotName,
          characterName,
          birthYear,
          isDormant,
        }),
      });

      if (!res.ok) {
        const data = await res.json().catch(() => ({}));
        throw new Error(data.error || 'Failed to persist record.');
      }

      const data: LifeSummaryResponse = await res.json();
      setSummary(data);
    } catch (err: any) {
      setErrorMsg(err.message || 'Error executing New Life flow.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="max-w-4xl mx-auto px-6 py-12 space-y-10">
      {/* Header Badge */}
      <header className="space-y-3">
        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#0C3854]/60 border border-[#38BDF8]/30">
          <span className="w-2 h-2 rounded-full bg-[#10B981] animate-pulse" />
          <span className="text-xs font-semibold tracking-wider text-[#BAE6FD] uppercase">
            Build 01 of 20 • Technical Foundation
          </span>
        </div>
        <h1 className="text-4xl sm:text-5xl font-black tracking-tight text-white">
          DESTINY
        </h1>
        <p className="text-base text-[#94A3B8] font-normal">
          Ultra-deep life simulation architecture • Infrastructure & Schema Initialization
        </p>
      </header>

      {/* Core Architectural Pillars */}
      <section className="grid grid-cols-1 md:grid-cols-3 gap-5">
        {/* Formula Engine */}
        <div className="bg-[#111723] border border-[#26354D] rounded-xl p-5 space-y-3">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-semibold text-white">Formula Engine</h3>
            <span className="text-xs font-bold text-[#10B981] bg-[#10B981]/10 px-2 py-0.5 rounded">
              5 LAYERS
            </span>
          </div>
          <p className="text-xs text-[#94A3B8] leading-relaxed">
            Shared resolution engine: Foundation, Momentum, Influence, World Variables, and Luck.
          </p>
          <div className="bg-[#090D14] p-2.5 rounded text-xs font-mono text-[#38BDF8] flex justify-between">
            <span>Sample Probe:</span>
            <span>{probeOutcome.compositeScore}/100 ({probeOutcome.tier})</span>
          </div>
        </div>

        {/* GameClock Timekeeper */}
        <div className="bg-[#111723] border border-[#26354D] rounded-xl p-5 space-y-3">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-semibold text-white">GameClock</h3>
            <span className="text-xs font-bold text-[#38BDF8] bg-[#0C3854] px-2 py-0.5 rounded">
              VARIABLE
            </span>
          </div>
          <p className="text-xs text-[#94A3B8] leading-relaxed">
            Defaults to yearly ticks; dynamically accepts monthly/weekly zoom for active events.
          </p>
          <div className="flex items-center justify-between bg-[#090D14] p-2.5 rounded text-xs font-mono text-white">
            <span>{clockDisplay}</span>
            <button
              onClick={handleAdvanceClock}
              className="text-[#38BDF8] hover:underline font-semibold"
            >
              +1 Tick
            </button>
          </div>
          <div className="flex gap-2 text-[10px]">
            <button
              onClick={() => handleResolutionChange('YEARLY')}
              className="px-2 py-1 bg-[#182235] hover:bg-[#212C42] rounded text-[#94A3B8]"
            >
              Yearly
            </button>
            <button
              onClick={() => handleResolutionChange('MONTHLY')}
              className="px-2 py-1 bg-[#182235] hover:bg-[#212C42] rounded text-[#94A3B8]"
            >
              Monthly Zoom
            </button>
          </div>
        </div>

        {/* NPC Model & Karma Encapsulation */}
        <div className="bg-[#111723] border border-[#26354D] rounded-xl p-5 space-y-3">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-semibold text-white">NPC & Karma</h3>
            <span className="text-xs font-bold text-[#10B981] bg-[#10B981]/10 px-2 py-0.5 rounded">
              SECURED
            </span>
          </div>
          <p className="text-xs text-[#94A3B8] leading-relaxed">
            Dual states: Dormant (lightweight storage) vs. Active (full ticks). Raw karma hidden from client responses.
          </p>
          <div className="bg-[#090D14] p-2.5 rounded text-xs font-mono text-[#10B981]">
            ✓ ClientCharacter strips Karma
          </div>
        </div>
      </section>

      {/* New Life Flow Form */}
      <section className="bg-[#111723] border border-[#26354D] rounded-xl p-6 space-y-6">
        <div className="border-b border-[#26354D] pb-4">
          <h2 className="text-lg font-bold text-white tracking-wide">
            NEW LIFE FLOW (END-TO-END WRITE VERIFICATION)
          </h2>
          <p className="text-xs text-[#94A3B8] mt-1">
            Executes atomic write across User → SaveSlot → Life → Character entities.
          </p>
        </div>

        <form onSubmit={handleCreateLife} className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-[#94A3B8] mb-1">
                1. Player Account Name (User)
              </label>
              <input
                type="text"
                required
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="w-full px-3 py-2 bg-[#090D14] border border-[#26354D] rounded-lg text-sm text-white focus:outline-none focus:border-[#38BDF8]"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-[#94A3B8] mb-1">
                2. Save Slot Name (SaveSlot)
              </label>
              <input
                type="text"
                required
                value={slotName}
                onChange={(e) => setSlotName(e.target.value)}
                className="w-full px-3 py-2 bg-[#090D14] border border-[#26354D] rounded-lg text-sm text-white focus:outline-none focus:border-[#38BDF8]"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-[#94A3B8] mb-1">
                3. Subject Name (Life)
              </label>
              <input
                type="text"
                required
                value={characterName}
                onChange={(e) => setCharacterName(e.target.value)}
                className="w-full px-3 py-2 bg-[#090D14] border border-[#26354D] rounded-lg text-sm text-white focus:outline-none focus:border-[#38BDF8]"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-[#94A3B8] mb-1">
                Birth Year
              </label>
              <input
                type="number"
                required
                value={birthYear}
                onChange={(e) => setBirthYear(Number(e.target.value))}
                className="w-full px-3 py-2 bg-[#090D14] border border-[#26354D] rounded-lg text-sm text-white focus:outline-none focus:border-[#38BDF8]"
              />
            </div>
          </div>

          <div className="flex items-center gap-3 pt-2">
            <label className="text-xs font-medium text-[#94A3B8]">
              NPC Simulation Mode:
            </label>
            <button
              type="button"
              onClick={() => setIsDormant(!isDormant)}
              className={`px-3 py-1 text-xs rounded-full border transition-colors ${
                isDormant
                  ? 'bg-[#182235] text-[#94A3B8] border-[#26354D]'
                  : 'bg-[#10B981]/20 text-[#10B981] border-[#10B981]/40 font-semibold'
              }`}
            >
              {isDormant ? 'Dormant (Lightweight)' : 'Active (Simulated)'}
            </button>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full sm:w-auto px-6 py-2.5 bg-[#38BDF8] hover:bg-[#0EA5E9] text-[#021626] font-bold text-sm rounded-lg transition-colors disabled:opacity-50"
          >
            {loading ? 'Committing to Database...' : 'Commit New Life Record'}
          </button>
        </form>

        {errorMsg && (
          <div className="p-3 rounded-lg bg-[#F87171]/10 border border-[#F87171]/30 text-xs text-[#F87171]">
            {errorMsg}
          </div>
        )}

        {/* Database Write Confirmation */}
        {summary && (
          <div className="mt-6 p-5 rounded-xl bg-[#090D14] border border-[#10B981]/40 space-y-4">
            <div className="flex items-center justify-between">
              <span className="text-xs font-bold text-[#10B981] tracking-wider uppercase">
                ✓ Database Write Confirmed
              </span>
              <span className="text-[10px] font-mono text-[#94A3B8]">
                Prisma PostgreSQL / Room
              </span>
            </div>

            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 text-xs font-mono">
              <div className="p-2 bg-[#111723] rounded">
                <span className="text-[#64748B] block text-[10px]">User PK</span>
                <span className="text-white truncate block">{summary.user.id.slice(0, 10)}...</span>
              </div>
              <div className="p-2 bg-[#111723] rounded">
                <span className="text-[#64748B] block text-[10px]">Slot PK</span>
                <span className="text-white truncate block">{summary.saveSlot.id.slice(0, 10)}...</span>
              </div>
              <div className="p-2 bg-[#111723] rounded">
                <span className="text-[#64748B] block text-[10px]">Life PK</span>
                <span className="text-white truncate block">{summary.life.id.slice(0, 10)}...</span>
              </div>
              <div className="p-2 bg-[#111723] rounded">
                <span className="text-[#64748B] block text-[10px]">Character PK</span>
                <span className="text-white truncate block">{summary.character.id.slice(0, 10)}...</span>
              </div>
            </div>

            <div className="pt-2 border-t border-[#1E293B]">
              <span className="text-[11px] font-semibold text-[#94A3B8] block mb-2">
                Generated Core Stats (Karma excluded from response):
              </span>
              <div className="grid grid-cols-4 gap-2 text-xs font-mono text-center">
                <div className="bg-[#182235] p-1.5 rounded">INT: {summary.character.intelligence}</div>
                <div className="bg-[#182235] p-1.5 rounded">DIS: {summary.character.discipline}</div>
                <div className="bg-[#182235] p-1.5 rounded">WIL: {summary.character.willpower}</div>
                <div className="bg-[#182235] p-1.5 rounded">AMB: {summary.character.ambition}</div>
                <div className="bg-[#182235] p-1.5 rounded">HLT: {summary.character.health}</div>
                <div className="bg-[#182235] p-1.5 rounded">LOK: {summary.character.looks}</div>
                <div className="bg-[#182235] p-1.5 rounded">SMA: {summary.character.smarts}</div>
                <div className="bg-[#182235] p-1.5 rounded">HAP: {summary.character.happiness}</div>
              </div>
            </div>
          </div>
        )}
      </section>
    </main>
  );
}
