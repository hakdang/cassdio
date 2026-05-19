import type { LucideIcon } from 'lucide-react';

export type NavItem = {
  label: string;
  path: string;
  icon: LucideIcon;
};

export type SidebarSection = {
  title: string;
  items: Array<NavItem & { children?: NavItem[] }>;
};

export type GnbKey = 'dashboard' | 'cluster' | 'query' | 'schema' | 'operations' | 'admin';
