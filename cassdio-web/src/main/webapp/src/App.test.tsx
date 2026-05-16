import { render, screen } from '@testing-library/react';
import { describe, expect, it, vi } from 'vitest';
import { App } from './App';

describe('App', () => {
  it('renders the Cassdio workspace', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue({
        ok: true,
        json: async () => ({
          status: 'UP',
          service: 'cassdio-web',
          timestamp: new Date().toISOString(),
        }),
      }),
    );

    render(<App />);

    expect(screen.getByText('Cassdio')).toBeInTheDocument();
    expect(await screen.findByText('UP')).toBeInTheDocument();
  });
});
