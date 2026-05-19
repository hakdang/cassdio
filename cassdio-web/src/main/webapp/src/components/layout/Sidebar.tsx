import { ChevronDown } from 'lucide-react';
import { useState } from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import { getActiveGnb, sidebarSections } from '../../config/navigation';

type SidebarProps = {
  open: boolean;
  onClose: () => void;
};

export function Sidebar({ open, onClose }: SidebarProps) {
  const { pathname } = useLocation();
  const activeGnb = getActiveGnb(pathname);
  const sections = sidebarSections[activeGnb];
  const [collapsed, setCollapsed] = useState<Record<string, boolean>>({});

  return (
    <>
      {open ? <button className="fixed inset-0 z-20 bg-slate-900/20 lg:hidden" aria-label="Close sidebar" onClick={onClose} /> : null}
      <aside
        className={`fixed bottom-0 left-0 top-14 z-sidebar w-72 border-r border-console-line bg-console-surface transition-transform lg:sticky lg:top-14 lg:h-[calc(100vh-3.5rem)] lg:translate-x-0 ${
          open ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <div className="flex h-full flex-col overflow-y-auto px-3 py-4">
          {sections.map((section) => {
            const isCollapsed = collapsed[section.title] ?? false;

            return (
              <section key={section.title} className="mb-5">
                <button
                  type="button"
                  className="mb-2 flex w-full items-center justify-between rounded px-2 py-1 text-xs font-semibold uppercase text-console-muted"
                  onClick={() => setCollapsed((current) => ({ ...current, [section.title]: !isCollapsed }))}
                >
                  {section.title}
                  <ChevronDown aria-hidden className={`h-4 w-4 transition ${isCollapsed ? '-rotate-90' : ''}`} />
                </button>
                {isCollapsed ? null : (
                  <div className="space-y-1">
                    {section.items.map((item) => {
                      const Icon = item.icon;

                      return (
                        <NavLink
                          key={item.path}
                          to={item.path}
                          onClick={onClose}
                          className={({ isActive }) =>
                            `flex items-center gap-2 rounded px-3 py-2 text-sm font-medium ${
                              isActive || pathname === item.path
                                ? 'bg-blue-50 text-console-brand'
                                : 'text-console-muted hover:bg-slate-100 hover:text-console-ink'
                            }`
                          }
                        >
                          <Icon aria-hidden className="h-4 w-4" />
                          {item.label}
                        </NavLink>
                      );
                    })}
                  </div>
                )}
              </section>
            );
          })}
        </div>
      </aside>
    </>
  );
}
