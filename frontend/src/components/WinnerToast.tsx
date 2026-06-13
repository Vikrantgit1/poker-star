import { Trophy, X } from 'lucide-react';
import { useEffect, useMemo } from 'react';
import type { FC } from 'react';
 
 type Winner = { name: string; amount: number };
 
 type Props = {
   winners: Winner[];
   onClose: () => void;
 };
 
 const createConfettiPieces = () =>
   Array.from({ length: 18 }, (_, index) => ({
     id: index,
     left: `${Math.floor(Math.random() * 80) + 10}%`,
     rotate: `${Math.floor(Math.random() * 360)}deg`,
     delay: `${Math.random() * 0.4}s`,
     color: `hsl(${Math.floor(Math.random() * 50 + 40)}, 90%, 65%)`,
   }));
 
 const playCelebrationSound = () => {
   if (typeof window === 'undefined' || !window.AudioContext) {
     return;
   }
 
   const audioCtx = new window.AudioContext();
   const oscillator = audioCtx.createOscillator();
   const gain = audioCtx.createGain();
   oscillator.type = 'triangle';
   oscillator.frequency.value = 880;
   gain.gain.value = 0.15;
   oscillator.connect(gain);
   gain.connect(audioCtx.destination);
   oscillator.start();
   oscillator.frequency.exponentialRampToValueAtTime(660, audioCtx.currentTime + 0.2);
   gain.gain.exponentialRampToValueAtTime(0.001, audioCtx.currentTime + 0.4);
   oscillator.stop(audioCtx.currentTime + 0.45);
 };
 
 export const WinnerToast: FC<Props> = ({ winners, onClose }) => {
   const confetti = useMemo(() => createConfettiPieces(), []);
 
   useEffect(() => {
     playCelebrationSound();
   }, []);
 
   if (!winners || winners.length === 0) return null;
   return (
     <div className="relative">
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
       <div className="pointer-events-none fixed left-1/2 top-6 z-40 h-0 w-[28rem] -translate-x-1/2 overflow-visible">
         {confetti.map((piece) => (
           <span
             key={piece.id}
             className="absolute top-0 h-3 w-1 rounded-full opacity-90 animate-confetti"
             style={{
               left: piece.left,
               background: piece.color,
               transform: `rotate(${piece.rotate})`,
               animationDelay: piece.delay,
             }}
           />
         ))}
       </div>
     </div>
   );
};

export default WinnerToast;
