import type { ReactNode } from 'react';
import { Breadcrumb, type BreadcrumbItem } from './Breadcrumb';

type PageHeaderProps = {
  title: string;
  description?: string;
  breadcrumbs?: BreadcrumbItem[];
  actions?: ReactNode;
};

export function PageHeader({ title, description, breadcrumbs = [], actions }: PageHeaderProps) {
  return (
    <header className="flex flex-col gap-4 border-b border-console-line bg-console-surface px-4 py-5 sm:px-6 lg:px-8">
      {breadcrumbs.length > 0 ? <Breadcrumb items={breadcrumbs} /> : null}
      <div className="flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
        <div className="min-w-0">
          <h1 className="truncate text-2xl font-semibold leading-tight text-console-ink">{title}</h1>
          {description ? <p className="mt-1 max-w-3xl text-sm leading-6 text-console-muted">{description}</p> : null}
        </div>
        {actions ? <div className="flex shrink-0 flex-wrap items-center gap-2">{actions}</div> : null}
      </div>
    </header>
  );
}
