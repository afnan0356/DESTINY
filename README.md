# DESTINY

An ultra-deep life-simulation engine where every NPC lives an autonomous life, the world simulates its own economy, politics, and dynasties, and player decisions echo for decades. Built with a local-first, zero-backend architecture to run with zero operational cost forever.

---

## Strategic Pivot: Zero Backend & Local-Only Architecture

Destiny operates under a **zero-backend cost model**. All simulation state, NPC records, and lineage data live exclusively on the user's Android device:
- **Primary Data Owner**: Android client running Room SQLite as the single source of truth.
- **Data Portability**: Built-in JSON Save Export and Import allows manual backup, export, and migration (to Google Drive, email, or local files) without any server dependency.
- **Web Client**: Reduced to a clean, static landing page with game information and a direct link to GitHub Releases for APK sideloading.
- **No Third-Party Cloud Services**: Firebase, Firestore, external relational databases, and proprietary backend services have been purged from both platforms.

---

## Security Advisory: Key Rotation Notice

> ⚠️ **IMPORTANT SECURITY NOTICE**:
> During initial setup in earlier development passes, API credentials and configurations for a Firebase test project (`thedestiny` / `AIzaSy...`) were committed to configuration files. 
> 
> All Firebase SDKs, dependencies, and environment references have been completely purged from this repository, and `.env*` files are strictly excluded via `.gitignore`. If you previously deployed this repository, **ensure any past Firebase API keys are immediately rotated or revoked** in your Google Cloud / Firebase console.

---

## Build Progress

- ✅ **Prompt 01 — Foundation:** User → SaveSlot → Life → Character hierarchy, dormant/active NPC model, 5-layer FormulaEngine (Character Foundation / Momentum / Influence / World Variables / Luck), variable-resolution GameClock, karma privacy enforcement.
- ✅ **Prompt 02 — Character System:** Full character creation flow, appearance system, attributes, genetics stub fields, aging service, death check service.
- ✅ **Prompt 03 — Family System:** Relationships (friend tiers, enemies with sabotage chance), marriage/divorce with dormant-to-active NPC promotion, children with real genetic inheritance, inheritance on death, family event log.
- ✅ **Prompt 04 — Infrastructure Cleanup & Pivot:** Complete removal of Firebase & Prisma, purge of leaked secrets, implementation of Room local-only storage model with JSON Save Export/Import, resolution of root directory `/app` conflict by migrating Android to `/android/app`, static Next.js landing page.
- ⬜ Prompt 05 — Education System
- ⬜ Prompt 06 — Career System
- ⬜ Prompts 07–20 — Business, Economy, Politics, Military, Crime, Sports, Fame & Media, Healthcare, World Simulation, UI/UX, Testing & Balancing.

---

## Project Structure

```
├── android/
│   └── app/                 # Android Jetpack Compose application
│       ├── build.gradle.kts # Android module Gradle configuration
│       └── src/             # Kotlin source code, Room entities, DAOs, UI
├── gradle/
│   └── libs.versions.toml   # Version Catalog (Room, Compose, etc.)
├── src/
│   └── app/                 # Next.js App Router (Static landing page)
│       ├── page.tsx         # Destiny info & APK download link
│       └── layout.tsx       # Root layout & metadata
├── settings.gradle.kts      # Gradle settings (includes :android:app)
├── build.gradle.kts         # Root Gradle build file
└── package.json             # Next.js dependencies (zero backend SDKs)
```

---

## Tech Stack

- **Android (Primary Platform):** Kotlin, Jetpack Compose, Room Database (KSP), Material 3.
- **Web (Landing Page):** Next.js 15 (App Router, Static Export), React 19, Tailwind CSS, TypeScript.
- **Persistence:** Local Room SQLite on Android; Portable single-file JSON backup.

---

## Running Locally

### Web Landing Page
```bash
npm install
npm run build
npm run start
```
Open [http://localhost:3000](http://localhost:3000) to view the static landing page.

### Android Application
```bash
# Build debug APK
gradle :android:app:assembleDebug

# Run local unit & Robolectric tests
gradle :android:app:testDebugUnitTest
```
The resulting APK is located at `android/app/build/outputs/apk/debug/app-debug.apk`.

---

## Core Principles

- **5-Layer Formula Engine:** `Outcome = Character Foundation + Momentum + Influence + World Variables + Luck`
- **Dual NPC Model:** NPCs are dormant (lightweight, for population-scale performance) until promoted to active (full simulation for direct player relationships).
- **Variable GameClock:** Advances yearly by default, automatically zooming into monthly/weekly/hourly resolution during critical life crises and events.
- **Local Sovereignty:** Zero tracking, zero analytics, zero external servers.
