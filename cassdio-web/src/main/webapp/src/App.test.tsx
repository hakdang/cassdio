import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, expect, it, vi } from 'vitest';
import { App } from './App';

vi.mock('./utils/api', () => ({
  fetchHealth: vi.fn().mockResolvedValue({
    status: 'UP',
    service: 'cassdio-web',
    timestamp: new Date().toISOString(),
  }),
}));

describe('App', () => {
  it('renders the Cassdio workspace', async () => {
    window.localStorage.setItem(
      'cassdio.session',
      JSON.stringify({ email: 'operator@cassdio.local', accessToken: 'test-token' }),
    );

    render(
      <MemoryRouter initialEntries={['/health']}>
        <App />
      </MemoryRouter>,
    );

    expect(screen.getByText('Cassdio')).toBeInTheDocument();
    expect(await screen.findByText('UP')).toBeInTheDocument();
  });

  it('redirects anonymous users to login', () => {
    window.localStorage.clear();

    render(
      <MemoryRouter initialEntries={['/cluster']}>
        <App />
      </MemoryRouter>,
    );

    expect(screen.getByText('Sign in to Cassdio')).toBeInTheDocument();
  });
});
