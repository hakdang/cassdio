# Cassdio Frontend Component Catalog

## Layout

- `AppLayout`: Console frame with GNB header, contextual LNB sidebar, content outlet, and status footer.
- `Header`: Project title, global navigation, responsive sidebar toggle, and user menu entry.
- `Sidebar`: GNB-aware local navigation with collapsible sections and scrollable content.
- `Footer`: Version, workspace, metadata Cassandra placeholder, and API status.

## Navigation

- `PageHeader`: Page title, description, breadcrumbs, and action slot.
- `Breadcrumb`: Compact route hierarchy with link support.

## States

- `EmptyState`: No-data state with optional action.
- `ErrorState`: Error state with optional code and retry action.
- `LoadingState`: Spinner row for async loading.
- `Skeleton`: Lightweight placeholder block for panels and lists.

## Style Convention

- Use Tailwind utility classes for component-local layout and visual styling.
- Prefer semantic tokens from `tailwind.config.ts` such as `console.bg`, `console.surface`, `console.line`, and `console.brand`.
- Keep shared class abstractions in `src/styles.css` under `@layer components` only when repeated across components.
- Use 8px or smaller border radius for console surfaces.
- Keep page sections full-width; reserve card styling for individual panels, repeated items, and modals.
