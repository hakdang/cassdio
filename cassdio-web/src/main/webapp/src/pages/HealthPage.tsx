import { StatusPanel } from '../components/StatusPanel';
import { useHealth } from '../hooks/useHealth';

export function HealthPage() {
  const { data, loading, error, refresh } = useHealth();

  return (
    <main className="app-shell">
      <section className="page-header">
        <p className="eyebrow">Cassdio</p>
        <h1>Cassandra studio workspace</h1>
        <p className="summary">A clean starting point for the Cassdio API and client.</p>
      </section>
      <StatusPanel health={data} loading={loading} error={error} onRefresh={refresh} />
    </main>
  );
}
