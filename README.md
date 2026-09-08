# DESTINY

An ultra-deep life-simulation game where every NPC lives an independent life, the world simulates its own economy, politics, crime, and war on its own, and player decisions can echo for decades. Built across Next.js (web) and Kotlin/Jetpack Compose (Android, Play Store-ready), sharing one architecture across both platforms.

## Build Progress

- ✅ **Prompt 01 — Foundation:** User → SaveSlot → Life → Character hierarchy, dormant/active NPC model, 5-layer FormulaEngine (Character Foundation / Momentum / Influence / World Variables / Luck), variable-resolution GameClock, karma privacy enforcement.
- ✅ **Prompt 02 — Character System:** Full character creation flow, appearance system, attributes, genetics stub fields, aging service, death check service.
- ✅ **Prompt 03 — Family System:** Relationships (friend tiers, enemies with sabotage chance), marriage/divorce with dormant-to-active NPC promotion, children with real genetic inheritance, inheritance on death, family event log.
- ⬜ Prompt 04 — Education System
- ⬜ Prompt 05 — Career System
- ⬜ Prompts 06–20 — Business, Economy, Politics, Military, Crime, Sports, Fame & Media, Healthcare, World Simulation, UI/UX, Save & Progression, Testing & Balancing

## Tech Stack

**Web:** Next.js 15 (App Router), TypeScript, React 19, Tailwind CSS, PostgreSQL + Prisma ORM.
**Android:** Kotlin, Jetpack Compose, Room Database (KSP), Material 3.

Both clients share the same architecture (FormulaEngine, GameClock, data model) and are kept in sync prompt-by-prompt — every system built for one platform is built for both in the same pass.

## Design Philosophy

Destiny treats life as several interlocking simulators — not just a career-and-family game, but a full life/country/business/political/crime/sports/economy/dynasty simulation running in one persistent world. Full design details live in the project's Master Game Design Document (not included in this repo — maintained separately).

Core principles:
- Every major system's outcomes run through one shared formula: `Outcome = Character Foundation + Momentum + Influence + World Variables + Luck`
- NPCs are either dormant (lightweight, for population-scale performance) or active (fully simulated, for anyone the player actually interacts with)
- Player decisions can resurface years or decades later — momentum, influence, and consequences persist and compound over time
- Time moves yearly by default, automatically zooming in to monthly/weekly resolution during major events (elections, wars, trials, contract negotiations), then returning to yearly once resolved

## Running Locally

**Web:**
