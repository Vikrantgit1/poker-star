import { CircleDollarSign, Eye, Timer } from 'lucide-react';
import type { Player } from '../types';
import { CardView } from './CardView';

type PlayerSeatProps = {
  player: Player;
  isActiveViewer: boolean;
  isCurrentTurn: boolean;
  isWinner: boolean;
  onSelect: (playerId: string) => void;
};

export function PlayerSeat({
  player,
  isActiveViewer,
  isCurrentTurn,
  isWinner,
  onSelect,
}: PlayerSeatProps) {
  return (
    <button
      type="button"
      onClick={() => onSelect(player.playerId)}
      className={`min-h-44 rounded-lg border p-3 text-left transition ${
        isActiveViewer
          ? 'border-amber-300 bg-amber-50 shadow-md'
          : 'border-white/25 bg-white/12 hover:bg-white/18'
      } ${player.folded ? 'opacity-60' : ''}`}
    >
      <div className="mb-3 flex items-start justify-between gap-3">
        <div className="min-w-0">
          <div className="flex items-center gap-2">
            <p className="truncate text-sm font-semibold text-white">
              {player.name}
            </p>
            {isActiveViewer && <Eye className="h-4 w-4 shrink-0 text-amber-300" />}
            {isCurrentTurn && <Timer className="h-4 w-4 shrink-0 text-cyan-200" />}
          </div>
          <p className="mt-1 text-xs uppercase tracking-wide text-emerald-100/75">
            {player.folded ? 'Folded' : player.actedThisRound ? 'Acted' : 'Waiting'}
          </p>
        </div>
        {isWinner && (
          <span className="rounded-full bg-amber-300 px-2 py-1 text-xs font-bold text-slate-950">
            Winner
          </span>
        )}
      </div>

      <div className="mb-3 flex gap-2">
        {player.hand?.length ? (
          player.hand.map((card, index) => <CardView key={`${card.rank}-${card.suit}-${index}`} card={card} />)
        ) : (
          <>
            <CardView hidden />
            <CardView hidden />
          </>
        )}
      </div>

      <div className="grid grid-cols-2 gap-2 text-xs">
        <div className="rounded-md bg-black/18 px-2 py-2 text-emerald-50">
          <div className="flex items-center gap-1 text-emerald-100/75">
            <CircleDollarSign className="h-3.5 w-3.5" />
            Chips
          </div>
          <strong className="text-sm">{player.chips}</strong>
        </div>
        <div className="rounded-md bg-black/18 px-2 py-2 text-emerald-50">
          <div className="text-emerald-100/75">Round bet</div>
          <strong className="text-sm">{player.currentRoundBet}</strong>
        </div>
      </div>
    </button>
  );
}
