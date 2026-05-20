import { Activity, Boxes, CheckCircle2, Pencil, PlugZap, RefreshCw, RotateCcw, Save, ShieldCheck, Trash2, XCircle } from 'lucide-react';
import { useCallback, useEffect, useMemo, useState } from 'react';
import type { ReactNode } from 'react';
import { apiClient, getApiData } from '../utils/apiClient';

type Cluster = {
  clusterId: string;
  name: string;
  environment: string;
  contactPoints: string[];
  port: number;
  localDatacenter: string;
  cassandraVersion: string;
  keyspaceCount: number;
  tableCount: number;
  ownerMemberId: string;
  status: string;
  updatedAt: string;
  credential?: {
    usernameConfigured: boolean;
    passwordConfigured: boolean;
    secretReferenceConfigured: boolean;
    tlsEnabled: boolean;
    updatedAt: string;
  };
  health?: {
    status: string;
    nodeCount: number;
    upNodeCount: number;
    schemaAgreement: boolean;
    keyspaceCount: number;
    tableCount: number;
    pendingCompactions?: number;
    repairsStatus: string;
    checkedAt: string;
  };
  session: {
    status: string;
    openedAt?: string;
    lastUsedAt?: string;
    lastClearedAt?: string;
    lastError?: string;
  };
  lastConnectionError?: string;
};

type ConnectionTest = {
  success: boolean;
  cassandraVersion?: string;
  keyspaceCount?: number;
  tableCount?: number;
  failureCode?: string;
  checks: Array<{ name: string; success: boolean; message: string }>;
};

type ClusterForm = {
  name: string;
  environment: string;
  contactPoints: string;
  port: string;
  localDatacenter: string;
  username: string;
  password: string;
  secretReference: string;
  tlsEnabled: boolean;
  ownerMemberId: string;
  rotateCredential: boolean;
};

const emptyForm: ClusterForm = {
  name: '',
  environment: 'DEV',
  contactPoints: '127.0.0.1',
  port: '9042',
  localDatacenter: 'datacenter1',
  username: '',
  password: '',
  secretReference: '',
  tlsEnabled: false,
  ownerMemberId: '',
  rotateCredential: false,
};

export function ClusterPage() {
  const [clusters, setClusters] = useState<Cluster[]>([]);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [form, setForm] = useState<ClusterForm>(emptyForm);
  const [editing, setEditing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [testResult, setTestResult] = useState<ConnectionTest | null>(null);

  const selected = useMemo(() => clusters.find((cluster) => cluster.clusterId === selectedId) ?? clusters[0], [clusters, selectedId]);

  const loadClusters = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getApiData<Cluster[]>('/api/clusters');
      setClusters(data);
      setSelectedId((current) => current ?? data[0]?.clusterId ?? null);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : 'Failed to load clusters.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadClusters();
  }, [loadClusters]);

  useEffect(() => {
    if (!selected || editing) return;
    setForm(fromCluster(selected));
  }, [selected, editing]);

  function startCreate() {
    setEditing(true);
    setSelectedId(null);
    setForm(emptyForm);
    setTestResult(null);
    setNotice(null);
  }

  function startEdit(cluster: Cluster) {
    setEditing(true);
    setSelectedId(cluster.clusterId);
    setForm(fromCluster(cluster));
    setTestResult(null);
    setNotice(null);
  }

  async function testConnection() {
    setError(null);
    setNotice(null);
    setTestResult(null);
    try {
      const response = await apiClient.post('/api/clusters/test', toCreatePayload(form));
      setTestResult(response.data.data);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : 'Connection test failed.');
    }
  }

  async function saveCluster() {
    setSaving(true);
    setError(null);
    try {
      if (selectedId) {
        await apiClient.put(`/api/clusters/${selectedId}`, toUpdatePayload(form));
        setNotice('Cluster updated.');
      } else {
        const response = await apiClient.post('/api/clusters', toCreatePayload(form));
        setSelectedId(response.data.data.clusterId);
        setNotice('Cluster registered.');
      }
      setEditing(false);
      await loadClusters();
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : 'Failed to save cluster.');
    } finally {
      setSaving(false);
    }
  }

  async function clearSession(clusterId: string) {
    await apiClient.post(`/api/clusters/${clusterId}/session/clear`);
    setNotice('Cluster session cleared.');
    await loadClusters();
  }

  async function clearAllSessions() {
    await apiClient.post('/api/clusters/session/clear');
    setNotice('All cluster sessions cleared.');
    await loadClusters();
  }

  async function deleteCluster(clusterId: string) {
    await apiClient.delete(`/api/clusters/${clusterId}`);
    setNotice('Cluster deleted.');
    setSelectedId(null);
    await loadClusters();
  }

  return (
    <section className="space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-semibold text-console-ink">Clusters</h1>
          <p className="text-sm text-console-muted">Managed Cassandra clusters, credentials, sessions, and health.</p>
        </div>
        <div className="flex items-center gap-2">
          <button className="inline-flex h-9 items-center gap-2 rounded border border-console-line px-3 text-sm hover:bg-slate-50" type="button" onClick={clearAllSessions}>
            <RotateCcw className="h-4 w-4" aria-hidden />
            Clear Sessions
          </button>
          <button className="inline-flex h-9 items-center gap-2 rounded border border-console-line px-3 text-sm hover:bg-slate-50" type="button" onClick={loadClusters}>
            <RefreshCw className="h-4 w-4" aria-hidden />
            Refresh
          </button>
          <button className="inline-flex h-9 items-center gap-2 rounded bg-console-accent px-3 text-sm text-white hover:bg-blue-700" type="button" onClick={startCreate}>
            <Boxes className="h-4 w-4" aria-hidden />
            Register
          </button>
        </div>
      </div>

      {error ? <p className="rounded border border-red-200 bg-red-50 px-3 py-2 text-sm text-console-danger">{error}</p> : null}
      {notice ? <p className="rounded border border-emerald-200 bg-emerald-50 px-3 py-2 text-sm text-emerald-700">{notice}</p> : null}

      <div className="grid gap-4 lg:grid-cols-[minmax(0,1.05fr)_minmax(360px,0.95fr)]">
        <div className="overflow-hidden rounded-console border border-console-line bg-console-surface">
          <table className="w-full text-left text-sm">
            <thead className="border-b border-console-line bg-slate-50 text-xs uppercase text-console-muted">
              <tr>
                <th className="px-4 py-3">Cluster</th>
                <th className="px-4 py-3">Health</th>
                <th className="px-4 py-3">Session</th>
                <th className="px-4 py-3">Version</th>
                <th className="px-4 py-3">Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td className="px-4 py-6 text-console-muted" colSpan={5}>
                    Loading clusters...
                  </td>
                </tr>
              ) : clusters.length === 0 ? (
                <tr>
                  <td className="px-4 py-6 text-console-muted" colSpan={5}>
                    No clusters registered.
                  </td>
                </tr>
              ) : (
                clusters.map((cluster) => (
                  <tr className="border-b border-console-line last:border-0" key={cluster.clusterId}>
                    <td className="px-4 py-3">
                      <button className="text-left" type="button" onClick={() => setSelectedId(cluster.clusterId)}>
                        <p className="font-medium text-console-ink">{cluster.name}</p>
                        <p className="text-xs text-console-muted">{cluster.environment} · {cluster.contactPoints.join(', ')}:{cluster.port}</p>
                      </button>
                    </td>
                    <td className="px-4 py-3">{statusBadge(cluster.health?.status ?? 'UNKNOWN')}</td>
                    <td className="px-4 py-3">{statusBadge(cluster.session.status)}</td>
                    <td className="px-4 py-3">{cluster.cassandraVersion}</td>
                    <td className="px-4 py-3">
                      <div className="flex gap-2">
                        <IconButton label="Edit cluster" onClick={() => startEdit(cluster)}>
                          <Pencil className="h-4 w-4" aria-hidden />
                        </IconButton>
                        <IconButton label="Clear session" onClick={() => clearSession(cluster.clusterId)}>
                          <RotateCcw className="h-4 w-4" aria-hidden />
                        </IconButton>
                        <IconButton label="Delete cluster" onClick={() => deleteCluster(cluster.clusterId)}>
                          <Trash2 className="h-4 w-4" aria-hidden />
                        </IconButton>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        <aside className="space-y-4">
          {editing ? (
            <ClusterFormPanel
              form={form}
              saving={saving}
              testResult={testResult}
              editingExisting={Boolean(selectedId)}
              onCancel={() => {
                setEditing(false);
                setTestResult(null);
              }}
              onChange={setForm}
              onSave={saveCluster}
              onTest={testConnection}
            />
          ) : selected ? (
            <ClusterDetail cluster={selected} onEdit={() => startEdit(selected)} onClearSession={() => clearSession(selected.clusterId)} />
          ) : (
            <div className="rounded-console border border-console-line bg-console-surface p-4 text-sm text-console-muted">Select or register a cluster.</div>
          )}
        </aside>
      </div>
    </section>
  );
}

function ClusterDetail({ cluster, onEdit, onClearSession }: { cluster: Cluster; onEdit: () => void; onClearSession: () => void }) {
  return (
    <div className="rounded-console border border-console-line bg-console-surface p-4">
      <div className="flex items-start justify-between gap-3">
        <div>
          <h2 className="text-lg font-semibold text-console-ink">{cluster.name}</h2>
          <p className="text-sm text-console-muted">{cluster.environment} · owner {cluster.ownerMemberId}</p>
        </div>
        <div className="flex gap-2">
          <IconButton label="Edit cluster" onClick={onEdit}>
            <Pencil className="h-4 w-4" aria-hidden />
          </IconButton>
          <IconButton label="Clear session" onClick={onClearSession}>
            <RotateCcw className="h-4 w-4" aria-hidden />
          </IconButton>
        </div>
      </div>
      <div className="mt-4 grid gap-3 sm:grid-cols-2">
        <Metric icon={<PlugZap className="h-4 w-4" />} label="Session" value={cluster.session.status} />
        <Metric icon={<Activity className="h-4 w-4" />} label="Health" value={cluster.health?.status ?? 'UNKNOWN'} />
        <Metric icon={<ShieldCheck className="h-4 w-4" />} label="Credential" value={cluster.credential?.secretReferenceConfigured ? 'SECRET REF' : cluster.credential?.usernameConfigured ? 'AUTH' : 'NONE'} />
        <Metric icon={<Boxes className="h-4 w-4" />} label="Schema" value={`${cluster.keyspaceCount} keyspaces / ${cluster.tableCount} tables`} />
      </div>
      <dl className="mt-4 space-y-2 text-sm">
        <Info label="Contact points" value={`${cluster.contactPoints.join(', ')}:${cluster.port}`} />
        <Info label="Datacenter" value={cluster.localDatacenter} />
        <Info label="Cassandra" value={cluster.cassandraVersion} />
        <Info label="TLS" value={cluster.credential?.tlsEnabled ? 'Enabled' : 'Disabled'} />
        <Info label="Schema agreement" value={cluster.health?.schemaAgreement ? 'Agreed' : 'Unknown'} />
        <Info label="Updated" value={cluster.updatedAt} />
      </dl>
    </div>
  );
}

function ClusterFormPanel(props: {
  form: ClusterForm;
  saving: boolean;
  testResult: ConnectionTest | null;
  editingExisting: boolean;
  onChange: (form: ClusterForm) => void;
  onSave: () => void;
  onTest: () => void;
  onCancel: () => void;
}) {
  const { form, saving, testResult, editingExisting, onChange, onSave, onTest, onCancel } = props;
  const set = (patch: Partial<ClusterForm>) => onChange({ ...form, ...patch });

  return (
    <div className="rounded-console border border-console-line bg-console-surface p-4">
      <div className="flex items-center justify-between gap-3">
        <h2 className="text-lg font-semibold text-console-ink">{editingExisting ? 'Edit Cluster' : 'Register Cluster'}</h2>
        <button className="text-sm text-console-muted hover:text-console-ink" type="button" onClick={onCancel}>Cancel</button>
      </div>
      <div className="mt-4 grid gap-3 sm:grid-cols-2">
        <Field label="Name" value={form.name} onChange={(value) => set({ name: value })} />
        <label className="space-y-1 text-sm">
          <span className="text-console-muted">Environment</span>
          <select className="h-9 w-full rounded border border-console-line bg-white px-3" value={form.environment} onChange={(event) => set({ environment: event.target.value })}>
            {['DEV', 'STAGING', 'PROD', 'DR', 'SANDBOX'].map((environment) => <option key={environment}>{environment}</option>)}
          </select>
        </label>
        <Field label="Contact points" value={form.contactPoints} onChange={(value) => set({ contactPoints: value })} />
        <Field label="Port" value={form.port} onChange={(value) => set({ port: value })} />
        <Field label="Local datacenter" value={form.localDatacenter} onChange={(value) => set({ localDatacenter: value })} />
        <Field label="Owner member ID" value={form.ownerMemberId} onChange={(value) => set({ ownerMemberId: value })} />
        <Field label="Username" value={form.username} onChange={(value) => set({ username: value })} />
        <Field label="Password" type="password" value={form.password} onChange={(value) => set({ password: value })} />
        <Field label="Secret reference" value={form.secretReference} onChange={(value) => set({ secretReference: value })} />
        <label className="flex items-center gap-2 pt-7 text-sm text-console-ink">
          <input checked={form.tlsEnabled} type="checkbox" onChange={(event) => set({ tlsEnabled: event.target.checked })} />
          TLS
        </label>
        {editingExisting ? (
          <label className="flex items-center gap-2 text-sm text-console-ink">
            <input checked={form.rotateCredential} type="checkbox" onChange={(event) => set({ rotateCredential: event.target.checked })} />
            Rotate credential
          </label>
        ) : null}
      </div>
      <div className="mt-4 flex gap-2">
        <button className="inline-flex h-9 items-center gap-2 rounded border border-console-line px-3 text-sm hover:bg-slate-50" type="button" onClick={onTest}>
          <PlugZap className="h-4 w-4" aria-hidden />
          Test
        </button>
        <button className="inline-flex h-9 items-center gap-2 rounded bg-console-accent px-3 text-sm text-white hover:bg-blue-700 disabled:opacity-60" disabled={saving} type="button" onClick={onSave}>
          <Save className="h-4 w-4" aria-hidden />
          Save
        </button>
      </div>
      {testResult ? (
        <div className="mt-4 rounded border border-console-line bg-slate-50 p-3 text-sm">
          <p className="font-medium text-console-ink">{testResult.success ? 'Connection ready' : `Connection failed ${testResult.failureCode ? `(${testResult.failureCode})` : ''}`}</p>
          <div className="mt-2 space-y-1">
            {testResult.checks.map((check) => (
              <p className="flex items-center gap-2" key={check.name}>
                {check.success ? <CheckCircle2 className="h-4 w-4 text-emerald-600" /> : <XCircle className="h-4 w-4 text-console-danger" />}
                <span>{check.name}: {check.message}</span>
              </p>
            ))}
          </div>
        </div>
      ) : null}
    </div>
  );
}

function Field({ label, value, onChange, type = 'text' }: { label: string; value: string; type?: string; onChange: (value: string) => void }) {
  return (
    <label className="space-y-1 text-sm">
      <span className="text-console-muted">{label}</span>
      <input className="h-9 w-full rounded border border-console-line px-3" type={type} value={value} onChange={(event) => onChange(event.target.value)} />
    </label>
  );
}

function IconButton({ label, onClick, children }: { label: string; onClick: () => void; children: ReactNode }) {
  return (
    <button aria-label={label} className="inline-flex h-8 w-8 items-center justify-center rounded border border-console-line text-console-muted hover:bg-slate-50 hover:text-console-ink" title={label} type="button" onClick={onClick}>
      {children}
    </button>
  );
}

function Metric({ icon, label, value }: { icon: ReactNode; label: string; value: string }) {
  return (
    <div className="rounded border border-console-line p-3">
      <p className="flex items-center gap-2 text-xs uppercase text-console-muted">{icon}{label}</p>
      <p className="mt-1 break-words text-sm font-semibold text-console-ink">{value}</p>
    </div>
  );
}

function Info({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex justify-between gap-4 border-b border-console-line pb-2 last:border-0">
      <dt className="text-console-muted">{label}</dt>
      <dd className="min-w-0 break-words text-right text-console-ink">{value}</dd>
    </div>
  );
}

function statusBadge(status: string) {
  const tone = status === 'HEALTHY' || status === 'CONNECTED' ? 'border-emerald-200 bg-emerald-50 text-emerald-700' : status === 'DEGRADED' ? 'border-amber-200 bg-amber-50 text-amber-700' : 'border-slate-200 bg-slate-50 text-console-muted';
  return <span className={`inline-flex rounded border px-2 py-1 text-xs font-medium ${tone}`}>{status}</span>;
}

function fromCluster(cluster: Cluster): ClusterForm {
  return {
    name: cluster.name,
    environment: cluster.environment,
    contactPoints: cluster.contactPoints.join(', '),
    port: String(cluster.port),
    localDatacenter: cluster.localDatacenter,
    username: '',
    password: '',
    secretReference: '',
    tlsEnabled: Boolean(cluster.credential?.tlsEnabled),
    ownerMemberId: cluster.ownerMemberId,
    rotateCredential: false,
  };
}

function toCreatePayload(form: ClusterForm) {
  return {
    name: form.name,
    environment: form.environment,
    contactPoints: form.contactPoints.split(',').map((point) => point.trim()).filter(Boolean),
    port: Number(form.port),
    localDatacenter: form.localDatacenter,
    username: form.username || null,
    password: form.password || null,
    secretReference: form.secretReference || null,
    tlsEnabled: form.tlsEnabled,
    ownerMemberId: form.ownerMemberId || null,
    grantDbaToSuperAdmin: false,
  };
}

function toUpdatePayload(form: ClusterForm) {
  return {
    ...toCreatePayload(form),
    rotateCredential: form.rotateCredential,
  };
}
