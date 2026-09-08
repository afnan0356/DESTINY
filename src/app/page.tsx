'use client';

import React, { useState } from 'react';
import { evaluateFormula } from '@/lib/formulaEngine';
import { GameClockService } from '@/services/gameClock';
import { LifeSummaryResponse, TickResolution } from '@/types';
import { CharacterCreator } from '@/components/CharacterCreator';
import { CharacterProfile } from '@/components/CharacterProfile';

type ActiveTab = 'creator' | 'profile' | 'architecture';

export default function DestinyLandingPage() {
  const [activeTab, setActiveTab] = useState<ActiveTab>('creator');

  // GameClock Service Instance
  const [clockService] = useState(() => new GameClockService(2026, 'YEARLY'));
  const [clockDisplay, setClockDisplay] = useState(clockService.formatDisplay());

  // Active Life Summary State
  const [activeLifeSummary, setActiveLifeSummary] = useState<LifeSummaryResponse | null>(null);

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

  const handleCharacterCreated = (summary: LifeSummaryResponse) => {
    setActiveLifeSummary(summary);
    setActiveTab('profile');
  };

  return (
    <main className="max-w-5xl mx-auto px-4 sm:px-6 py-10 space-y-8">
      {/* Header Badge & Title */}
      <header className="space-y-3">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#0C3854]/60 border border-[#38BDF8]/30">
            <span className="w-2 h-2 rounded-full bg-[#10B981] animate-pulse" />
            <span className="text-xs font-semibold tracking-wider text-[#BAE6FD] uppercase">
              Project: Destiny • Master Build 02 of 20 • Character System
            </span>
          </div>

          <div className="text-xs font-mono text-slate-400 bg-[#0E1420] px-3 py-1 rounded-lg border border-[#26354D]">
            Clock: <span className="text-[#38BDF8] font-bold">{clockDisplay}</span>
          </div>
        </div>

        <div className="flex flex-col sm:flex-row sm:items-baseline justify-between gap-2">
          <h1 className="text-3xl sm:text-4xl font-black tracking-tight text-white">
            DESTINY
          </h1>
          <p className="text-xs text-slate-400">
            Next.js & Kotlin Android Multiplatform Parity • Real-Time Life Simulation
          </p>
        </div>

        {/* Navigation Tabs */}
        <div className="flex items-center gap-2 pt-2 border-b border-[#26354D]">
          <button
            onClick={() => setActiveTab('creator')}
            className={`px-4 py-2.5 text-xs font-bold uppercase tracking-wider border-b-2 transition-all ${
              activeTab === 'creator'
                ? 'border-[#38BDF8] text-[#38BDF8]'
                : 'border-transparent text-slate-400 hover:text-slate-200'
            }`}
          >
            Character Creator (7-Step Flow)
          </button>

          <button
            onClick={() => setActiveTab('profile')}
            className={`px-4 py-2.5 text-xs font-bold uppercase tracking-wider border-b-2 transition-all ${
              activeTab === 'profile'
                ? 'border-[#38BDF8] text-[#38BDF8]'
                : 'border-transparent text-slate-400 hover:text-slate-200'
            }`}
          >
            Character Profile & Aging {activeLifeSummary ? `(${activeLifeSummary.life.name})` : ''}
          </button>

          <button
            onClick={() => setActiveTab('architecture')}
            className={`px-4 py-2.5 text-xs font-bold uppercase tracking-wider border-b-2 transition-all ${
              activeTab === 'architecture'
                ? 'border-[#38BDF8] text-[#38BDF8]'
                : 'border-transparent text-slate-400 hover:text-slate-200'
            }`}
          >
            Foundation Architecture (Prompt 01)
          </button>
        </div>
      </header>

      {/* TAB 1: Character Creator */}
      {activeTab === 'creator' && (
        <CharacterCreator
          onCharacterCreated={handleCharacterCreated}
        />
      )}

      {/* TAB 2: Character Profile & Simulation */}
      {activeTab === 'profile' && (
        <div>
          {activeLifeSummary ? (
            <CharacterProfile
              summary={activeLifeSummary}
              onUpdateSummary={(upd) => setActiveLifeSummary(upd)}
              onBackToCreator={() => setActiveTab('creator')}
            />
          ) : (
            <div className="bg-[#111723] border border-[#26354D] rounded-2xl p-10 text-center space-y-4 max-w-xl mx-auto">
              <div className="w-12 h-12 mx-auto rounded-full bg-[#38BDF8]/10 flex items-center justify-center text-xl">
                👤
              </div>
              <h3 className="text-lg font-bold text-white">No Active Character Selected</h3>
              <p className="text-xs text-slate-400 leading-relaxed">
                Initialize a subject via the 7-step Character Creator to begin the live life simulation with progressive aging, mortality checks, and attribute tracking.
              </p>
              <button
                onClick={() => setActiveTab('creator')}
                className="px-5 py-2.5 bg-[#38BDF8] text-[#090D14] font-bold text-xs uppercase tracking-wider rounded-xl hover:bg-[#7DD3FC] transition-all"
              >
                Go to Character Creator
              </button>
            </div>
          )}
        </div>
      )}

      {/* TAB 3: Foundation Architecture (Prompt 01) */}
      {activeTab === 'architecture' && (
        <div className="space-y-8">
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
                Foundation (35%), Momentum (20%), Influence (15%), World Variables (15%), Luck (15%).
              </p>
              <div className="bg-[#090D14] p-2.5 rounded text-xs font-mono text-[#38BDF8] flex justify-between">
                <span>Sample Probe:</span>
                <span className="font-bold">{probeOutcome.compositeScore.toFixed(2)} pts</span>
              </div>
            </div>

            {/* Game Clock */}
            <div className="bg-[#111723] border border-[#26354D] rounded-xl p-5 space-y-3">
              <div className="flex items-center justify-between">
                <h3 className="text-sm font-semibold text-white">Game Clock</h3>
                <span className="text-xs font-bold text-[#38BDF8] bg-[#38BDF8]/10 px-2 py-0.5 rounded">
                  VARIABLE RESOLUTION
                </span>
              </div>
              <p className="text-xs text-[#94A3B8] leading-relaxed">
                Yearly default with contextual sub-ticks (Monthly, Weekly, Daily zoom).
              </p>
              <div className="flex items-center justify-between gap-2">
                <button
                  type="button"
                  onClick={handleAdvanceClock}
                  className="px-3 py-1.5 bg-[#1E293B] hover:bg-[#334155] text-xs font-mono text-white rounded transition-colors"
                >
                  Advance Tick
                </button>
                <div className="flex gap-1">
                  {(['YEARLY', 'MONTHLY', 'WEEKLY', 'DAILY'] as TickResolution[]).map((res) => (
                    <button
                      key={res}
                      type="button"
                      onClick={() => handleResolutionChange(res)}
                      className={`text-[10px] px-1.5 py-1 rounded font-mono ${
                        clockService.getState().resolution === res
                          ? 'bg-[#38BDF8] text-[#021626] font-bold'
                          : 'bg-[#182235] text-[#94A3B8]'
                      }`}
                    >
                      {res[0]}
                    </button>
                  ))}
                </div>
              </div>
            </div>

            {/* Relational Hierarchy */}
            <div className="bg-[#111723] border border-[#26354D] rounded-xl p-5 space-y-3">
              <div className="flex items-center justify-between">
                <h3 className="text-sm font-semibold text-white">Relational Hierarchy</h3>
                <span className="text-xs font-bold text-[#A855F7] bg-[#A855F7]/10 px-2 py-0.5 rounded">
                  STRICT CASCADE
                </span>
              </div>
              <p className="text-xs text-[#94A3B8] leading-relaxed">
                User → SaveSlot (1:N) → Life (1:N) → Character (1:1). Karma stripped at boundary.
              </p>
              <div className="bg-[#090D14] p-2 rounded text-[11px] font-mono text-[#94A3B8] text-center">
                User ➔ SaveSlot ➔ Life ➔ Character
              </div>
            </div>
          </section>
        </div>
      )}
    </main>
  );
}
