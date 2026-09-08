'use client';

import React, { useState } from 'react';
import {
  COUNTRIES_AND_CITIES,
  COUNTRIES,
  GENDERS,
  SEXUALITIES,
  TALENTS,
  SKIN_TONES,
  EYE_COLORS,
  EYE_STYLES,
  BROW_STYLES,
  HAIR_COLORS,
  HAIR_STYLES,
  FACIAL_HAIR_STYLES,
  getRandomName,
} from '@/lib/characterData';
import { CharacterAvatar } from './CharacterAvatar';
import { LifeSummaryResponse, TalentType } from '@/types';

interface CharacterCreatorProps {
  onCharacterCreated: (summary: LifeSummaryResponse) => void;
  onCancel?: () => void;
}

export const CharacterCreator: React.FC<CharacterCreatorProps> = ({
  onCharacterCreated,
  onCancel,
}) => {
  const [step, setStep] = useState<number>(1);

  // Step 1 & 2: Country & City
  const [country, setCountry] = useState<string>('United States');
  const [city, setCity] = useState<string>('New York');

  // Step 3: Name
  const [firstName, setFirstName] = useState<string>('Julian');
  const [lastName, setLastName] = useState<string>('Vance');

  // Step 4: Gender & Sexuality
  const [gender, setGender] = useState<string>('Male');
  const [sexuality, setSexuality] = useState<string>('Heterosexual');

  // Step 5: Special Talent
  const [talent, setTalent] = useState<TalentType>('None');

  // Step 6: Appearance
  const [skinTone, setSkinTone] = useState<string>('Fair');
  const [eyeStyle, setEyeStyle] = useState<string>('Almond');
  const [eyeColor, setEyeColor] = useState<string>('Brown');
  const [browStyle, setBrowStyle] = useState<string>('Straight');
  const [facialHairStyle, setFacialHairStyle] = useState<string>('Clean Shaven');
  const [facialHairColor, setFacialHairColor] = useState<string>('Black');
  const [hairStyle, setHairStyle] = useState<string>('Short Crop');
  const [hairColor, setHairColor] = useState<string>('Black');

  // Step 7: Attributes (Karma is strictly omitted)
  const [intelligence, setIntelligence] = useState<number>(75);
  const [discipline, setDiscipline] = useState<number>(68);
  const [willpower, setWillpower] = useState<number>(70);
  const [ambition, setAmbition] = useState<number>(78);
  const [health, setHealth] = useState<number>(90);
  const [looks, setLooks] = useState<number>(72);
  const [smarts, setSmarts] = useState<number>(75);
  const [happiness, setHappiness] = useState<number>(80);
  const [fertility, setFertility] = useState<number>(85);
  const [energy, setEnergy] = useState<number>(95);
  const [athleticPerformance, setAthleticPerformance] = useState<number>(70);

  // Submitting state
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  const availableCities = COUNTRIES_AND_CITIES[country] || [];

  const handleCountryChange = (newCountry: string) => {
    setCountry(newCountry);
    const newCities = COUNTRIES_AND_CITIES[newCountry] || [];
    if (newCities.length > 0) {
      setCity(newCities[0]);
    }
  };

  const handleRandomizeName = () => {
    const { firstName: f, lastName: l } = getRandomName(gender);
    setFirstName(f);
    setLastName(l);
  };

  const handleRollNaturalDistribution = () => {
    const rand = (min: number, max: number) =>
      Math.floor(Math.random() * (max - min + 1)) + min;

    setIntelligence(rand(45, 92));
    setDiscipline(rand(40, 88));
    setWillpower(rand(42, 90));
    setAmbition(rand(45, 95));
    setHealth(rand(70, 98));
    setLooks(rand(40, 92));
    setSmarts(rand(45, 92));
    setHappiness(rand(50, 90));
    setFertility(rand(75, 98));
    setEnergy(rand(85, 100));
    setAthleticPerformance(rand(40, 90));
  };

  const handleSubmit = async () => {
    setIsSubmitting(true);
    setErrorMsg(null);

    const fullName = `${firstName.trim()} ${lastName.trim()}`.trim();
    if (!fullName) {
      setErrorMsg('Please provide a valid character name.');
      setIsSubmitting(false);
      return;
    }

    try {
      const res = await fetch('/api/lives', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username: `Player_${firstName}`,
          slotName: `Slot ${fullName}`,
          characterName: fullName,
          birthYear: 2000,
          birthCountry: country,
          birthCity: city,
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
        }),
      });

      if (!res.ok) {
        const data = await res.json().catch(() => ({}));
        throw new Error(data.error || 'Failed to initialize character.');
      }

      const summary: LifeSummaryResponse = await res.json();
      onCharacterCreated(summary);
    } catch (err: any) {
      setErrorMsg(err.message || 'Error occurred while saving character.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="bg-[#111723] border border-[#26354D] rounded-2xl p-6 sm:p-8 space-y-8 shadow-xl max-w-3xl mx-auto">
      {/* Header & Step Indicator */}
      <div className="space-y-3 border-b border-[#26354D] pb-5">
        <div className="flex items-center justify-between">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#0C3854]/60 border border-[#38BDF8]/30">
            <span className="text-xs font-semibold tracking-wider text-[#BAE6FD] uppercase">
              Character Creator • Step {step} of 7
            </span>
          </div>
          {onCancel && (
            <button
              onClick={onCancel}
              className="text-xs text-slate-400 hover:text-white transition-colors"
            >
              Cancel
            </button>
          )}
        </div>

        {/* Step Progress Bar */}
        <div className="w-full bg-[#1E293B] h-1.5 rounded-full overflow-hidden">
          <div
            className="bg-[#38BDF8] h-full transition-all duration-300"
            style={{ width: `${(step / 7) * 100}%` }}
          />
        </div>
      </div>

      {/* Step 1: Country */}
      {step === 1 && (
        <div className="space-y-5">
          <div>
            <h2 className="text-xl font-bold text-white">Select Birth Country</h2>
            <p className="text-xs text-slate-400">
              The national foundation sets regional economic and geopolitical baselines.
            </p>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
            {COUNTRIES.map((c) => (
              <button
                key={c}
                type="button"
                onClick={() => handleCountryChange(c)}
                className={`p-3.5 rounded-xl border text-left text-sm font-medium transition-all ${
                  country === c
                    ? 'border-[#38BDF8] bg-[#38BDF8]/10 text-white shadow-sm'
                    : 'border-[#26354D] bg-[#0E1420] text-slate-300 hover:border-slate-500'
                }`}
              >
                {c}
              </button>
            ))}
          </div>
        </div>
      )}

      {/* Step 2: City */}
      {step === 2 && (
        <div className="space-y-5">
          <div>
            <h2 className="text-xl font-bold text-white">Select Birth City</h2>
            <p className="text-xs text-slate-400">
              Cities within {country}. Determines local district environments and opportunities.
            </p>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
            {availableCities.map((cty) => (
              <button
                key={cty}
                type="button"
                onClick={() => setCity(cty)}
                className={`p-3.5 rounded-xl border text-left text-sm font-medium transition-all ${
                  city === cty
                    ? 'border-[#38BDF8] bg-[#38BDF8]/10 text-white shadow-sm'
                    : 'border-[#26354D] bg-[#0E1420] text-slate-300 hover:border-slate-500'
                }`}
              >
                {cty}
              </button>
            ))}
          </div>
        </div>
      )}

      {/* Step 3: Name */}
      {step === 3 && (
        <div className="space-y-5">
          <div>
            <h2 className="text-xl font-bold text-white">Identity & Name</h2>
            <p className="text-xs text-slate-400">
              Set the legal identity for your simulated subject.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div className="space-y-1.5">
              <label className="text-xs font-semibold text-slate-300 uppercase tracking-wider">
                First Name
              </label>
              <input
                type="text"
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
                className="w-full bg-[#0E1420] border border-[#26354D] rounded-xl px-4 py-3 text-white text-sm focus:outline-none focus:border-[#38BDF8]"
              />
            </div>
            <div className="space-y-1.5">
              <label className="text-xs font-semibold text-slate-300 uppercase tracking-wider">
                Last Name
              </label>
              <input
                type="text"
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
                className="w-full bg-[#0E1420] border border-[#26354D] rounded-xl px-4 py-3 text-white text-sm focus:outline-none focus:border-[#38BDF8]"
              />
            </div>
          </div>

          <button
            type="button"
            onClick={handleRandomizeName}
            className="px-4 py-2 rounded-xl bg-[#1E293B] border border-[#334155] text-xs font-medium text-slate-200 hover:bg-[#283548] hover:text-white transition-all flex items-center gap-2"
          >
            🎲 Randomize Name
          </button>
        </div>
      )}

      {/* Step 4: Gender & Sexuality */}
      {step === 4 && (
        <div className="space-y-6">
          <div>
            <h2 className="text-xl font-bold text-white">Gender & Sexuality</h2>
            <p className="text-xs text-slate-400">
              Biological and orientation parameters for the life simulation.
            </p>
          </div>

          <div className="space-y-3">
            <label className="text-xs font-semibold text-slate-300 uppercase tracking-wider">
              Gender
            </label>
            <div className="grid grid-cols-3 gap-3">
              {GENDERS.map((g) => (
                <button
                  key={g}
                  type="button"
                  onClick={() => setGender(g)}
                  className={`p-3 rounded-xl border text-center text-sm font-medium transition-all ${
                    gender === g
                      ? 'border-[#38BDF8] bg-[#38BDF8]/10 text-white'
                      : 'border-[#26354D] bg-[#0E1420] text-slate-300 hover:border-slate-500'
                  }`}
                >
                  {g}
                </button>
              ))}
            </div>
          </div>

          <div className="space-y-3">
            <label className="text-xs font-semibold text-slate-300 uppercase tracking-wider">
              Sexuality
            </label>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
              {SEXUALITIES.map((s) => (
                <button
                  key={s}
                  type="button"
                  onClick={() => setSexuality(s)}
                  className={`p-3 rounded-xl border text-center text-sm font-medium transition-all ${
                    sexuality === s
                      ? 'border-[#38BDF8] bg-[#38BDF8]/10 text-white'
                      : 'border-[#26354D] bg-[#0E1420] text-slate-300 hover:border-slate-500'
                  }`}
                >
                  {s}
                </button>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Step 5: Special Talent */}
      {step === 5 && (
        <div className="space-y-5">
          <div>
            <h2 className="text-xl font-bold text-white">Special Talent</h2>
            <p className="text-xs text-slate-400">
              Innate natural disposition that provides kinetic and cognitive bonuses in specific life endeavors.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            {TALENTS.map((t) => (
              <button
                key={t.id}
                type="button"
                onClick={() => setTalent(t.id as TalentType)}
                className={`p-4 rounded-xl border text-left transition-all ${
                  talent === t.id
                    ? 'border-[#38BDF8] bg-[#38BDF8]/10 shadow-sm'
                    : 'border-[#26354D] bg-[#0E1420] hover:border-slate-500'
                }`}
              >
                <div className="flex items-center justify-between mb-1">
                  <span className="font-semibold text-sm text-white">{t.label}</span>
                  {talent === t.id && (
                    <span className="w-2 h-2 rounded-full bg-[#38BDF8]" />
                  )}
                </div>
                <p className="text-xs text-slate-400 leading-relaxed">{t.desc}</p>
              </button>
            ))}
          </div>
        </div>
      )}

      {/* Step 6: Appearance (with Live Visual Preview) */}
      {step === 6 && (
        <div className="space-y-6">
          <div>
            <h2 className="text-xl font-bold text-white">Appearance Customization</h2>
            <p className="text-xs text-slate-400">
              Real-time procedural visual generation. All facial features render dynamically.
            </p>
          </div>

          {/* Live Preview & Controls Layout */}
          <div className="grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
            {/* Live Avatar Preview */}
            <div className="md:col-span-5 flex flex-col items-center justify-center p-5 bg-[#0E1420] border border-[#26354D] rounded-2xl space-y-3">
              <CharacterAvatar
                skinTone={skinTone}
                eyeStyle={eyeStyle}
                eyeColor={eyeColor}
                browStyle={browStyle}
                hairStyle={hairStyle}
                hairColor={hairColor}
                facialHairStyle={facialHairStyle}
                facialHairColor={facialHairColor}
                size={180}
              />
              <span className="text-xs font-mono text-[#38BDF8] tracking-wider uppercase">
                {skinTone} • {hairStyle} ({hairColor})
              </span>
            </div>

            {/* Controls */}
            <div className="md:col-span-7 space-y-4 max-h-[380px] overflow-y-auto pr-1">
              {/* Skin Tone */}
              <div className="space-y-1.5">
                <label className="text-xs font-semibold text-slate-300">Skin Tone</label>
                <div className="flex flex-wrap gap-2">
                  {SKIN_TONES.map((s) => (
                    <button
                      key={s.name}
                      type="button"
                      onClick={() => setSkinTone(s.name)}
                      className={`px-3 py-1.5 rounded-lg border text-xs font-medium flex items-center gap-1.5 transition-all ${
                        skinTone === s.name
                          ? 'border-[#38BDF8] bg-[#38BDF8]/15 text-white'
                          : 'border-[#26354D] bg-[#0E1420] text-slate-300'
                      }`}
                    >
                      <span
                        className="w-3 h-3 rounded-full border border-black/30"
                        style={{ backgroundColor: s.hex }}
                      />
                      {s.name}
                    </button>
                  ))}
                </div>
              </div>

              {/* Eyes Style & Color */}
              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-slate-300">Eye Style</label>
                  <select
                    value={eyeStyle}
                    onChange={(e) => setEyeStyle(e.target.value)}
                    className="w-full bg-[#0E1420] border border-[#26354D] rounded-xl px-3 py-2 text-xs text-white"
                  >
                    {EYE_STYLES.map((st) => (
                      <option key={st} value={st}>
                        {st}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-slate-300">Eye Color</label>
                  <select
                    value={eyeColor}
                    onChange={(e) => setEyeColor(e.target.value)}
                    className="w-full bg-[#0E1420] border border-[#26354D] rounded-xl px-3 py-2 text-xs text-white"
                  >
                    {EYE_COLORS.map((ec) => (
                      <option key={ec.name} value={ec.name}>
                        {ec.name}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              {/* Eyebrows */}
              <div className="space-y-1.5">
                <label className="text-xs font-semibold text-slate-300">Brow Style</label>
                <select
                  value={browStyle}
                  onChange={(e) => setBrowStyle(e.target.value)}
                  className="w-full bg-[#0E1420] border border-[#26354D] rounded-xl px-3 py-2 text-xs text-white"
                >
                  {BROW_STYLES.map((bs) => (
                    <option key={bs} value={bs}>
                      {bs}
                    </option>
                  ))}
                </select>
              </div>

              {/* Hair Style & Color */}
              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-slate-300">Hair Style</label>
                  <select
                    value={hairStyle}
                    onChange={(e) => setHairStyle(e.target.value)}
                    className="w-full bg-[#0E1420] border border-[#26354D] rounded-xl px-3 py-2 text-xs text-white"
                  >
                    {HAIR_STYLES.map((hs) => (
                      <option key={hs} value={hs}>
                        {hs}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-slate-300">Hair Color</label>
                  <select
                    value={hairColor}
                    onChange={(e) => setHairColor(e.target.value)}
                    className="w-full bg-[#0E1420] border border-[#26354D] rounded-xl px-3 py-2 text-xs text-white"
                  >
                    {HAIR_COLORS.map((hc) => (
                      <option key={hc.name} value={hc.name}>
                        {hc.name}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              {/* Facial Hair Style & Color */}
              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-slate-300">Facial Hair</label>
                  <select
                    value={facialHairStyle}
                    onChange={(e) => setFacialHairStyle(e.target.value)}
                    className="w-full bg-[#0E1420] border border-[#26354D] rounded-xl px-3 py-2 text-xs text-white"
                  >
                    {FACIAL_HAIR_STYLES.map((fhs) => (
                      <option key={fhs} value={fhs}>
                        {fhs}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-slate-300">Beard Color</label>
                  <select
                    value={facialHairColor}
                    onChange={(e) => setFacialHairColor(e.target.value)}
                    className="w-full bg-[#0E1420] border border-[#26354D] rounded-xl px-3 py-2 text-xs text-white"
                  >
                    {HAIR_COLORS.map((hc) => (
                      <option key={hc.name} value={hc.name}>
                        {hc.name}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Step 7: Attributes */}
      {step === 7 && (
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-xl font-bold text-white">Starting Attributes</h2>
              <p className="text-xs text-slate-400">
                Core psychological and biological baselines (0-100).
              </p>
            </div>
            <button
              type="button"
              onClick={handleRollNaturalDistribution}
              className="px-3.5 py-1.5 rounded-xl bg-[#1E293B] border border-[#334155] text-xs font-medium text-slate-200 hover:bg-[#283548] hover:text-white transition-all flex items-center gap-1.5"
            >
              🎲 Roll Natural Distribution
            </button>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {[
              { label: 'Intelligence', val: intelligence, set: setIntelligence },
              { label: 'Discipline', val: discipline, set: setDiscipline },
              { label: 'Willpower', val: willpower, set: setWillpower },
              { label: 'Ambition', val: ambition, set: setAmbition },
              { label: 'Health', val: health, set: setHealth },
              { label: 'Looks', val: looks, set: setLooks },
              { label: 'Smarts', val: smarts, set: setSmarts },
              { label: 'Happiness', val: happiness, set: setHappiness },
              { label: 'Fertility', val: fertility, set: setFertility },
              { label: 'Energy', val: energy, set: setEnergy },
              { label: 'Athletic Performance', val: athleticPerformance, set: setAthleticPerformance },
            ].map(({ label, val, set }) => (
              <div
                key={label}
                className="bg-[#0E1420] border border-[#26354D] rounded-xl p-3 space-y-1.5"
              >
                <div className="flex justify-between text-xs">
                  <span className="font-semibold text-slate-300">{label}</span>
                  <span className="font-mono text-[#38BDF8] font-bold">{val}</span>
                </div>
                <input
                  type="range"
                  min="1"
                  max="100"
                  value={val}
                  onChange={(e) => set(Number(e.target.value))}
                  className="w-full h-1.5 bg-[#1E293B] rounded-lg appearance-none cursor-pointer accent-[#38BDF8]"
                />
              </div>
            ))}
          </div>

          <div className="p-3 rounded-xl bg-slate-900/60 border border-slate-700/50 text-xs text-slate-400">
            🔒 <strong className="text-slate-300">Privacy Guarantee:</strong> Internal destiny and morality karma metrics are managed strictly by backend simulation engines and are omitted from user inspection.
          </div>
        </div>
      )}

      {/* Error Message */}
      {errorMsg && (
        <div className="p-3.5 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-300 text-xs">
          {errorMsg}
        </div>
      )}

      {/* Navigation Controls */}
      <div className="flex items-center justify-between pt-4 border-t border-[#26354D]">
        <button
          type="button"
          disabled={step === 1 || isSubmitting}
          onClick={() => setStep((s) => Math.max(1, s - 1))}
          className={`px-5 py-2.5 rounded-xl text-xs font-semibold uppercase tracking-wider transition-all ${
            step === 1 || isSubmitting
              ? 'opacity-40 cursor-not-allowed text-slate-500'
              : 'bg-[#1E293B] text-slate-200 hover:bg-[#283548]'
          }`}
        >
          Previous
        </button>

        {step < 7 ? (
          <button
            type="button"
            onClick={() => setStep((s) => Math.min(7, s + 1))}
            className="px-6 py-2.5 rounded-xl bg-[#38BDF8] text-[#090D14] text-xs font-bold uppercase tracking-wider hover:bg-[#7DD3FC] transition-all"
          >
            Next Step
          </button>
        ) : (
          <button
            type="button"
            disabled={isSubmitting}
            onClick={handleSubmit}
            className={`px-7 py-2.5 rounded-xl bg-gradient-to-r from-[#10B981] to-[#059669] text-white text-xs font-bold uppercase tracking-wider shadow-lg hover:brightness-110 transition-all ${
              isSubmitting ? 'opacity-50 cursor-wait' : ''
            }`}
          >
            {isSubmitting ? 'Creating Life...' : 'Initialize Life'}
          </button>
        )}
      </div>
    </div>
  );
};
