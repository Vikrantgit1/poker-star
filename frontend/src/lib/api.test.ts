import { describe, expect, it } from 'vitest';
import { testExports } from './api';

describe('api helpers', () => {
  it('adds viewerPlayerId as the first query param', () => {
    expect(testExports.withViewer('/game/1/state', 'player one')).toBe(
      '/game/1/state?viewerPlayerId=player%20one',
    );
  });

  it('adds viewerPlayerId after existing query params', () => {
    expect(testExports.withViewer('/game/1/community?count=3', 'p1')).toBe(
      '/game/1/community?count=3&viewerPlayerId=p1',
    );
  });

  it('leaves paths unchanged without a viewer', () => {
    expect(testExports.withViewer('/game/create', '')).toBe('/game/create');
  });
});
