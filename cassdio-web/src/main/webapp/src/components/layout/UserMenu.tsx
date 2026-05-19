import { LogOut, Settings, SlidersHorizontal, UserCircle } from 'lucide-react';
import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../../services/auth';

export function UserMenu() {
  const [open, setOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();
  const session = authService.getSession();

  useEffect(() => {
    function closeOnOutsideClick(event: MouseEvent) {
      if (!menuRef.current?.contains(event.target as Node)) {
        setOpen(false);
      }
    }

    document.addEventListener('mousedown', closeOnOutsideClick);
    return () => document.removeEventListener('mousedown', closeOnOutsideClick);
  }, []);

  function logout() {
    authService.logout();
    navigate('/login');
  }

  return (
    <div className="relative" ref={menuRef}>
      <button
        type="button"
        className="inline-flex h-9 w-9 items-center justify-center rounded-full border border-console-line bg-console-surface text-console-muted hover:bg-slate-100 hover:text-console-ink"
        aria-label="Open user settings"
        aria-haspopup="menu"
        aria-expanded={open}
        onClick={() => setOpen((current) => !current)}
        onKeyDown={(event) => {
          if (event.key === 'Escape') setOpen(false);
        }}
      >
        <UserCircle className="h-5 w-5" aria-hidden />
      </button>
      {open ? (
        <div
          role="menu"
          className="absolute right-0 z-dropdown mt-2 w-64 overflow-hidden rounded-console border border-console-line bg-console-surface shadow-console"
        >
          <div className="border-b border-console-line px-4 py-3">
            <p className="text-sm font-semibold text-console-ink">{session?.displayName ?? 'Cassdio Operator'}</p>
            <p className="truncate text-xs text-console-muted">{session?.email ?? 'operator@cassdio.local'}</p>
          </div>
          <button className="menu-item" role="menuitem" type="button">
            <UserCircle className="h-4 w-4" aria-hidden />
            Profile
          </button>
          <button className="menu-item" role="menuitem" type="button">
            <Settings className="h-4 w-4" aria-hidden />
            Settings
          </button>
          <button className="menu-item" role="menuitem" type="button">
            <SlidersHorizontal className="h-4 w-4" aria-hidden />
            Preferences
          </button>
          <button className="menu-item border-t border-console-line text-console-danger" role="menuitem" type="button" onClick={logout}>
            <LogOut className="h-4 w-4" aria-hidden />
            Logout
          </button>
        </div>
      ) : null}
    </div>
  );
}
