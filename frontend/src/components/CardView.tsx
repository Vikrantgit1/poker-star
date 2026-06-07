import type { Card } from '../types';

type CardViewProps = {
  card?: Card | null;
  hidden?: boolean;
};

const suitSymbols: Record<string, string> = {
  HEARTS: '♥',
  DIAMONDS: '♦',
  CLUBS: '♣',
  SPADES: '♠',
};

const redSuits = new Set(['HEARTS', 'DIAMONDS']);

export function CardView({ card, hidden = false }: CardViewProps) {
  if (hidden || !card) {
    return (
      <div className="flex h-20 w-14 items-center justify-center rounded-md border border-emerald-950/20 bg-emerald-950 text-xs font-semibold text-emerald-100 shadow-sm sm:h-24 sm:w-16">
        P★
      </div>
    );
  }

  const suit = suitSymbols[card.suit] ?? card.suit;
  const isRed = redSuits.has(card.suit);

  return (
    <div
      className={`playing-card flex h-20 w-14 flex-col justify-between rounded-md border border-slate-300 p-2 text-sm font-bold shadow-sm sm:h-24 sm:w-16 ${
        isRed ? 'text-red-600' : 'text-slate-900'
      }`}
      aria-label={`${card.rank} of ${card.suit}`}
    >
      <span>{card.rank}</span>
      <span className="self-center text-2xl">{suit}</span>
      <span className="self-end rotate-180">{card.rank}</span>
    </div>
  );
}
