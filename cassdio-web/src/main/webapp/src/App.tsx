import { Navigate, Route, Routes } from 'react-router-dom';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { AppLayout } from './components/layout/AppLayout';
import { ConsolePage } from './pages/ConsolePage';
import { AdminRolesPage } from './pages/AdminRolesPage';
import { AdminUsersPage } from './pages/AdminUsersPage';
import { HealthPage } from './pages/HealthPage';
import { LoginPage } from './pages/LoginPage';

export function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route
            index
            element={
              <ConsolePage
                section="Dashboard"
                title="Workspace"
                description="A compact overview for the current Cassandra workspace."
              />
            }
          />
          <Route path="/health" element={<HealthPage />} />
          <Route
            path="/cluster/*"
            element={<ConsolePage section="Cluster" title="Cluster List" description="Browse Cassandra clusters and their health state." />}
          />
          <Route
            path="/query/*"
            element={
              <ConsolePage section="Query Workspace" title="Query Editor" description="Prepare and execute CQL queries from the shared workspace." />
            }
          />
          <Route
            path="/schema/*"
            element={<ConsolePage section="Schema" title="Keyspace" description="Inspect keyspaces, tables, indexes, and schema metadata." />}
          />
          <Route
            path="/operations/*"
            element={<ConsolePage section="Operations" title="Jobs" description="Track operational jobs and runtime activity." />}
          />
          <Route
            path="/admin/*"
            element={<AdminUsersPage />}
          />
          <Route path="/admin/roles" element={<AdminRolesPage />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate replace to="/" />} />
    </Routes>
  );
}
