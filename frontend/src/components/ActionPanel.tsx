import { BadgeDollarSign, Check, CornerUpRight, Hand, RefreshCcw, RotateCcw } from 'lucide-react';
import type { GameState, PlayerActionType } from '../types';

type ActionPanelProps = {
  game: GameState | null;
  activePlayerId: string;
  actionAmount: number;
  isBusy: boolean;
  onAmountChange: (amount: number) => void;
  onDeal: () => void;
  onReveal: (count: number) => void;
  onAction: (action: PlayerActionType) => void;
  onRefresh: () => void;
};

const actionIcons: Record<PlayerActionType, typeof Check> = {
  CHECK: Check,
  CALL: Hand,
  BET: BadgeDollarSign,
  RAISE: CornerUpRight,
  FOLD: RotateCcw,
};

export function ActionPanel({
  game,
  activePlayerId,
  actionAmount,
  isBusy,
  onAmountChange,
  onDeal,
  onReveal,
  onAction,
  onRefresh,
}: ActionPanelProps) {
  const legalActions = new Set(game?.legalActions ?? []);
  const isWaiting = game?.status === 'WAITING';
  const isActiveTurn = Boolean(game && activePlayerId && game.currentPlayerId === activePlayerId);
  const revealCount = game?.status === 'PRE_FLOP' ? 3 : game?.status === 'FLOP' || game?.status === 'TURN' ? 1 : 0;

  return (
    <aside className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <div className="mb-4 flex items-center justify-between gap-3">
        <div>
          <h2 className="text-base font-semibold text-slate-950">Controls</h2>
          <p className="text-sm text-slate-500">
            {isActiveTurn ? 'Active player turn' : 'Select the player whose turn it is'}
          </p>
        </div>
        <button
          type="button"
          onClick={onRefresh}
          disabled={!game || isBusy}
          className="inline-flex h-9 w-9 items-center justify-center rounded-md border border-slate-200 text-slate-700 hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-45"
          aria-label="Refresh game state"
          title="Refresh game state"
        >
          <RefreshCcw className="h-4 w-4" />
        </button>
      </div>

      <div className="grid grid-cols-2 gap-2">
        <button
          type="button"
          onClick={onDeal}
          disabled={!game || !isWaiting || isBusy}
          className="rounded-md bg-slate-950 px-3 py-2 text-sm font-semibold text-white hover:bg-slate-800 disabled:cursor-not-allowed disabled:bg-slate-300"
        >
          Deal
        </button>
        <button
          type="button"
          onClick={() => onReveal(revealCount)}
          disabled={!game || revealCount === 0 || isBusy}
          className="rounded-md bg-emerald-700 px-3 py-2 text-sm font-semibold text-white hover:bg-emerald-600 disabled:cursor-not-allowed disabled:bg-slate-300"
        >
          Reveal
        </button>
      </div>

      <label className="mt-4 block text-sm font-medium text-slate-700" htmlFor="amount">
        Bet or raise amount
      </label>
      <input
        id="amount"
        type="number"
        min={game?.minRaise ?? 1}
        value={actionAmount}
        onChange={(event) => onAmountChange(Number(event.target.value))}
        className="mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-emerald-600 focus:ring-2 focus:ring-emerald-600/15"
      />

      <div className="mt-4 grid grid-cols-2 gap-2">
        {(['CHECK', 'CALL', 'BET', 'RAISE', 'FOLD'] as PlayerActionType[]).map((action) => {
          const Icon = actionIcons[action];
          const enabled = isActiveTurn && legalActions.has(action) && !isBusy;
          return (
            <button
              key={action}
              type="button"
              onClick={() => onAction(action)}
              disabled={!enabled}
              className="inline-flex items-center justify-center gap-2 rounded-md border border-slate-200 px-3 py-2 text-sm font-semibold text-slate-800 hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
            >
              <Icon className="h-4 w-4" />
              {action}
            </button>
          );
        })}
      </div>
    </aside>
  );
}
