import React from 'react';

export default function DestinyLandingPage() {
  return (
    <div className="min-h-screen bg-[#070B12] text-[#E2E8F0] selection:bg-[#38BDF8] selection:text-[#021626]">
      {/* Navigation Header */}
      <header className="border-b border-[#1E293B]/80 bg-[#090E17]/80 backdrop-blur sticky top-0 z-50">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
          <div className="flex items-center space-x-3">
            <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-[#38BDF8] to-[#0284C7] flex items-center justify-center shadow-lg shadow-sky-950/40">
              <span className="text-[#070B12] font-black text-lg">D</span>
            </div>
            <div>
              <span className="font-black tracking-wider text-white text-lg">DESTINY</span>
              <span className="hidden sm:inline-block ml-2 text-[10px] font-mono tracking-widest text-[#38BDF8] uppercase bg-[#0C3854]/40 px-2 py-0.5 rounded border border-[#38BDF8]/30">
                Engine
              </span>
            </div>
          </div>

          <div className="flex items-center space-x-4">
            <a
              href="https://github.com/Destiny-Game/destiny/releases"
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center space-x-2 text-xs font-semibold px-4 py-2 rounded-lg bg-[#38BDF8] text-[#05131E] hover:bg-[#7DD3FC] transition-colors shadow-sm"
            >
              <span>Download APK</span>
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
              </svg>
            </a>
          </div>
        </div>
      </header>

      <main className="max-w-6xl mx-auto px-4 sm:px-6 py-12 sm:py-20 space-y-20">
        {/* Hero Section */}
        <section className="text-center space-y-6 max-w-3xl mx-auto">
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-[#0E1F33] border border-[#1E3A5F]">
            <span className="w-2 h-2 rounded-full bg-[#10B981] animate-pulse" />
            <span className="text-xs font-medium text-[#7DD3FC] tracking-wide">
              Zero-Backend Architecture • 100% Local Sovereignty
            </span>
          </div>

          <h1 className="text-4xl sm:text-6xl font-extrabold text-white tracking-tight leading-[1.1]">
            Every choice ripples. <br />
            <span className="bg-gradient-to-r from-[#38BDF8] via-[#60A5FA] to-[#A855F7] bg-clip-text text-transparent">
              Every life is sovereign.
            </span>
          </h1>

          <p className="text-base sm:text-lg text-slate-300 leading-relaxed max-w-2xl mx-auto">
            Destiny is an ultra-deep life simulation engine where every NPC lives an autonomous,
            formula-driven existence. Featuring generational lineages, variable-resolution game chronology,
            and complete offline local persistence.
          </p>

          <div className="flex flex-col sm:flex-row items-center justify-center gap-4 pt-4">
            <a
              href="https://github.com/Destiny-Game/destiny/releases"
              target="_blank"
              rel="noopener noreferrer"
              className="w-full sm:w-auto inline-flex items-center justify-center space-x-2 px-7 py-3.5 rounded-xl bg-[#38BDF8] text-[#070B12] font-bold text-sm hover:bg-[#7DD3FC] transition-all shadow-lg shadow-sky-900/30"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
              </svg>
              <span>Download Android APK (GitHub Releases)</span>
            </a>

            <a
              href="https://github.com/Destiny-Game/destiny"
              target="_blank"
              rel="noopener noreferrer"
              className="w-full sm:w-auto inline-flex items-center justify-center space-x-2 px-6 py-3.5 rounded-xl bg-[#0F172A] border border-[#334155] text-slate-200 font-semibold text-sm hover:bg-[#1E293B] transition-all"
            >
              <svg className="w-5 h-5 fill-current" viewBox="0 0 24 24">
                <path fillRule="evenodd" clipRule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.53 1.032 1.53 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z" />
              </svg>
              <span>GitHub Repository</span>
            </a>
          </div>

          {/* Key Metric Tags */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 pt-6">
            <div className="p-3 rounded-lg bg-[#0A101D] border border-[#1E293B]">
              <div className="text-lg font-bold text-[#38BDF8]">0ms</div>
              <div className="text-xs text-slate-400">Server Latency (Offline)</div>
            </div>
            <div className="p-3 rounded-lg bg-[#0A101D] border border-[#1E293B]">
              <div className="text-lg font-bold text-[#10B981]">$0.00</div>
              <div className="text-xs text-slate-400">Backend Cloud Cost</div>
            </div>
            <div className="p-3 rounded-lg bg-[#0A101D] border border-[#1E293B]">
              <div className="text-lg font-bold text-[#F59E0B]">5 Layers</div>
              <div className="text-xs text-slate-400">Deterministic Engine</div>
            </div>
            <div className="p-3 rounded-lg bg-[#0A101D] border border-[#1E293B]">
              <div className="text-lg font-bold text-[#A855F7]">Room SQLite</div>
              <div className="text-xs text-slate-400">On-Device Storage</div>
            </div>
          </div>
        </section>

        {/* Feature Grid */}
        <section className="space-y-8">
          <div className="text-center space-y-2">
            <h2 className="text-2xl sm:text-3xl font-bold text-white tracking-tight">
              Architectural Highlights
            </h2>
            <p className="text-sm text-slate-400">
              Built with rigorous engineering for uncompromising simulation depth.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Feature 1 */}
            <div className="p-6 rounded-2xl bg-[#090E17] border border-[#1E293B] space-y-3">
              <div className="w-10 h-10 rounded-xl bg-[#0C3854]/70 border border-[#38BDF8]/40 flex items-center justify-center text-[#38BDF8]">
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
                </svg>
              </div>
              <h3 className="text-lg font-bold text-white">5-Layer Mathematical Engine</h3>
              <p className="text-sm text-slate-300 leading-relaxed">
                Replaces arbitrary random rolls with multi-layered probability matrices.
                Combines internal traits, hidden Karma, Momentum trajectories, Social Influence,
                and Macroeconomic conditions.
              </p>
            </div>

            {/* Feature 2 */}
            <div className="p-6 rounded-2xl bg-[#090E17] border border-[#1E293B] space-y-3">
              <div className="w-10 h-10 rounded-xl bg-[#0F2D1F]/70 border border-[#10B981]/40 flex items-center justify-center text-[#10B981]">
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <h3 className="text-lg font-bold text-white">Variable GameClock Chronology</h3>
              <p className="text-sm text-slate-300 leading-relaxed">
                Seamless zoom capabilities from macro yearly life ticks down to monthly and
                hourly resolutions during critical event sequences such as court trials, medical emergencies,
                and corporate takeovers.
              </p>
            </div>

            {/* Feature 3 */}
            <div className="p-6 rounded-2xl bg-[#090E17] border border-[#1E293B] space-y-3">
              <div className="w-10 h-10 rounded-xl bg-[#2D1A3F]/70 border border-[#A855F7]/40 flex items-center justify-center text-[#A855F7]">
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
              </div>
              <h3 className="text-lg font-bold text-white">Living Dynasties & Lineages</h3>
              <p className="text-sm text-slate-300 leading-relaxed">
                Complex relationship networks with autonomous rivals, enemies with sabotage risks,
                arranged and spontaneous marriages, child genetics, and legally binding inheritance distribution.
              </p>
            </div>

            {/* Feature 4 */}
            <div className="p-6 rounded-2xl bg-[#090E17] border border-[#1E293B] space-y-3">
              <div className="w-10 h-10 rounded-xl bg-[#3B280E]/70 border border-[#F59E0B]/40 flex items-center justify-center text-[#F59E0B]">
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-3m-1 4l-3 3m0 0l-3-3m3 3V4" />
                </svg>
              </div>
              <h3 className="text-lg font-bold text-white">Portable Single-File Backup</h3>
              <p className="text-sm text-slate-300 leading-relaxed">
                Complete data ownership. Export your entire simulation universe into a clean, human-readable
                JSON file with a single tap. Restore across devices instantly without cloud accounts.
              </p>
            </div>
          </div>
        </section>

        {/* Installation & Release Instructions */}
        <section className="p-8 rounded-2xl bg-gradient-to-br from-[#0B1322] to-[#0E1B2E] border border-[#1E3A5F] space-y-6">
          <div className="space-y-2">
            <h2 className="text-xl sm:text-2xl font-bold text-white">
              Installation & Sideloading
            </h2>
            <p className="text-sm text-slate-300">
              Destiny is distributed directly via GitHub Releases as a standalone Android APK.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 text-sm">
            <div className="p-4 rounded-xl bg-[#070B12]/80 border border-[#1E293B] space-y-2">
              <div className="font-mono text-xs text-[#38BDF8] font-bold">STEP 01</div>
              <div className="font-semibold text-white">Download APK</div>
              <p className="text-xs text-slate-400">
                Visit the GitHub Releases section and download the latest <code className="text-[#7DD3FC]">destiny-release.apk</code>.
              </p>
            </div>

            <div className="p-4 rounded-xl bg-[#070B12]/80 border border-[#1E293B] space-y-2">
              <div className="font-mono text-xs text-[#38BDF8] font-bold">STEP 02</div>
              <div className="font-semibold text-white">Enable Install</div>
              <p className="text-xs text-slate-400">
                Allow &quot;Install from Unknown Sources&quot; in Android security settings for your browser or file manager.
              </p>
            </div>

            <div className="p-4 rounded-xl bg-[#070B12]/80 border border-[#1E293B] space-y-2">
              <div className="font-mono text-xs text-[#38BDF8] font-bold">STEP 03</div>
              <div className="font-semibold text-white">Play Offline</div>
              <p className="text-xs text-slate-400">
                Launch Destiny. No internet connection, account registration, or subscriptions required.
              </p>
            </div>
          </div>
        </section>
      </main>

      {/* Footer */}
      <footer className="border-t border-[#1E293B]/60 py-8 bg-[#070B12]">
        <div className="max-w-6xl mx-auto px-4 sm:px-6 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs text-slate-500">
          <div>
            Destiny Simulation Engine • Open Source • Zero Backend Dependencies
          </div>
          <div className="flex space-x-6">
            <a
              href="https://github.com/Destiny-Game/destiny"
              target="_blank"
              rel="noopener noreferrer"
              className="hover:text-slate-300 transition-colors"
            >
              GitHub Source
            </a>
            <a
              href="https://github.com/Destiny-Game/destiny/releases"
              target="_blank"
              rel="noopener noreferrer"
              className="hover:text-slate-300 transition-colors"
            >
              Releases
            </a>
          </div>
        </div>
      </footer>
    </div>
  );
}
