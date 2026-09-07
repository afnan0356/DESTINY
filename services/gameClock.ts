/**
 * GAME CLOCK ARCHITECTURE (Prompt 01 of 20: FOUNDATION)
 *
 * Primary temporal engine for Destiny.
 *
 * ARCHITECTURAL MANDATE:
 * Defaults to YEARLY ticks for macro life progression, but is structurally
 * designed to accept variable resolution (MONTHLY, WEEKLY, DAILY) for active events
 * (courtroom trials, business turnarounds, medical crises, military tours)
 * coming in Prompts 02-20.
 */

import { ClockState, TickResolution } from '../types';

export const RESOLUTION_SUBTICKS: Record<TickResolution, number> = {
  YEARLY: 1,
  MONTHLY: 12,
  WEEKLY: 52,
  DAILY: 365,
};

export class GameClockService {
  private state: ClockState;

  constructor(initialYear = 2000, initialResolution: TickResolution = 'YEARLY') {
    this.state = {
      year: initialYear,
      subTick: 1,
      resolution: initialResolution,
      isZoomedIn: initialResolution !== 'YEARLY',
      totalTicksElapsed: 0,
    };
  }

  public getState(): ClockState {
    return { ...this.state };
  }

  /**
   * Advance simulation time by [steps] in current resolution.
   */
  public tick(steps = 1): ClockState {
    if (steps <= 0) throw new Error('Tick steps must be greater than zero.');

    let { year, subTick, resolution, totalTicksElapsed } = this.state;
    const maxSubTicks = RESOLUTION_SUBTICKS[resolution];

    for (let i = 0; i < steps; i++) {
      totalTicksElapsed++;
      if (resolution === 'YEARLY') {
        year++;
      } else {
        subTick++;
        if (subTick > maxSubTicks) {
          subTick = 1;
          year++;
        }
      }
    }

    this.state = {
      ...this.state,
      year,
      subTick,
      totalTicksElapsed,
    };

    return this.getState();
  }

  /**
   * Enters higher-resolution temporal zoom for active interactive events.
   * Architectural hook for subsequent prompts.
   */
  public enterEventZoom(resolution: Exclude<TickResolution, 'YEARLY'>): ClockState {
    this.state = {
      ...this.state,
      resolution,
      subTick: 1,
      isZoomedIn: true,
    };
    return this.getState();
  }

  /**
   * Exits zoom and returns to standard yearly macro-simulation.
   */
  public exitEventZoom(): ClockState {
    this.state = {
      ...this.state,
      resolution: 'YEARLY',
      subTick: 1,
      isZoomedIn: false,
    };
    return this.getState();
  }

  public formatDisplay(): string {
    const { year, subTick, resolution } = this.state;
    switch (resolution) {
      case 'YEARLY':
        return `Year ${year}`;
      case 'MONTHLY':
        return `Year ${year} • Month ${subTick}/12`;
      case 'WEEKLY':
        return `Year ${year} • Week ${subTick}/52`;
      case 'DAILY':
        return `Year ${year} • Day ${subTick}/365`;
    }
  }
}
