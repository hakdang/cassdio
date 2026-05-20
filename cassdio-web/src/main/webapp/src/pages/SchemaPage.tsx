import { AlertTriangle, BookOpen, Database, FileCode2, History, KeyRound, RefreshCw, Search, Table2, Tags } from 'lucide-react';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { apiClient, getApiData } from '../utils/apiClient';

type Cluster = {
  clusterId: string;
  name: string;
  environment: string;
};

type Keyspace = {
  name: string;
  durableWrites: boolean;
  replication: Record<string, string>;
  system: boolean;
  queryable: boolean;
  tableCount: number;
  userTypeCount: number;
  describeCql: string;
};

type TableSummary = {
  keyspaceName: string;
  name: string;
  kind: string;
  comment?: string;
  catalog?: TableCatalog;
};

type TableDetail = TableSummary & {
  columns: Column[];
  indexes: Array<{ name: string; kind?: string; target?: string }>;
  views: Array<{ name: string; baseTableName: string; whereClause?: string }>;
  options: Record<string, string>;
  createStatement: string;
};

type Column = {
  name: string;
  type: string;
  kind: string;
  position: number;
  clusteringOrder?: string;
  catalog?: ColumnCatalog;
  userTypeLink?: { keyspaceName: string; typeName: string };
};

type TableCatalog = {
  owner?: string;
  escalationContact?: string;
  description?: string;
  dataFreshness?: string;
  retentionPolicy?: string;
  accessPattern?: string;
  tags: string[];
};

type ColumnCatalog = {
  description?: string;
  sensitive: boolean;
  maskingPolicy?: string;
  exportPolicy?: string;
  tags: string[];
};

type UserType = {
  keyspaceName: string;
  name: string;
  fieldCount: number;
};

type Change = {
  changeId: string;
  changeType: string;
  actor: string;
  tableName?: string;
  diff: string[];
  createdAt: string;
};

export function SchemaPage() {
  const [clusters, setClusters] = useState<Cluster[]>([]);
  const [clusterId, setClusterId] = useState('');
  const [keyspaces, setKeyspaces] = useState<Keyspace[]>([]);
  const [keyspaceName, setKeyspaceName] = useState('');
  const [tables, setTables] = useState<TableSummary[]>([]);
  const [tableName, setTableName] = useState('');
  const [tableDetail, setTableDetail] = useState<TableDetail | null>(null);
  const [types, setTypes] = useState<UserType[]>([]);
  const [history, setHistory] = useState<Change[]>([]);
  const [includeSystem, setIncludeSystem] = useState(false);
  const [filter, setFilter] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  const selectedCluster = useMemo(() => clusters.find((cluster) => cluster.clusterId === clusterId), [clusters, clusterId]);
  const selectedKeyspace = useMemo(() => keyspaces.find((keyspace) => keyspace.name === keyspaceName), [keyspaces, keyspaceName]);
  const filteredTables = useMemo(
    () => tables.filter((table) => table.name.toLowerCase().includes(filter.toLowerCase())),
    [tables, filter],
  );

  const loadClusters = useCallback(async () => {
    const data = await getApiData<Cluster[]>('/api/clusters');
    setClusters(data);
    setClusterId((current) => current || data[0]?.clusterId || '');
  }, []);

  const loadKeyspaces = useCallback(async (id: string, refresh = false) => {
    if (!id) return;
    setLoading(true);
    setError(null);
    try {
      const data = await getApiData<Keyspace[]>(`/api/clusters/${id}/schema/keyspaces`, { params: { includeSystem, refresh } });
      setKeyspaces(data);
      setKeyspaceName((current) => data.find((keyspace) => keyspace.name === current)?.name || data[0]?.name || '');
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : 'Failed to load keyspaces.');
    } finally {
      setLoading(false);
    }
  }, [includeSystem]);

  const loadTables = useCallback(async (id: string, ks: string) => {
    if (!id || !ks) return;
    setLoading(true);
    setError(null);
    try {
      const data = await getApiData<{ items: TableSummary[] }>(`/api/clusters/${id}/schema/keyspaces/${ks}/tables`, { params: { limit: 200 } });
      setTables(data.items);
      setTableName((current) => data.items.find((table) => table.name === current)?.name || data.items[0]?.name || '');
      const typeData = await getApiData<UserType[]>(`/api/clusters/${id}/schema/keyspaces/${ks}/types`);
      setTypes(typeData);
      const historyData = await getApiData<Change[]>(`/api/clusters/${id}/schema/keyspaces/${ks}/history`);
      setHistory(historyData);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : 'Failed to load tables.');
    } finally {
      setLoading(false);
    }
  }, []);

  const loadTableDetail = useCallback(async (id: string, ks: string, table: string) => {
    if (!id || !ks || !table) {
      setTableDetail(null);
      return;
    }
    setError(null);
    try {
      const data = await getApiData<TableDetail>(`/api/clusters/${id}/schema/keyspaces/${ks}/tables/${table}`);
      setTableDetail(data);
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : 'Failed to load table detail.');
    }
  }, []);

  useEffect(() => {
    loadClusters().catch((caught) => setError(caught instanceof Error ? caught.message : 'Failed to load clusters.'));
  }, [loadClusters]);

  useEffect(() => {
    loadKeyspaces(clusterId);
  }, [clusterId, includeSystem, loadKeyspaces]);

  useEffect(() => {
    loadTables(clusterId, keyspaceName);
  }, [clusterId, keyspaceName, loadTables]);

  useEffect(() => {
    loadTableDetail(clusterId, keyspaceName, tableName);
  }, [clusterId, keyspaceName, tableName, loadTableDetail]);

  async function saveTableCatalog() {
    if (!tableDetail) return;
    const owner = prompt('Owner', tableDetail.catalog?.owner || '') ?? tableDetail.catalog?.owner ?? '';
    const description = prompt('Description', tableDetail.catalog?.description || '') ?? tableDetail.catalog?.description ?? '';
    await apiClient.put(`/api/clusters/${clusterId}/schema/keyspaces/${keyspaceName}/tables/${tableName}/catalog`, {
      owner,
      description,
      tags: tableDetail.catalog?.tags || [],
    });
    setNotice('Table catalog saved.');
    await loadTableDetail(clusterId, keyspaceName, tableName);
  }

  async function markSensitive(column: Column) {
    await apiClient.put(`/api/clusters/${clusterId}/schema/keyspaces/${keyspaceName}/tables/${tableName}/columns/${column.name}/catalog`, {
      description: column.catalog?.description,
      sensitive: !column.catalog?.sensitive,
      maskingPolicy: column.catalog?.maskingPolicy,
      exportPolicy: column.catalog?.exportPolicy,
      tags: column.catalog?.tags || [],
    });
    setNotice(`${column.name} catalog updated.`);
    await loadTableDetail(clusterId, keyspaceName, tableName);
  }

  return (
    <section className="space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-semibold text-console-ink">Schema Explorer</h1>
          <p className="text-sm text-console-muted">Explore Cassandra schema, catalog metadata, UDTs, and schema change history.</p>
        </div>
        <div className="flex flex-wrap items-center gap-2">
          <label className="inline-flex h-9 items-center gap-2 rounded border border-console-line px-3 text-sm">
            <input checked={includeSystem} onChange={(event) => setIncludeSystem(event.target.checked)} type="checkbox" />
            System
          </label>
          <button className="inline-flex h-9 items-center gap-2 rounded border border-console-line px-3 text-sm hover:bg-slate-50" type="button" onClick={() => loadKeyspaces(clusterId, true)}>
            <RefreshCw className="h-4 w-4" aria-hidden />
            Refresh
          </button>
        </div>
      </div>

      {error ? <p className="rounded border border-red-200 bg-red-50 px-3 py-2 text-sm text-console-danger">{error}</p> : null}
      {notice ? <p className="rounded border border-emerald-200 bg-emerald-50 px-3 py-2 text-sm text-emerald-700">{notice}</p> : null}

      <div className="grid gap-4 xl:grid-cols-[260px_300px_minmax(0,1fr)]">
        <aside className="rounded-console border border-console-line bg-console-surface">
          <div className="border-b border-console-line p-3">
            <label className="text-xs font-semibold uppercase text-console-muted">Cluster</label>
            <select className="mt-2 h-9 w-full rounded border border-console-line px-2 text-sm" value={clusterId} onChange={(event) => setClusterId(event.target.value)}>
              {clusters.map((cluster) => (
                <option key={cluster.clusterId} value={cluster.clusterId}>
                  {cluster.name} ({cluster.environment})
                </option>
              ))}
            </select>
          </div>
          <div className="max-h-[680px] overflow-auto p-2">
            {keyspaces.map((keyspace) => (
              <button
                className={`mb-1 flex w-full items-center justify-between rounded px-3 py-2 text-left text-sm ${keyspace.name === keyspaceName ? 'bg-blue-50 text-console-accent' : 'hover:bg-slate-50'}`}
                key={keyspace.name}
                onClick={() => setKeyspaceName(keyspace.name)}
                type="button"
              >
                <span className="inline-flex min-w-0 items-center gap-2">
                  <KeyRound className="h-4 w-4 shrink-0" aria-hidden />
                  <span className="truncate">{keyspace.name}</span>
                </span>
                <span className="text-xs text-console-muted">{keyspace.tableCount}</span>
              </button>
            ))}
          </div>
        </aside>

        <aside className="rounded-console border border-console-line bg-console-surface">
          <div className="border-b border-console-line p-3">
            <div className="flex items-center gap-2 rounded border border-console-line px-2">
              <Search className="h-4 w-4 text-console-muted" aria-hidden />
              <input className="h-9 min-w-0 flex-1 text-sm outline-none" placeholder="Filter tables" value={filter} onChange={(event) => setFilter(event.target.value)} />
            </div>
          </div>
          <div className="max-h-[680px] overflow-auto p-2">
            {filteredTables.map((table) => (
              <button
                className={`mb-1 flex w-full items-center gap-2 rounded px-3 py-2 text-left text-sm ${table.name === tableName ? 'bg-blue-50 text-console-accent' : 'hover:bg-slate-50'}`}
                key={`${table.kind}:${table.name}`}
                onClick={() => setTableName(table.name)}
                type="button"
              >
                <Table2 className="h-4 w-4 shrink-0" aria-hidden />
                <span className="min-w-0 flex-1 truncate">{table.name}</span>
                <span className="rounded bg-slate-100 px-1.5 py-0.5 text-[11px] text-console-muted">{table.kind === 'TABLE' ? 'T' : 'MV'}</span>
              </button>
            ))}
          </div>
        </aside>

        <main className="space-y-4">
          <div className="rounded-console border border-console-line bg-console-surface p-4">
            <div className="flex flex-wrap items-start justify-between gap-3">
              <div>
                <p className="text-xs font-semibold uppercase text-console-muted">{selectedCluster?.name || 'Cluster'} / {selectedKeyspace?.name || 'Keyspace'}</p>
                <h2 className="mt-1 text-xl font-semibold text-console-ink">{tableDetail?.name || 'Select a table'}</h2>
                <p className="mt-1 text-sm text-console-muted">{tableDetail?.catalog?.description || selectedKeyspace?.describeCql || 'Schema metadata will appear here.'}</p>
              </div>
              {tableDetail ? (
                <button className="inline-flex h-9 items-center gap-2 rounded border border-console-line px-3 text-sm hover:bg-slate-50" type="button" onClick={saveTableCatalog}>
                  <Tags className="h-4 w-4" aria-hidden />
                  Catalog
                </button>
              ) : null}
            </div>
          </div>

          <div className="grid gap-4 lg:grid-cols-[minmax(0,1fr)_320px]">
            <div className="overflow-hidden rounded-console border border-console-line bg-console-surface">
              <div className="flex items-center gap-2 border-b border-console-line px-4 py-3">
                <Database className="h-4 w-4 text-console-muted" aria-hidden />
                <h3 className="text-sm font-semibold text-console-ink">Columns</h3>
              </div>
              <table className="w-full text-left text-sm">
                <thead className="bg-slate-50 text-xs uppercase text-console-muted">
                  <tr>
                    <th className="px-4 py-3">Column</th>
                    <th className="px-4 py-3">Type</th>
                    <th className="px-4 py-3">Kind</th>
                    <th className="px-4 py-3">Policy</th>
                  </tr>
                </thead>
                <tbody>
                  {tableDetail?.columns.map((column) => (
                    <tr className="border-t border-console-line" key={column.name}>
                      <td className="px-4 py-3 font-medium text-console-ink">{column.name}</td>
                      <td className="px-4 py-3 font-mono text-xs text-console-muted">{column.type}</td>
                      <td className="px-4 py-3">{column.kind.replace('_', ' ')}</td>
                      <td className="px-4 py-3">
                        <button className={`inline-flex h-7 items-center gap-1 rounded px-2 text-xs ${column.catalog?.sensitive ? 'bg-red-50 text-red-700' : 'bg-slate-100 text-console-muted'}`} type="button" onClick={() => markSensitive(column)}>
                          <AlertTriangle className="h-3.5 w-3.5" aria-hidden />
                          {column.catalog?.sensitive ? 'Sensitive' : 'Normal'}
                        </button>
                      </td>
                    </tr>
                  )) || (
                    <tr>
                      <td className="px-4 py-8 text-console-muted" colSpan={4}>{loading ? 'Loading schema...' : 'No table selected.'}</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>

            <div className="space-y-4">
              <section className="rounded-console border border-console-line bg-console-surface">
                <div className="flex items-center gap-2 border-b border-console-line px-4 py-3">
                  <BookOpen className="h-4 w-4 text-console-muted" aria-hidden />
                  <h3 className="text-sm font-semibold text-console-ink">UDT Types</h3>
                </div>
                <div className="max-h-48 overflow-auto p-3">
                  {types.length ? types.map((type) => (
                    <div className="mb-2 flex items-center justify-between rounded bg-slate-50 px-3 py-2 text-sm" key={type.name}>
                      <span>{type.name}</span>
                      <span className="text-xs text-console-muted">{type.fieldCount} fields</span>
                    </div>
                  )) : <p className="text-sm text-console-muted">No UDTs.</p>}
                </div>
              </section>

              <section className="rounded-console border border-console-line bg-console-surface">
                <div className="flex items-center gap-2 border-b border-console-line px-4 py-3">
                  <History className="h-4 w-4 text-console-muted" aria-hidden />
                  <h3 className="text-sm font-semibold text-console-ink">Change History</h3>
                </div>
                <div className="max-h-56 overflow-auto p-3">
                  {history.length ? history.slice(0, 8).map((change) => (
                    <div className="mb-3 border-b border-console-line pb-3 text-sm last:border-0" key={change.changeId}>
                      <div className="font-medium text-console-ink">{change.changeType}</div>
                      <div className="text-xs text-console-muted">{change.tableName || keyspaceName} · {new Date(change.createdAt).toLocaleString()}</div>
                    </div>
                  )) : <p className="text-sm text-console-muted">No schema changes recorded.</p>}
                </div>
              </section>
            </div>
          </div>

          <section className="rounded-console border border-console-line bg-console-surface">
            <div className="flex items-center gap-2 border-b border-console-line px-4 py-3">
              <FileCode2 className="h-4 w-4 text-console-muted" aria-hidden />
              <h3 className="text-sm font-semibold text-console-ink">Definition</h3>
            </div>
            <pre className="max-h-80 overflow-auto p-4 text-xs leading-6 text-console-ink">{tableDetail?.createStatement || selectedKeyspace?.describeCql || ''}</pre>
          </section>
        </main>
      </div>
    </section>
  );
}
