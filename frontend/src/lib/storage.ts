import type { JoinedPlayer } from '../types';

const STORAGE_KEY = 'poker-star-session';

export type StoredSession = {
  gameId: string;
  players: JoinedPlayer[];
  activePlayerId: string;
};

export function loadSession(): StoredSession {
  try {
    const raw = window.localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      return emptySession();
    }
    const parsed = JSON.parse(raw) as Partial<StoredSession>;
    return {
      gameId: parsed.gameId ?? '',
      players: Array.isArray(parsed.players) ? parsed.players : [],
      activePlayerId: parsed.activePlayerId ?? '',
    };
  } catch {
    return emptySession();
  }
}

export function saveSession(session: StoredSession) {
  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
}

export function clearSession() {
  window.localStorage.removeItem(STORAGE_KEY);
}

function emptySession(): StoredSession {
  return {
    gameId: '',
    players: [],
    activePlayerId: '',
  };
}
