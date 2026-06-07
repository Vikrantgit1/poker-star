import { render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { GameTable } from './GameTable';
import type { GameState } from '../types';

const game: GameState = {
  gameId: 'game-1',
  players: [
    {
      playerId: 'p1',
      name: 'Ada',
      chips: 980,
      hand: [
        { rank: 'A', suit: 'SPADES' },
        { rank: 'K', suit: 'HEARTS' },
      ],
      folded: false,
      currentRoundBet: 20,
      actedThisRound: true,
    },
    {
      playerId: 'p2',
      name: 'Grace',
      chips: 1000,
      hand: null,
      folded: false,
      currentRoundBet: 0,
      actedThisRound: false,
    },
  ],
  communityCards: [{ rank: '10', suit: 'CLUBS' }],
  pot: 40,
  status: 'FLOP',
  currentPlayerId: 'p2',
  currentBet: 20,
  minRaise: 20,
  legalActions: ['CALL', 'RAISE', 'FOLD'],
  winnerPlayerIds: [],
  winnerNames: [],
  winningHandRank: null,
};

describe('GameTable', () => {
  it('renders table state, players, and visible cards', () => {
    render(<GameTable game={game} activePlayerId="p1" onSelectPlayer={vi.fn()} />);

    expect(screen.getByText('Flop')).toBeInTheDocument();
    expect(screen.getByText('Ada')).toBeInTheDocument();
    expect(screen.getByText('Grace')).toBeInTheDocument();
    expect(screen.getByLabelText('A of SPADES')).toBeInTheDocument();
    expect(screen.getByLabelText('10 of CLUBS')).toBeInTheDocument();
    expect(screen.getByText('40')).toBeInTheDocument();
  });

  it('renders an empty table message without game state', () => {
    render(<GameTable game={null} activePlayerId="" onSelectPlayer={vi.fn()} />);

    expect(screen.getByText('No active table')).toBeInTheDocument();
  });
});
