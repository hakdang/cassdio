import { ChevronRight } from 'lucide-react';
import { Link } from 'react-router-dom';

export type BreadcrumbItem = {
  label: string;
  path?: string;
};

type BreadcrumbProps = {
  items: BreadcrumbItem[];
};

export function Breadcrumb({ items }: BreadcrumbProps) {
  return (
    <nav aria-label="Breadcrumb" className="flex min-w-0 items-center gap-1 text-sm text-console-muted">
      {items.map((item, index) => {
        const isCurrent = index === items.length - 1;

        return (
          <span key={`${item.label}-${index}`} className="flex min-w-0 items-center gap-1">
            {index > 0 ? <ChevronRight aria-hidden className="h-4 w-4 shrink-0" /> : null}
            {item.path && !isCurrent ? (
              <Link className="truncate rounded px-1 py-0.5 hover:bg-slate-100 hover:text-console-ink" to={item.path}>
                {item.label}
              </Link>
            ) : (
              <span className="truncate px-1 py-0.5 font-medium text-console-ink" aria-current={isCurrent ? 'page' : undefined}>
                {item.label}
              </span>
            )}
          </span>
        );
      })}
    </nav>
  );
}
