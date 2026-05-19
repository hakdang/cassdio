import { type FormEvent, useEffect, useState } from 'react';
import { apiClient, getApiData } from '../utils/apiClient';

type Role = {
  roleId: string;
  name: string;
  description?: string;
  systemRole: boolean;
  scopeType: string;
  permissions: string[];
};

export function AdminRolesPage() {
  const [roles, setRoles] = useState<Role[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [name, setName] = useState('');
  const [permission, setPermission] = useState('schema:read');

  useEffect(() => {
    getApiData<Role[]>('/api/roles')
      .then(setRoles)
      .catch((caught) => setError(caught instanceof Error ? caught.message : 'Failed to load roles.'));
  }, []);

  async function createRole(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const response = await apiClient.post('/api/roles', {
      name,
      scopeType: 'WORKSPACE',
      permissions: permission.split(',').map((value) => value.trim()).filter(Boolean),
    });
    setRoles((current) => [...current, response.data.data]);
    setName('');
  }

  return (
    <section className="space-y-4">
      <div>
        <h1 className="text-2xl font-semibold text-console-ink">Roles</h1>
        <p className="text-sm text-console-muted">System and custom roles with permission bindings.</p>
      </div>
      <form className="grid gap-3 rounded-console border border-console-line bg-console-surface p-4 md:grid-cols-[1fr_1fr_auto]" onSubmit={createRole}>
        <input className="h-10 rounded border border-console-line px-3 text-sm" placeholder="Role name" value={name} onChange={(event) => setName(event.target.value)} />
        <input
          className="h-10 rounded border border-console-line px-3 text-sm"
          placeholder="Permissions, comma separated"
          value={permission}
          onChange={(event) => setPermission(event.target.value)}
        />
        <button className="h-10 rounded bg-console-brand px-4 text-sm font-semibold text-white" type="submit">
          Create
        </button>
      </form>
      {error ? <p className="rounded border border-red-200 bg-red-50 px-3 py-2 text-sm text-console-danger">{error}</p> : null}
      <div className="grid gap-3 lg:grid-cols-2">
        {roles.map((role) => (
          <article className="rounded-console border border-console-line bg-console-surface p-4" key={role.roleId}>
            <div className="mb-3 flex items-start justify-between gap-3">
              <div>
                <h2 className="font-semibold text-console-ink">{role.name}</h2>
                <p className="text-sm text-console-muted">{role.description}</p>
              </div>
              <span className="rounded bg-slate-100 px-2 py-1 text-xs text-console-muted">{role.scopeType}</span>
            </div>
            <div className="flex flex-wrap gap-2">
              {role.permissions.map((permission) => (
                <span className="rounded border border-console-line px-2 py-1 text-xs text-console-ink" key={permission}>
                  {permission}
                </span>
              ))}
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}
