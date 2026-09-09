package com.example.services

/**
 * GAME CLOCK ARCHITECTURE (Prompt 01 of 20 Foundation)
 *
 * Core timekeeper for the Destiny simulation.
 * Defaults to YEARLY resolution, but is specifically architected to support
 * variable resolution (MONTHLY, WEEKLY, DAILY) for zoom-in active events
 * (e.g., military deployment, political campaign, courtroom trials, surgical emergencies)
 * in subsequent prompts.
 */
class GameClock(
    initialYear: Int = 2000,
    initialResolution: TickResolution = TickResolution.YEARLY
) {
    enum class TickResolution(val subTicksPerYear: Int, val label: String) {
        YEARLY(1, "Annual"),
        MONTHLY(12, "Monthly"),
        WEEKLY(52, "Weekly"),
        DAILY(365, "Daily")
    }

    data class ClockState(
        val year: Int,
        val subTick: Int, // 1-indexed relative to resolution (e.g., Month 1..12, Week 1..52)
        val resolution: TickResolution,
        val isZoomedIn: Boolean = false,
        val totalTicksElapsed: Long = 0L
    )

    private var state: ClockState = ClockState(
        year = initialYear,
        subTick = 1,
        resolution = initialResolution,
        isZoomedIn = initialResolution != TickResolution.YEARLY
    )

    fun getCurrentState(): ClockState = state

    /**
     * Advance the clock by [steps] of current resolution.
     */
    fun advanceTick(steps: Int = 1): ClockState {
        require(steps > 0) { "Steps must be positive" }
        var currentYear = state.year
        var currentSubTick = state.subTick
        val maxSubTicks = state.resolution.subTicksPerYear
        var elapsed = state.totalTicksElapsed

        repeat(steps) {
            elapsed++
            if (state.resolution == TickResolution.YEARLY) {
                currentYear++
            } else {
                currentSubTick++
                if (currentSubTick > maxSubTicks) {
                    currentSubTick = 1
                    currentYear++
                }
            }
        }

        state = state.copy(
            year = currentYear,
            subTick = currentSubTick,
            totalTicksElapsed = elapsed
        )
        return state
    }

    /**
     * Zoom into higher resolution (e.g., monthly/weekly) during an active encounter.
     * Architectural hook for future prompts (Prompts 02-20).
     */
    fun enterEventZoom(temporaryResolution: TickResolution) {
        state = state.copy(
            resolution = temporaryResolution,
            subTick = 1,
            isZoomedIn = true
        )
    }

    /**
     * Revert to standard yearly simulation once an active event completes.
     */
    fun exitEventZoom() {
        state = state.copy(
            resolution = TickResolution.YEARLY,
            subTick = 1,
            isZoomedIn = false
        )
    }

    fun formatDisplay(): String {
        return when (state.resolution) {
            TickResolution.YEARLY -> "Year ${state.year}"
            TickResolution.MONTHLY -> "Year ${state.year} • Month ${state.subTick}/12"
            TickResolution.WEEKLY -> "Year ${state.year} • Week ${state.subTick}/52"
            TickResolution.DAILY -> "Year ${state.year} • Day ${state.subTick}/365"
        }
    }
}
