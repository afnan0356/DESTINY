# PROJECT: DESTINY — Master Build Prompt 01 of 20: FOUNDATION

Destiny is an ultra-deep life-simulation game where every NPC lives an independent life, the world simulates its own economy/politics/crime/war, and player decisions echo for decades.

This repository implements **Prompt 01 of 20 (Technical Foundation)**. It establishes the core infrastructure, modular database schema, shared formula engine, variable-resolution game clock, and atomic persistence pipeline.

---

## 1. Architectural Highlights

### A. Modular Save Architecture (Unlimited Saves)
- **Hierarchy:** `User` (1) ➔ `SaveSlot` (Many) ➔ `Life` (Many) ➔ `Character` (1).
- Supports infinite player save slots and branching family lineages.
- Designed to allow resuming any life at any saved age without single-save constraints.

### B. NPC Simulation Model: Dormant vs. Active States
- **Dormant NPCs (`isDormant = true`):**
  Lightweight state. Intrinsic attributes and stats persist in the database, but annual tick simulation loops, relationship calculus, and career advancements are bypassed. This prevents CPU bottlenecks and memory bloat across tens of thousands of simulated world citizens.
- **Active NPCs (`isDormant = false`):**
  Full simulation state. NPCs in the player's immediate relational or professional sphere undergo full formula checks, event cascades, and momentum tracking.

### C. 5-Layer Shared Formula Engine (`lib/formulaEngine.ts` / `services/FormulaEngine.kt`)
Every future game system (Prompts 02–20: Character traits, Family, Career, Business, Politics, Military, Crime, Sports, etc.) routes outcome checks through this single engine:
1. **Character Foundation (35%):** Intelligence, discipline, willpower, ambition, health, looks, smarts, happiness.
2. **Momentum (20%):** Trajectory velocity, success/failure streaks, fatigue factor.
3. **Influence (15%):** Social capital, family wealth tier, faction reputation.
4. **World Variables (15%):** Macroeconomic index, political stability, unrest rating.
5. **Luck (15%):** Stochastic variance roll biased by hidden karma.

### D. Variable-Resolution GameClock (`services/gameClock.ts` / `services/GameClock.kt`)
- Defaults to **Yearly ticks** for macro life progression.
- Dynamically accepts variable zoom (**Monthly**, **Weekly**, **Daily**) for high-intensity active events (courtroom trials, military tours, medical emergencies, election campaigns) in future prompts.

### E. Karma Privacy Mandate
- **Internal Database Stat:** `karma` is stored in the database `characters` table for internal fate and luck calculations.
- **Client Response Encapsulation:** Stripped at the repository / service boundary via `ClientCharacter` (`Omit<Character, 'karma'>`). Raw karma is **strictly confidential** and never exposed to the client or UI.

---

## 2. Technology Stack & ORM Choice

- **Web Stack:** Next.js 15 (App Router) + TypeScript + React 19 + Tailwind CSS.
- **Database & ORM:** **PostgreSQL** paired with **Prisma ORM** (`prisma/schema.prisma`).
- **Android Mobile Client:** Kotlin + Jetpack Compose + AndroidX Room Database (KSP) + Material 3 Fintech Dark theme.

---

## 3. Project Directory Structure

```text
├── .env.example                # Documented environment variables (DATABASE_URL, etc.)
├── README.md                   # System architectural documentation
├── package.json                # Next.js and Prisma dependencies
├── tsconfig.json               # TypeScript configuration with path aliases
├── tailwind.config.js          # Fintech Dark theme definitions
├── prisma/
│   └── schema.prisma           # Modular PostgreSQL schema (User, SaveSlot, Life, Character)
├── lib/
│   ├── prisma.ts               # Global Prisma client singleton
│   └── formulaEngine.ts        # 5-Layer Shared Formula Engine
├── services/
│   ├── gameClock.ts            # Variable-resolution temporal simulation clock
│   └── lifeService.ts          # Atomic persistence & NPC state orchestrator
├── types/
│   └── index.ts                # Strict TypeScript contracts (including ClientCharacter)
├── src/
│   └── app/
│       ├── layout.tsx          # Next.js Root Layout with Fintech Dark defaults
│       ├── page.tsx            # Foundation Dashboard & New Life Flow verification
│       ├── globals.css         # Tailwind directives
│       └── api/
│           └── lives/
│               └── route.ts    # REST API for atomic User->SaveSlot->Life->Character write
│
└── app/                        # Android Client Module (Google AI Studio Streaming Emulator)
    ├── build.gradle.kts        # Android build configuration with Room & KSP
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   └── java/com/example/
        │       ├── MainActivity.kt
        │       ├── data/
        │       │   ├── local/
        │       │   │   ├── DestinyDatabase.kt
        │       │   │   ├── dao/DestinyDao.kt
        │       │   │   └── entity/ (UserEntity, SaveSlotEntity, LifeEntity, CharacterEntity)
        │       │   ├── model/ClientCharacter.kt (Karma-excluded client DTO)
        │       │   └── repository/DestinyRepository.kt
        │       ├── services/
        │       │   ├── FormulaEngine.kt
        │       │   ├── GameClock.kt
        │       │   └── LifeService.kt
        │       └── ui/
        │           ├── DestinyApp.kt
        │           ├── DestinyViewModel.kt
        │           ├── screens/ (DestinyLandingScreen, NewLifeFlowScreen, ArchitectureInspectorScreen)
        │           ├── components/LifeDetailDialog.kt
        │           └── theme/ (Color.kt, Theme.kt, Type.kt)
        └── test/               # Local JVM Robolectric tests verifying core foundation
```

---

## 4. How to Run Locally

### Next.js Web Stack
1. Clone repository:
   ```bash
   git clone <repo-url>
   cd destiny
   ```
2. Configure `.env`:
   ```bash
   cp .env.example .env
   # Add your PostgreSQL connection string to DATABASE_URL
   ```
3. Install dependencies and generate Prisma client:
   ```bash
   npm install
   npx prisma generate
   npx prisma migrate dev --name init
   ```
4. Start development server:
   ```bash
   npm run dev
   ```
5. Open `http://localhost:3000` to interact with the Landing Dashboard and New Life flow.

### Vercel Deployment
1. Connect repository to Vercel.
2. Under Project Settings ➔ Environment Variables, configure:
   - `DATABASE_URL`: Your hosted PostgreSQL connection string (Supabase, Neon, AWS RDS, or Vercel Postgres).
3. Deploy. Prisma generate is automatically called during `npm run build`.

### Android Application
1. In Android Studio or AI Studio Streaming Emulator:
   ```bash
   gradle :app:assembleDebug
   ```
2. Or run local JVM tests:
   ```bash
   gradle :app:testDebugUnitTest
   ```

---

## 5. What Is Intentionally NOT Built Yet

Per the Prompt 01 specification, this phase is strictly **infrastructure and architectural foundation**:
- **NO Character Creation Screens:** Detailed trait selection, childhood backgrounds, and appearance customizers are deferred to Prompt 02.
- **NO Career / Education Logic:** University progression, job ladders, and promotions are deferred to Prompts 04–05.
- **NO World NPC Population Generators:** Full population spawning and background economic cycles are deferred to subsequent prompts.
- **NO Active Event Zoom Triggers:** The GameClock supports monthly/weekly resolution, but event-driven automatic zoom triggers are deferred.

All future 19 prompts will dock directly into this foundation without requiring architectural refactoring.
