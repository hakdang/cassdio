import { DatabaseZap } from 'lucide-react';
import { FormEvent, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { authService } from '../services/auth';

export function LoginPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [email, setEmail] = useState('admin@cassdio.local');
  const [password, setPassword] = useState('ChangeMe!2026');
  const [error, setError] = useState<string | null>(null);

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError(null);

    try {
      await authService.login({ email, password });
      navigate(searchParams.get('redirect') || '/', { replace: true });
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : 'Login failed.');
    }
  }

  return (
    <main className="grid min-h-screen place-items-center bg-console-bg px-4 py-8">
      <section className="w-full max-w-md rounded-console border border-console-line bg-console-surface p-6 shadow-console">
        <div className="mb-6 flex items-center gap-3">
          <span className="inline-flex h-10 w-10 items-center justify-center rounded bg-slate-900 text-white">
            <DatabaseZap aria-hidden className="h-5 w-5" />
          </span>
          <div>
            <h1 className="text-xl font-semibold text-console-ink">Sign in to Cassdio</h1>
            <p className="text-sm text-console-muted">Use your Cassdio member account.</p>
          </div>
        </div>
        <form className="space-y-4" onSubmit={submit}>
          <label className="block text-sm font-medium text-console-ink">
            Email
            <input
              className="mt-1 h-10 w-full rounded border border-console-line px-3 text-sm outline-none focus:border-console-brand focus:ring-2 focus:ring-blue-100"
              type="email"
              value={email}
              autoComplete="email"
              onChange={(event) => setEmail(event.target.value)}
            />
          </label>
          <label className="block text-sm font-medium text-console-ink">
            Password
            <input
              className="mt-1 h-10 w-full rounded border border-console-line px-3 text-sm outline-none focus:border-console-brand focus:ring-2 focus:ring-blue-100"
              type="password"
              value={password}
              autoComplete="current-password"
              onChange={(event) => setPassword(event.target.value)}
            />
          </label>
          {error ? <p className="rounded border border-red-200 bg-red-50 px-3 py-2 text-sm text-console-danger">{error}</p> : null}
          <button className="h-10 w-full rounded bg-console-brand px-4 text-sm font-semibold text-white hover:bg-blue-700" type="submit">
            Login
          </button>
        </form>
      </section>
    </main>
  );
}
