import { Plus, Users } from 'lucide-react';
import type { JoinedPlayer } from '../types';

type LobbyPanelProps = {
  gameId: string;
  activePlayerId: string;
  joinedPlayers: JoinedPlayer[];
  playerName: string;
  startingChips: number;
  isBusy: boolean;
  onGameIdChange: (gameId: string) => void;
  onPlayerNameChange: (name: string) => void;
  onStartingChipsChange: (chips: number) => void;
  onCreateGame: () => void;
  onLoadGame: () => void;
  onJoinGame: () => void;
  onSelectPlayer: (playerId: string) => void;
  onClearSession: () => void;
};

export function LobbyPanel({
  gameId,
  activePlayerId,
  joinedPlayers,
  playerName,
  startingChips,
  isBusy,
  onGameIdChange,
  onPlayerNameChange,
  onStartingChipsChange,
  onCreateGame,
  onLoadGame,
  onJoinGame,
  onSelectPlayer,
  onClearSession,
}: LobbyPanelProps) {
  return (
    <aside className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
      <div className="mb-4 flex items-start justify-between gap-3">
        <div>
          <h2 className="text-base font-semibold text-slate-950">Session</h2>
          <p className="text-sm text-slate-500">Create, load, and join a table.</p>
        </div>
        <button
          type="button"
          onClick={onClearSession}
          className="text-sm font-semibold text-slate-500 hover:text-slate-950"
        >
          Reset
        </button>
      </div>

      <label className="block text-sm font-medium text-slate-700" htmlFor="game-id">
        Game ID
      </label>
      <input
        id="game-id"
        value={gameId}
        onChange={(event) => onGameIdChange(event.target.value)}
        className="mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-emerald-600 focus:ring-2 focus:ring-emerald-600/15"
        placeholder="Mongo game id"
      />

      <div className="mt-3 grid grid-cols-2 gap-2">
        <button
          type="button"
          onClick={onCreateGame}
          disabled={isBusy}
          className="inline-flex items-center justify-center gap-2 rounded-md bg-slate-950 px-3 py-2 text-sm font-semibold text-white hover:bg-slate-800 disabled:cursor-not-allowed disabled:bg-slate-300"
        >
          <Plus className="h-4 w-4" />
          Create
        </button>
        <button
          type="button"
          onClick={onLoadGame}
          disabled={!gameId || isBusy}
          className="inline-flex items-center justify-center gap-2 rounded-md border border-slate-200 px-3 py-2 text-sm font-semibold text-slate-800 hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-45"
        >
          Load
        </button>
      </div>

      <div className="mt-5 border-t border-slate-200 pt-4">
        <label className="block text-sm font-medium text-slate-700" htmlFor="player-name">
          Player name
        </label>
        <input
          id="player-name"
          value={playerName}
          onChange={(event) => onPlayerNameChange(event.target.value)}
          className="mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-emerald-600 focus:ring-2 focus:ring-emerald-600/15"
          placeholder="Ada"
        />

        <label className="mt-3 block text-sm font-medium text-slate-700" htmlFor="chips">
          Starting chips
        </label>
        <input
          id="chips"
          type="number"
          min={0}
          value={startingChips}
          onChange={(event) => onStartingChipsChange(Number(event.target.value))}
          className="mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-emerald-600 focus:ring-2 focus:ring-emerald-600/15"
        />

        <button
          type="button"
          onClick={onJoinGame}
          disabled={!gameId || !playerName.trim() || isBusy}
          className="mt-3 inline-flex w-full items-center justify-center gap-2 rounded-md bg-emerald-700 px-3 py-2 text-sm font-semibold text-white hover:bg-emerald-600 disabled:cursor-not-allowed disabled:bg-slate-300"
        >
          <Users className="h-4 w-4" />
          Join player
        </button>
      </div>

      <div className="mt-5 border-t border-slate-200 pt-4">
        <p className="mb-2 text-sm font-semibold text-slate-950">Active viewer</p>
        <div className="flex flex-col gap-2">
          {joinedPlayers.length ? (
            joinedPlayers.map((player) => (
              <button
                key={player.id}
                type="button"
                onClick={() => onSelectPlayer(player.id)}
                className={`rounded-md border px-3 py-2 text-left text-sm ${
                  activePlayerId === player.id
                    ? 'border-amber-300 bg-amber-50 font-semibold text-slate-950'
                    : 'border-slate-200 text-slate-700 hover:bg-slate-50'
                }`}
              >
                {player.name}
              </button>
            ))
          ) : (
            <p className="text-sm text-slate-500">Join players to switch seats locally.</p>
          )}
        </div>
      </div>
    </aside>
  );
}
