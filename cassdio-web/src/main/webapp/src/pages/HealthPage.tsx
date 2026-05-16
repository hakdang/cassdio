import { StatusPanel } from '../components/StatusPanel';
import { PageHeader } from '../components/common/PageHeader';
import { useHealth } from '../hooks/useHealth';

export function HealthPage() {
  const { data, loading, error, refresh } = useHealth();

  return (
    <section>
      <PageHeader
        title="Workspace Health"
        description="Monitor the Cassdio web API status and service metadata."
        breadcrumbs={[
          { label: 'Dashboard', path: '/' },
          { label: 'Health' },
        ]}
      />
      <div className="p-4 sm:p-6 lg:p-8">
        <StatusPanel health={data} loading={loading} error={error} onRefresh={refresh} />
      </div>
    </section>
  );
}
