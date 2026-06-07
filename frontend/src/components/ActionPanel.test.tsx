import { render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { ActionPanel } from './ActionPanel';
import type { GameState } from '../types';

const game: GameState = {
  gameId: 'game-1',
  players: [],
  communityCards: [],
  pot: 0,
  status: 'PRE_FLOP',
  currentPlayerId: 'p1',
  currentBet: 10,
  minRaise: 10,
  legalActions: ['CALL', 'RAISE', 'FOLD'],
  winnerPlayerIds: [],
  winnerNames: [],
  winningHandRank: null,
};

describe('ActionPanel', () => {
  it('enables only legal actions for the active player', () => {
    render(
      <ActionPanel
        game={game}
        activePlayerId="p1"
        actionAmount={10}
        isBusy={false}
        onAmountChange={vi.fn()}
        onDeal={vi.fn()}
        onReveal={vi.fn()}
        onAction={vi.fn()}
        onRefresh={vi.fn()}
      />,
    );

    expect(screen.getByRole('button', { name: /check/i })).toBeDisabled();
    expect(screen.getByRole('button', { name: /call/i })).toBeEnabled();
    expect(screen.getByRole('button', { name: /raise/i })).toBeEnabled();
    expect(screen.getByRole('button', { name: /fold/i })).toBeEnabled();
  });

  it('disables actions when a different player is selected', () => {
    render(
      <ActionPanel
        game={game}
        activePlayerId="p2"
        actionAmount={10}
        isBusy={false}
        onAmountChange={vi.fn()}
        onDeal={vi.fn()}
        onReveal={vi.fn()}
        onAction={vi.fn()}
        onRefresh={vi.fn()}
      />,
    );

    expect(screen.getByRole('button', { name: /call/i })).toBeDisabled();
    expect(screen.getByRole('button', { name: /fold/i })).toBeDisabled();
  });
});
