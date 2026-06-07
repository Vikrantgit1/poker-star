import { Trophy } from 'lucide-react';
import type { GameState, Player } from '../types';
import { CardView } from './CardView';
import { PlayerSeat } from './PlayerSeat';

type GameTableProps = {
  game: GameState | null;
  activePlayerId: string;
  onSelectPlayer: (playerId: string) => void;
};

export function GameTable({ game, activePlayerId, onSelectPlayer }: GameTableProps) {
  if (!game) {
    return (
      <section className="flex min-h-[34rem] items-center justify-center rounded-xl border border-slate-200 bg-white p-8 text-center shadow-sm">
        <div>
          <p className="text-lg font-semibold text-slate-950">No active table</p>
          <p className="mt-2 text-sm text-slate-500">Create or load a game to start playing.</p>
        </div>
      </section>
    );
  }

  return (
    <section className="rounded-xl bg-felt-900 p-4 text-white shadow-table sm:p-6">
      <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
        <div>
          <p className="text-xs uppercase tracking-wide text-emerald-100/70">Game {game.gameId}</p>
          <h1 className="text-2xl font-bold">{formatPhase(game.status)}</h1>
        </div>
        <div className="grid grid-cols-3 gap-2 text-right text-sm">
          <Metric label="Pot" value={game.pot} />
          <Metric label="Current bet" value={game.currentBet} />
          <Metric label="Min raise" value={game.minRaise} />
        </div>
      </div>

      <div className="mb-5 rounded-lg border border-white/15 bg-black/18 p-4">
        <div className="mb-3 flex items-center justify-between">
          <p className="text-sm font-semibold text-emerald-50">Community cards</p>
          {game.winnerNames.length > 0 && (
            <div className="flex items-center gap-2 rounded-full bg-amber-300 px-3 py-1 text-sm font-bold text-slate-950">
              <Trophy className="h-4 w-4" />
              {game.winnerNames.join(', ')}
              {game.winningHandRank ? ` · ${formatPhase(game.winningHandRank)}` : ''}
            </div>
          )}
        </div>
        <div className="flex min-h-24 flex-wrap gap-2">
          {game.communityCards.length ? (
            game.communityCards.map((card, index) => (
              <CardView key={`${card.rank}-${card.suit}-${index}`} card={card} />
            ))
          ) : (
            <p className="self-center text-sm text-emerald-100/65">Cards have not been revealed yet.</p>
          )}
        </div>
      </div>

      <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
        {game.players.map((player: Player) => (
          <PlayerSeat
            key={player.playerId}
            player={player}
            isActiveViewer={player.playerId === activePlayerId}
            isCurrentTurn={player.playerId === game.currentPlayerId}
            isWinner={game.winnerPlayerIds.includes(player.playerId)}
            onSelect={onSelectPlayer}
          />
        ))}
      </div>
    </section>
  );
}

function Metric({ label, value }: { label: string; value: number }) {
  return (
    <div className="rounded-md bg-white/10 px-3 py-2">
      <div className="text-xs text-emerald-100/70">{label}</div>
      <div className="font-bold text-white">{value}</div>
    </div>
  );
}

function formatPhase(value: string) {
  return value
    .toLowerCase()
    .split('_')
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join(' ');
}
