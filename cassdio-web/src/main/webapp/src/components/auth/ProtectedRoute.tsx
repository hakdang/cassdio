import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { authService } from '../../services/auth';

export function ProtectedRoute() {
  const location = useLocation();

  if (!authService.isAuthenticated()) {
    const redirectTo = encodeURIComponent(`${location.pathname}${location.search}`);
    return <Navigate replace to={`/login?redirect=${redirectTo}`} />;
  }

  return <Outlet />;
}
