import { Trophy, X } from 'lucide-react';
import type { FC } from 'react';

type Winner = { name: string; amount: number };

type Props = {
  winners: Winner[];
  onClose: () => void;
};

export const WinnerToast: FC<Props> = ({ winners, onClose }) => {
  if (!winners || winners.length === 0) return null;
  return (
    <div className="fixed left-1/2 top-6 z-50 w-[28rem] -translate-x-1/2 rounded-md bg-amber-300 px-4 py-3 shadow-lg">
      <div className="flex items-start justify-between gap-3">
        <div className="flex items-center gap-2">
          <Trophy className="h-5 w-5 text-slate-950" />
          <div>
            <div className="text-sm font-bold text-slate-950">Round winners</div>
            <div className="text-xs text-slate-900/80">
              {winners.map((w) => `${w.name} +${w.amount}`).join(', ')}
            </div>
          </div>
        </div>
        <button onClick={onClose} className="text-slate-950">
          <X className="h-4 w-4" />
        </button>
      </div>
    </div>
  );
};

export default WinnerToast;
