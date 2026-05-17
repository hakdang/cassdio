import type { LucideIcon } from 'lucide-react';
import { AlertTriangle, Inbox, LoaderCircle } from 'lucide-react';
import type { ReactNode } from 'react';

type StateViewProps = {
  title: string;
  message?: string;
  action?: ReactNode;
  icon?: LucideIcon;
};

function StateFrame({ title, message, action, icon: Icon }: StateViewProps) {
  return (
    <div className="flex min-h-56 flex-col items-center justify-center gap-3 rounded-console border border-dashed border-console-line bg-console-surface p-8 text-center">
      {Icon ? <Icon aria-hidden className="h-8 w-8 text-console-muted" /> : null}
      <div>
        <h2 className="text-base font-semibold text-console-ink">{title}</h2>
        {message ? <p className="mt-1 max-w-md text-sm leading-6 text-console-muted">{message}</p> : null}
      </div>
      {action ? <div className="mt-1">{action}</div> : null}
    </div>
  );
}

export function EmptyState(props: Omit<StateViewProps, 'icon'>) {
  return <StateFrame {...props} icon={Inbox} />;
}

export function ErrorState({ code, ...props }: Omit<StateViewProps, 'icon'> & { code?: string }) {
  return (
    <StateFrame
      {...props}
      icon={AlertTriangle}
      message={code ? `${props.message ?? 'Request failed.'} (${code})` : props.message}
    />
  );
}

export function LoadingState({ label = 'Loading' }: { label?: string }) {
  return (
    <div className="flex min-h-56 items-center justify-center gap-3 rounded-console border border-console-line bg-console-surface p-8 text-sm font-medium text-console-muted">
      <LoaderCircle aria-hidden className="h-5 w-5 animate-spin text-console-brand" />
      <span>{label}</span>
    </div>
  );
}

export function Skeleton({ lines = 4 }: { lines?: number }) {
  return (
    <div className="animate-pulse rounded-console border border-console-line bg-console-surface p-5">
      <div className="mb-5 h-5 w-48 rounded bg-slate-200" />
      <div className="space-y-3">
        {Array.from({ length: lines }).map((_, index) => (
          <div key={index} className="h-4 rounded bg-slate-200" style={{ width: `${92 - index * 12}%` }} />
        ))}
      </div>
    </div>
  );
}
