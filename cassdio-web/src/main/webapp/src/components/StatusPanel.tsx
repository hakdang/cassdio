import type { HealthResponse } from '../utils/api';

type StatusPanelProps = {
  health: HealthResponse | null;
  loading: boolean;
  error: string | null;
  onRefresh: () => void;
};

export function StatusPanel({ health, loading, error, onRefresh }: StatusPanelProps) {
  return (
    <section className="grid gap-4 rounded-console border border-console-line bg-console-surface p-4 sm:grid-cols-[minmax(0,1fr)_minmax(0,1fr)_auto]" aria-live="polite">
      <div>
        <p className="mb-1 text-xs font-semibold uppercase text-console-muted">API status</p>
        <strong className={`block text-lg ${health?.status === 'UP' ? 'text-console-success' : 'text-console-muted'}`}>
          {loading ? 'Checking' : health?.status ?? 'Unavailable'}
        </strong>
      </div>
      <div>
        <p className="mb-1 text-xs font-semibold uppercase text-console-muted">Service</p>
        <span className="block text-lg">{health?.service ?? 'cassdio-web'}</span>
      </div>
      <button className="h-10 rounded bg-console-brand px-4 text-sm font-semibold text-white hover:bg-blue-700" type="button" onClick={onRefresh}>
        Refresh
      </button>
      {error ? <p className="text-sm text-console-danger sm:col-span-3">{error}</p> : null}
    </section>
  );
}
