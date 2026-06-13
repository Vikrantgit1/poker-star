import type {
  AddPlayerRequest,
  GameState,
  PlayerActionRequest,
  PlayerActionType,
} from '../types';

const API_PREFIX = '/api';

type RequestOptions = {
  viewerPlayerId?: string | null;
};

function withViewer(path: string, viewerPlayerId?: string | null) {
  if (!viewerPlayerId) {
    return path;
  }
  const separator = path.includes('?') ? '&' : '?';
  return `${path}${separator}viewerPlayerId=${encodeURIComponent(viewerPlayerId)}`;
}

async function requestGameState(path: string, init?: RequestInit): Promise<GameState> {
  const response = await fetch(`${API_PREFIX}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(init?.headers ?? {}),
    },
    ...init,
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed with status ${response.status}`);
  }

  return response.json() as Promise<GameState>;
}

export const gameApi = {
  createGame(options: RequestOptions = {}) {
    return requestGameState(withViewer('/game/create', options.viewerPlayerId), {
      method: 'POST',
    });
  },

  joinGame(gameId: string, player: AddPlayerRequest, options: RequestOptions = {}) {
    return requestGameState(withViewer(`/game/${gameId}/join`, options.viewerPlayerId), {
      method: 'POST',
      body: JSON.stringify(player),
    });
  },

  dealCards(gameId: string, options: RequestOptions = {}) {
    return requestGameState(withViewer(`/game/${gameId}/deal`, options.viewerPlayerId), {
      method: 'POST',
    });
  },

  submitAction(
    gameId: string,
    playerId: string,
    action: PlayerActionType,
    amount?: number,
    options: RequestOptions = {},
  ) {
    const body: PlayerActionRequest = {
      playerId,
      action,
      ...(amount ? { amount, chips: amount } : {}),
    };
    return requestGameState(withViewer(`/game/${gameId}/action`, options.viewerPlayerId), {
      method: 'POST',
      body: JSON.stringify(body),
    });
  },

  fold(gameId: string, playerId: string, options: RequestOptions = {}) {
    return requestGameState(withViewer(`/game/${gameId}/fold`, options.viewerPlayerId), {
      method: 'POST',
      body: JSON.stringify({ playerId }),
    });
  },

  revealCommunity(gameId: string, count: number, options: RequestOptions = {}) {
    return requestGameState(
      withViewer(`/game/${gameId}/community?count=${count}`, options.viewerPlayerId),
      { method: 'POST' },
    );
  },

  getState(gameId: string, options: RequestOptions = {}) {
    return requestGameState(withViewer(`/game/${gameId}/state`, options.viewerPlayerId));
  },

  setAutoStart(gameId: string, enabled: boolean, options: RequestOptions = {}) {
    return requestGameState(withViewer(`/game/${gameId}/autostart?enabled=${enabled}`, options.viewerPlayerId), {
      method: 'PATCH',
    });
  },
};

export const testExports = {
  withViewer,
};
