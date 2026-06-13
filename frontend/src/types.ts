export type GamePhase =
  | 'WAITING'
  | 'PRE_FLOP'
  | 'FLOP'
  | 'TURN'
  | 'RIVER'
  | 'SHOWDOWN'
  | 'FINISHED';

export type PlayerActionType = 'CHECK' | 'CALL' | 'BET' | 'RAISE' | 'FOLD';

export type HandRank =
  | 'HIGH_CARD'
  | 'ONE_PAIR'
  | 'TWO_PAIR'
  | 'THREE_OF_A_KIND'
  | 'STRAIGHT'
  | 'FLUSH'
  | 'FULL_HOUSE'
  | 'FOUR_OF_A_KIND'
  | 'STRAIGHT_FLUSH'
  | 'ROYAL_FLUSH';

export type Card = {
  rank: string;
  suit: string;
};

export type Player = {
  playerId: string;
  name: string;
  chips: number;
  hand: Card[] | null;
  folded: boolean;
  currentRoundBet: number;
  actedThisRound: boolean;
};

export type GameState = {
  gameId: string;
  players: Player[];
  communityCards: Card[];
  pot: number;
  status: GamePhase;
  currentPlayerId: string | null;
  currentBet: number;
  minRaise: number;
  legalActions: PlayerActionType[];
  winnerPlayerIds: string[];
  winnerNames: string[];
  winningHandRank: HandRank | null;
  autoStartNextRound?: boolean;
  lastRoundWinnings?: Record<string, number>;
};

export type JoinedPlayer = {
  id: string;
  name: string;
};

export type AddPlayerRequest = {
  id: string;
  name: string;
  chips: number;
};

export type PlayerActionRequest = {
  playerId: string;
  action: PlayerActionType;
  amount?: number;
  chips?: number;
};
