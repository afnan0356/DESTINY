'use client';

import React, { useState } from 'react';
import { CharacterAvatar } from './CharacterAvatar';
import { AgingService } from '@/lib/agingService';
import { DeathService } from '@/lib/deathService';
import { LifeSummaryResponse, ClientCharacter } from '@/types';

interface CharacterProfileProps {
  summary: LifeSummaryResponse;
  onUpdateSummary: (updated: LifeSummaryResponse) => void;
  onBackToCreator?: () => void;
}

interface SimLogEntry {
  id: string;
  year: number;
  age: number;
  text: string;
  isFatal?: boolean;
}

export const CharacterProfile: React.FC<CharacterProfileProps> = ({
  summary,
  onUpdateSummary,
  onBackToCreator,
}) => {
  const { user, saveSlot, life, character } = summary;

  const [currentAge, setCurrentAge] = useState<number>(life.currentAge);
  const [characterStats, setCharacterStats] = useState<ClientCharacter>(character);
  const [isDead, setIsDead] = useState<boolean>(false);
  const [deathCause, setDeathCause] = useState<string | null>(null);
  const [simLogs, setSimLogs] = useState<SimLogEntry[]>([
    {
      id: 'init',
      year: life.birthYear,
      age: 0,
      text: `Born in ${character.birthCity || 'New York'}, ${character.birthCountry || 'United States'}. Innate disposition initialized.`,
    },
  ]);
  const [isAging, setIsAging] = useState<boolean>(false);

  const handleAgeOneYear = async () => {
    if (isDead || isAging) return;
    setIsAging(true);

    const nextAge = currentAge + 1;
    const simYear = life.birthYear + nextAge;

    // 1. Run Aging Service
    const agingResult = AgingService.ageOneYear(characterStats, currentAge);

    // 2. Run Death Check Service
    const deathResult = DeathService.checkMortality(
      nextAge,
      agingResult.newStats.health,
      {
        intelligence: characterStats.intelligence,
        discipline: characterStats.discipline,
        willpower: characterStats.willpower,
        ambition: characterStats.ambition,
        looks: characterStats.looks,
        smarts: characterStats.smarts,
        happiness: characterStats.happiness,
      }
    );

    const updatedStats: ClientCharacter = {
      ...characterStats,
      health: agingResult.newStats.health,
      fertility: agingResult.newStats.fertility,
      energy: agingResult.newStats.energy,
      athleticPerformance: agingResult.newStats.athleticPerformance,
    };

    setCurrentAge(nextAge);
    setCharacterStats(updatedStats);

    const newLog: SimLogEntry = {
      id: `${simYear}-${Date.now()}`,
      year: simYear,
      age: nextAge,
      text: deathResult.isDead
        ? `FATAL EVENT: Deceased at age ${nextAge}. Cause: ${deathResult.cause}`
        : agingResult.narrative,
      isFatal: deathResult.isDead,
    };

    setSimLogs((prev) => [newLog, ...prev]);

    if (deathResult.isDead) {
      setIsDead(true);
      setDeathCause(deathResult.cause);
    }

    // Persist changes to server
    try {
      await fetch('/api/lives', {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          lifeId: life.id,
          characterId: character.id,
          age: nextAge,
          stats: {
            health: updatedStats.health,
            fertility: updatedStats.fertility,
            energy: updatedStats.energy,
            athleticPerformance: updatedStats.athleticPerformance,
          },
        }),
      });

      onUpdateSummary({
        ...summary,
        life: { ...life, currentAge: nextAge },
        character: updatedStats,
      });
    } catch (e) {
      console.error('Failed to sync aging to server', e);
    } finally {
      setIsAging(false);
    }
  };

  const attributeList = [
    { label: 'Health', value: characterStats.health, color: 'bg-emerald-500' },
    { label: 'Energy', value: characterStats.energy, color: 'bg-sky-500' },
    { label: 'Athletic Performance', value: characterStats.athleticPerformance, color: 'bg-amber-500' },
    { label: 'Fertility', value: characterStats.fertility, color: 'bg-pink-500' },
    { label: 'Happiness', value: characterStats.happiness, color: 'bg-yellow-400' },
    { label: 'Intelligence', value: characterStats.intelligence, color: 'bg-indigo-400' },
    { label: 'Smarts', value: characterStats.smarts, color: 'bg-blue-400' },
    { label: 'Discipline', value: characterStats.discipline, color: 'bg-teal-400' },
    { label: 'Willpower', value: characterStats.willpower, color: 'bg-purple-400' },
    { label: 'Ambition', value: characterStats.ambition, color: 'bg-violet-400' },
    { label: 'Looks', value: characterStats.looks, color: 'bg-rose-400' },
  ];

  return (
    <div className="bg-[#111723] border border-[#26354D] rounded-2xl p-6 sm:p-8 space-y-8 shadow-xl max-w-4xl mx-auto">
      {/* Header Banner */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-[#26354D] pb-6">
        <div className="flex items-center gap-5">
          <CharacterAvatar
            skinTone={characterStats.skinTone}
            eyeStyle={characterStats.eyeStyle}
            eyeColor={characterStats.eyeColor}
            browStyle={characterStats.browStyle}
            hairStyle={characterStats.hairStyle}
            hairColor={characterStats.hairColor}
            facialHairStyle={characterStats.facialHairStyle}
            facialHairColor={characterStats.facialHairColor}
            size={96}
          />
          <div className="space-y-1">
            <div className="flex items-center gap-2.5">
              <h1 className="text-2xl font-bold text-white tracking-tight">
                {life.name}
              </h1>
              {isDead ? (
                <span className="px-2.5 py-0.5 rounded text-xs font-bold bg-rose-500/20 text-rose-300 border border-rose-500/30">
                  DECEASED
                </span>
              ) : (
                <span className="px-2.5 py-0.5 rounded text-xs font-bold bg-emerald-500/20 text-emerald-300 border border-emerald-500/30">
                  ACTIVE LIFE
                </span>
              )}
            </div>

            <div className="text-xs text-slate-400 flex flex-wrap gap-x-3 gap-y-1">
              <span>
                Age: <strong className="text-white">{currentAge}</strong> ({life.birthYear + currentAge})
              </span>
              <span>•</span>
              <span>
                {characterStats.gender} • {characterStats.sexuality}
              </span>
              <span>•</span>
              <span>
                {characterStats.birthCity}, {characterStats.birthCountry}
              </span>
            </div>

            <div className="pt-1 flex items-center gap-2">
              <span className="text-[11px] font-semibold uppercase tracking-wider text-slate-400">
                Talent:
              </span>
              <span className="px-2 py-0.5 rounded-full text-xs font-bold bg-[#38BDF8]/15 text-[#38BDF8] border border-[#38BDF8]/30">
                {characterStats.talent || 'None'}
              </span>
            </div>
          </div>
        </div>

        {/* Action Controls */}
        <div className="flex items-center gap-3">
          {onBackToCreator && (
            <button
              onClick={onBackToCreator}
              className="px-4 py-2.5 rounded-xl border border-[#26354D] bg-[#0E1420] text-xs font-semibold text-slate-300 hover:text-white hover:border-slate-500 transition-all"
            >
              ← Creator
            </button>
          )}

          <button
            onClick={handleAgeOneYear}
            disabled={isDead || isAging}
            className={`px-6 py-2.5 rounded-xl text-xs font-bold uppercase tracking-wider shadow-lg transition-all ${
              isDead
                ? 'bg-slate-800 text-slate-500 cursor-not-allowed border border-slate-700'
                : isAging
                ? 'bg-[#0284C7] text-white opacity-75 cursor-wait'
                : 'bg-gradient-to-r from-[#0284C7] to-[#0EA5E9] text-white hover:brightness-110 active:scale-95'
            }`}
          >
            {isAging ? 'Simulating...' : isDead ? 'Life Concluded' : 'Age 1 Year (+1 Tick)'}
          </button>
        </div>
      </div>

      {/* Death Notice Modal / Card */}
      {isDead && (
        <div className="p-5 rounded-2xl bg-rose-950/40 border border-rose-500/40 space-y-2 text-center sm:text-left">
          <div className="flex items-center gap-2 text-rose-400 font-bold text-base">
            <span>⚰️</span>
            <h3>OBITUARY NOTICE</h3>
          </div>
          <p className="text-xs text-rose-200 leading-relaxed">
            {life.name} passed away at the age of {currentAge} (Year {life.birthYear + currentAge}).
            <br />
            <strong>Primary Cause:</strong> {deathCause || 'Natural causes'}.
          </p>
        </div>
      )}

      {/* Attributes Grid */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-sm font-semibold uppercase tracking-wider text-slate-300">
            Vital & Psychological Attributes
          </h2>
          <span className="text-[11px] text-slate-500 font-mono">
            Karma Strictly Masked (Privacy Policy Active)
          </span>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-3.5">
          {attributeList.map((attr) => (
            <div
              key={attr.label}
              className="p-3.5 rounded-xl bg-[#0E1420] border border-[#26354D] space-y-2"
            >
              <div className="flex items-center justify-between text-xs">
                <span className="font-medium text-slate-300">{attr.label}</span>
                <span className="font-mono font-bold text-white">{attr.value}</span>
              </div>
              <div className="w-full bg-[#1E293B] h-2 rounded-full overflow-hidden">
                <div
                  className={`h-full ${attr.color} transition-all duration-300`}
                  style={{ width: `${Math.min(100, Math.max(0, attr.value))}%` }}
                />
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Yearly Simulation Log */}
      <div className="space-y-3 pt-2">
        <h2 className="text-sm font-semibold uppercase tracking-wider text-slate-300">
          Chronological Event Ledger
        </h2>
        <div className="max-h-52 overflow-y-auto space-y-2 pr-1">
          {simLogs.map((log) => (
            <div
              key={log.id}
              className={`p-3 rounded-xl border text-xs leading-relaxed flex items-start gap-3 ${
                log.isFatal
                  ? 'bg-rose-950/25 border-rose-500/40 text-rose-300'
                  : 'bg-[#0E1420] border-[#26354D] text-slate-300'
              }`}
            >
              <span className="font-mono text-[#38BDF8] font-semibold whitespace-nowrap">
                Year {log.year} (Age {log.age}):
              </span>
              <span>{log.text}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
