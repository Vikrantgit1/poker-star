import { useEffect, useMemo, useState } from 'react';
import { AlertCircle } from 'lucide-react';
import WinnerToast from './components/WinnerToast';
import { ActionPanel } from './components/ActionPanel';
import { GameTable } from './components/GameTable';
import { LobbyPanel } from './components/LobbyPanel';
import { gameApi } from './lib/api';
import { clearSession, loadSession, saveSession } from './lib/storage';
import type { GameState, JoinedPlayer, PlayerActionType } from './types';

function newPlayerId() {
  return `player-${crypto.randomUUID()}`;
}

export default function App() {
  const initialSession = useMemo(() => loadSession(), []);
  const [game, setGame] = useState<GameState | null>(null);
  const [gameId, setGameId] = useState(initialSession.gameId);
  const [joinedPlayers, setJoinedPlayers] = useState<JoinedPlayer[]>(initialSession.players);
  const [activePlayerId, setActivePlayerId] = useState(initialSession.activePlayerId);
  const [playerName, setPlayerName] = useState('');
  const [startingChips, setStartingChips] = useState(1000);
  const [actionAmount, setActionAmount] = useState(10);
  const [isBusy, setIsBusy] = useState(false);
  const [error, setError] = useState('');
  const [toastWinners, setToastWinners] = useState<{ name: string; amount: number }[] | null>(null);
  const [highlightedPlayers, setHighlightedPlayers] = useState<string[]>([]);

  useEffect(() => {
    saveSession({ gameId, players: joinedPlayers, activePlayerId });
  }, [activePlayerId, gameId, joinedPlayers]);

  useEffect(() => {
    if (gameId) {
      void refreshGame(gameId, activePlayerId);
    }
    // This intentionally runs when the viewer changes so hidden cards and legal actions refresh.
  }, [activePlayerId]);

  async function run(operation: () => Promise<GameState>) {
    setIsBusy(true);
    setError('');
    try {
      const nextGame = await operation();
      // detect round finish to show winners and highlight players briefly
      setGame((prev) => {
        if (nextGame) {
          if (prev?.status !== 'FINISHED' && nextGame.status === 'FINISHED' && nextGame.winnerPlayerIds?.length) {
            // prepare toast and highlights
            const winners = nextGame.winnerPlayerIds.map((id, idx) => ({
              name: nextGame.winnerNames?.[idx] ?? id,
              amount: nextGame.lastRoundWinnings?.[id] ?? 0,
            }));
            setToastWinners(winners);
            setHighlightedPlayers(nextGame.winnerPlayerIds);
            // clear highlight after 4s
            setTimeout(() => setHighlightedPlayers([]), 4000);
            // clear toast after 5s
            setTimeout(() => setToastWinners(null), 5000);
            // if server is set to auto-start, refresh after short delay to pick up the new round
            if (nextGame.autoStartNextRound) {
              setTimeout(() => void refreshGame(), 1400);
            }
          }
        }
        return nextGame;
      });
      setGameId(nextGame.gameId);
      return nextGame;
    } catch (caught) {
      setError(caught instanceof Error ? normalizeError(caught.message) : 'Something went wrong');
      return null;
    } finally {
      setIsBusy(false);
    }
  }

  async function refreshGame(id = gameId, viewer = activePlayerId) {
    if (!id) {
      return null;
    }
    return run(() => gameApi.getState(id, { viewerPlayerId: viewer }));
  }

  async function handleCreateGame() {
    const created = await run(() => gameApi.createGame({ viewerPlayerId: activePlayerId }));
    if (created) {
      setJoinedPlayers([]);
      setActivePlayerId('');
    }
  }

  async function handleJoinGame() {
    const trimmedName = playerName.trim();
    if (!gameId || !trimmedName) {
      return;
    }
    const player = {
      id: newPlayerId(),
      name: trimmedName,
      chips: Number.isFinite(startingChips) ? startingChips : 0,
    };
    const nextGame = await run(() => gameApi.joinGame(gameId, player, { viewerPlayerId: player.id }));
    if (nextGame) {
      setJoinedPlayers((players) => [...players, { id: player.id, name: player.name }]);
      setActivePlayerId(player.id);
      setPlayerName('');
    }
  }

  async function handleDeal() {
    await run(() => gameApi.dealCards(gameId, { viewerPlayerId: activePlayerId }));
  }

  async function handleReveal(count: number) {
    if (!count) {
      return;
    }
    await run(() => gameApi.revealCommunity(gameId, count, { viewerPlayerId: activePlayerId }));
  }

  async function handleAction(action: PlayerActionType) {
    if (!activePlayerId) {
      setError('Select an active player before acting.');
      return;
    }
    if (action === 'FOLD') {
      await run(() => gameApi.fold(gameId, activePlayerId, { viewerPlayerId: activePlayerId }));
      return;
    }
    const amount = action === 'BET' || action === 'RAISE' ? actionAmount : undefined;
    await run(() => gameApi.submitAction(gameId, activePlayerId, action, amount, { viewerPlayerId: activePlayerId }));
  }

  function handleSelectPlayer(playerId: string) {
    setActivePlayerId(playerId);
  }

  function handleClearSession() {
    clearSession();
    setGame(null);
    setGameId('');
    setJoinedPlayers([]);
    setActivePlayerId('');
    setPlayerName('');
    setError('');
  }

  return (
    <main className="min-h-screen bg-[#e7ece8] px-4 py-5 text-slate-950 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-7xl">
        <header className="mb-5 flex flex-wrap items-end justify-between gap-3">
          <div>
            <p className="text-sm font-semibold uppercase tracking-wide text-emerald-700">Poker Star</p>
            <h1 className="text-3xl font-bold tracking-normal text-slate-950">Texas hold&apos;em table</h1>
          </div>
          {game && (
            <div className="rounded-md border border-slate-200 bg-white px-3 py-2 text-sm text-slate-600 shadow-sm">
              Current turn:{' '}
              <strong className="text-slate-950">
                {game.players.find((player) => player.playerId === game.currentPlayerId)?.name ?? 'None'}
              </strong>
            </div>
          )}
        </header>

        {error && (
          <div className="mb-4 flex items-start gap-2 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800">
            <AlertCircle className="mt-0.5 h-4 w-4 shrink-0" />
            <p>{error}</p>
          </div>
        )}

        <div className="grid gap-4 lg:grid-cols-[20rem_minmax(0,1fr)_20rem]">
          <LobbyPanel
            gameId={gameId}
            activePlayerId={activePlayerId}
            joinedPlayers={joinedPlayers}
            playerName={playerName}
            startingChips={startingChips}
            isBusy={isBusy}
            autoStartNextRound={game?.autoStartNextRound}
            onToggleAutoStart={async (enabled: boolean) => {
              if (!gameId) return;
              const next = await run(() => gameApi.setAutoStart(gameId, enabled, { viewerPlayerId: activePlayerId }));
              if (next) {
                setGame(next);
              }
            }}
            onGameIdChange={setGameId}
            onPlayerNameChange={setPlayerName}
            onStartingChipsChange={setStartingChips}
            onCreateGame={handleCreateGame}
            onLoadGame={() => void refreshGame()}
            onJoinGame={handleJoinGame}
            onSelectPlayer={handleSelectPlayer}
            onClearSession={handleClearSession}
          />

          <GameTable
            game={game}
            activePlayerId={activePlayerId}
            onSelectPlayer={handleSelectPlayer}
            highlightedPlayerIds={highlightedPlayers}
          />

          <ActionPanel
            game={game}
            activePlayerId={activePlayerId}
            actionAmount={actionAmount}
            isBusy={isBusy}
            onAmountChange={setActionAmount}
            onDeal={handleDeal}
            onReveal={handleReveal}
            onAction={handleAction}
            onRefresh={() => void refreshGame()}
          />
        </div>
        {toastWinners && <WinnerToast winners={toastWinners} onClose={() => setToastWinners(null)} />}
      </div>
    </main>
  );
}

function normalizeError(message: string) {
  try {
    const parsed = JSON.parse(message) as { message?: string; error?: string };
    return parsed.message ?? parsed.error ?? message;
  } catch {
    return message;
  }
}
