import type { HealthResponse } from '../utils/api';

type StatusPanelProps = {
  health: HealthResponse | null;
  loading: boolean;
  error: string | null;
  onRefresh: () => void;
};

export function StatusPanel({ health, loading, error, onRefresh }: StatusPanelProps) {
  return (
    <section className="status-panel" aria-live="polite">
      <div>
        <p className="panel-label">API status</p>
        <strong className={health?.status === 'UP' ? 'status-up' : 'status-muted'}>
          {loading ? 'Checking' : health?.status ?? 'Unavailable'}
        </strong>
      </div>
      <div>
        <p className="panel-label">Service</p>
        <span>{health?.service ?? 'cassdio-web'}</span>
      </div>
      <button type="button" onClick={onRefresh}>
        Refresh
      </button>
      {error ? <p className="error-message">{error}</p> : null}
    </section>
  );
}
