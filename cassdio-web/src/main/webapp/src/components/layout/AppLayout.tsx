import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { useHealth } from '../../hooks/useHealth';
import { Footer } from './Footer';
import { Header } from './Header';
import { Sidebar } from './Sidebar';

export function AppLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { data } = useHealth({ pollIntervalMs: 30000 });

  return (
    <div className="min-h-screen bg-console-bg text-console-ink">
      <Header onToggleSidebar={() => setSidebarOpen((current) => !current)} />
      <div className="lg:grid lg:grid-cols-[18rem_minmax(0,1fr)]">
        <Sidebar open={sidebarOpen} onClose={() => setSidebarOpen(false)} />
        <div className="flex min-h-[calc(100vh-3.5rem)] min-w-0 flex-col">
          <main className="min-w-0 flex-1">
            <Outlet />
          </main>
          <Footer health={data} />
        </div>
      </div>
    </div>
  );
}
