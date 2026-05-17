import {
  Activity,
  Boxes,
  Database,
  Gauge,
  History,
  KeyRound,
  Layers3,
  ListTree,
  MonitorCog,
  PlaySquare,
  Save,
  SearchCode,
  Settings,
  ShieldCheck,
  Table2,
  TerminalSquare,
  Users,
} from 'lucide-react';
import type { GnbKey, NavItem, SidebarSection } from '../types/navigation';

export const gnbItems: Array<NavItem & { key: GnbKey }> = [
  { key: 'dashboard', label: 'Dashboard', path: '/', icon: Gauge },
  { key: 'cluster', label: 'Cluster', path: '/cluster', icon: Boxes },
  { key: 'query', label: 'Query Workspace', path: '/query', icon: TerminalSquare },
  { key: 'schema', label: 'Schema', path: '/schema', icon: Database },
  { key: 'operations', label: 'Operations', path: '/operations', icon: Activity },
  { key: 'admin', label: 'Admin', path: '/admin', icon: ShieldCheck },
];

export const sidebarSections: Record<GnbKey, SidebarSection[]> = {
  dashboard: [
    {
      title: 'Overview',
      items: [
        { label: 'Workspace', path: '/', icon: Gauge },
        { label: 'Health', path: '/health', icon: Activity },
      ],
    },
  ],
  cluster: [
    {
      title: 'Clusters',
      items: [
        { label: 'Cluster List', path: '/cluster', icon: ListTree },
        { label: 'Cluster Detail', path: '/cluster/detail', icon: Boxes },
        { label: 'Health', path: '/cluster/health', icon: Activity },
      ],
    },
  ],
  query: [
    {
      title: 'Workspace',
      items: [
        { label: 'Query Editor', path: '/query', icon: PlaySquare },
        { label: 'Query History', path: '/query/history', icon: History },
        { label: 'Saved Queries', path: '/query/saved', icon: Save },
      ],
    },
  ],
  schema: [
    {
      title: 'Schema Browser',
      items: [
        { label: 'Keyspace', path: '/schema', icon: KeyRound },
        { label: 'Table', path: '/schema/table', icon: Table2 },
        { label: 'Indexes', path: '/schema/indexes', icon: SearchCode },
      ],
    },
  ],
  operations: [
    {
      title: 'Runtime',
      items: [
        { label: 'Jobs', path: '/operations', icon: Layers3 },
        { label: 'Monitoring', path: '/operations/monitoring', icon: MonitorCog },
      ],
    },
  ],
  admin: [
    {
      title: 'Administration',
      items: [
        { label: 'Users', path: '/admin', icon: Users },
        { label: 'Settings', path: '/admin/settings', icon: Settings },
      ],
    },
  ],
};

export function getActiveGnb(pathname: string): GnbKey {
  if (pathname.startsWith('/cluster')) return 'cluster';
  if (pathname.startsWith('/query')) return 'query';
  if (pathname.startsWith('/schema')) return 'schema';
  if (pathname.startsWith('/operations')) return 'operations';
  if (pathname.startsWith('/admin')) return 'admin';
  return 'dashboard';
}
