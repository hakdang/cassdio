import { Menu } from 'lucide-react';
import { NavLink, useLocation } from 'react-router-dom';
import { getActiveGnb, gnbItems } from '../../config/navigation';
import { UserMenu } from './UserMenu';

type HeaderProps = {
  onToggleSidebar: () => void;
};

export function Header({ onToggleSidebar }: HeaderProps) {
  const { pathname } = useLocation();
  const activeGnb = getActiveGnb(pathname);

  return (
    <header className="sticky top-0 z-header flex h-14 items-center border-b border-console-line bg-console-surface px-3 sm:px-4">
      <button
        type="button"
        className="mr-2 inline-flex h-9 w-9 items-center justify-center rounded border border-console-line bg-console-surface text-console-muted lg:hidden"
        aria-label="Toggle sidebar"
        onClick={onToggleSidebar}
      >
        <Menu className="h-5 w-5" aria-hidden />
      </button>
      <NavLink className="mr-4 shrink-0 text-base font-bold text-console-ink" to="/">
        Cassdio
      </NavLink>
      <nav className="flex min-w-0 flex-1 items-center gap-1 overflow-x-auto" aria-label="Global">
        {gnbItems.map((item) => {
          const Icon = item.icon;
          const selected = activeGnb === item.key;

          return (
            <NavLink
              key={item.key}
              to={item.path}
              className={`inline-flex h-9 shrink-0 items-center gap-2 rounded px-3 text-sm font-medium transition ${
                selected ? 'bg-slate-900 text-white' : 'text-console-muted hover:bg-slate-100 hover:text-console-ink'
              }`}
            >
              <Icon aria-hidden className="h-4 w-4" />
              <span className="hidden md:inline">{item.label}</span>
            </NavLink>
          );
        })}
      </nav>
      <UserMenu />
    </header>
  );
}
