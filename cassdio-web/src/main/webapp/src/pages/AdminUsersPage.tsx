import { RefreshCw } from 'lucide-react';
import { useCallback, useEffect, useState } from 'react';
import { apiClient, getApiData } from '../utils/apiClient';

type Member = {
  memberId: string;
  email: string;
  displayName: string;
  status: string;
  authProvider: string;
  mfaEnabled: boolean;
  locale: string;
  timezone: string;
  lastLoginAt?: string;
  createdAt: string;
};

export function AdminUsersPage() {
  const [members, setMembers] = useState<Member[]>([]);
  const [status, setStatus] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadMembers = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      setMembers(await getApiData<Member[]>(status ? `/api/members?status=${status}` : '/api/members'));
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : 'Failed to load members.');
    } finally {
      setLoading(false);
    }
  }, [status]);

  useEffect(() => {
    loadMembers();
  }, [loadMembers]);

  async function changeStatus(memberId: string, nextStatus: string) {
    await apiClient.patch(`/api/members/${memberId}/status`, { status: nextStatus });
    await loadMembers();
  }

  return (
    <section className="space-y-4">
      <div className="flex items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-semibold text-console-ink">Members</h1>
          <p className="text-sm text-console-muted">Cassdio operators and login state.</p>
        </div>
        <div className="flex items-center gap-2">
          <select className="h-9 rounded border border-console-line bg-white px-3 text-sm" value={status} onChange={(event) => setStatus(event.target.value)}>
            <option value="">All</option>
            <option value="PENDING">Pending</option>
            <option value="ACTIVE">Active</option>
            <option value="SUSPENDED">Suspended</option>
            <option value="DISABLED">Disabled</option>
          </select>
          <button className="inline-flex h-9 items-center gap-2 rounded border border-console-line px-3 text-sm hover:bg-slate-50" onClick={loadMembers} type="button">
            <RefreshCw className="h-4 w-4" aria-hidden />
            Refresh
          </button>
        </div>
      </div>
      {error ? <p className="rounded border border-red-200 bg-red-50 px-3 py-2 text-sm text-console-danger">{error}</p> : null}
      <div className="overflow-hidden rounded-console border border-console-line bg-console-surface">
        <table className="w-full text-left text-sm">
          <thead className="border-b border-console-line bg-slate-50 text-xs uppercase text-console-muted">
            <tr>
              <th className="px-4 py-3">Member</th>
              <th className="px-4 py-3">Status</th>
              <th className="px-4 py-3">Provider</th>
              <th className="px-4 py-3">Locale</th>
              <th className="px-4 py-3">Last Login</th>
              <th className="px-4 py-3">Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td className="px-4 py-6 text-console-muted" colSpan={6}>
                  Loading members...
                </td>
              </tr>
            ) : (
              members.map((member) => (
                <tr className="border-b border-console-line last:border-0" key={member.memberId}>
                  <td className="px-4 py-3">
                    <p className="font-medium text-console-ink">{member.displayName}</p>
                    <p className="text-xs text-console-muted">{member.email}</p>
                  </td>
                  <td className="px-4 py-3">{member.status}</td>
                  <td className="px-4 py-3">{member.authProvider}</td>
                  <td className="px-4 py-3">{member.locale}</td>
                  <td className="px-4 py-3">{member.lastLoginAt ?? '-'}</td>
                  <td className="px-4 py-3">
                    <div className="flex gap-2">
                      <button className="rounded border border-console-line px-2 py-1 text-xs" type="button" onClick={() => changeStatus(member.memberId, 'ACTIVE')}>
                        Activate
                      </button>
                      <button className="rounded border border-console-line px-2 py-1 text-xs" type="button" onClick={() => changeStatus(member.memberId, 'SUSPENDED')}>
                        Suspend
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}
