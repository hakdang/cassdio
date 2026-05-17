import { RefreshCw } from 'lucide-react';
import { PageHeader } from '../components/common/PageHeader';
import { EmptyState, Skeleton } from '../components/common/StateViews';

type ConsolePageProps = {
  title: string;
  description: string;
  section: string;
};

export function ConsolePage({ title, description, section }: ConsolePageProps) {
  return (
    <section>
      <PageHeader
        title={title}
        description={description}
        breadcrumbs={[
          { label: section, path: title === 'Workspace' ? '/' : undefined },
          { label: title },
        ]}
        actions={
          <button className="inline-flex h-9 items-center gap-2 rounded bg-console-brand px-3 text-sm font-semibold text-white hover:bg-blue-700">
            <RefreshCw aria-hidden className="h-4 w-4" />
            Refresh
          </button>
        }
      />
      <div className="grid gap-4 p-4 sm:p-6 lg:p-8">
        <Skeleton />
        <EmptyState title="No records yet" message={`${title} data will appear here when the Phase 2+ APIs are connected.`} />
      </div>
    </section>
  );
}
