import type { HealthResponse } from '../../types/api';

type FooterProps = {
  health: HealthResponse | null;
};

export function Footer({ health }: FooterProps) {
  const apiOnline = health?.status === 'UP';

  return (
    <footer className="flex min-h-10 flex-wrap items-center gap-x-6 gap-y-2 border-t border-console-line bg-console-surface px-4 py-2 text-xs text-console-muted sm:px-6 lg:px-8">
      <span>Version 0.1.0</span>
      <span>Workspace local</span>
      <span>Metadata Cassandra pending</span>
      <span className={apiOnline ? 'text-console-success' : 'text-console-danger'}>API {apiOnline ? 'Online' : 'Offline'}</span>
    </footer>
  );
}
