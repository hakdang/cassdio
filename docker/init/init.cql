CREATE KEYSPACE IF NOT EXISTS cassdio_meta
WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor': 1
};

CREATE TABLE IF NOT EXISTS cassdio_meta.schema_migrations (
  version TEXT PRIMARY KEY,
  description TEXT,
  executed_at TIMESTAMP,
  execution_time INT,
  success BOOLEAN
);

CREATE TABLE IF NOT EXISTS cassdio_meta.bootstrap_locks (
  name TEXT PRIMARY KEY,
  owner TEXT,
  status TEXT,
  heartbeat_at TIMESTAMP,
  expires_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cassdio_meta.installation_state (
  id TEXT PRIMARY KEY,
  installation_id UUID,
  bootstrap_completed_at TIMESTAMP,
  schema_version TEXT,
  cassdio_version TEXT,
  initial_settings MAP<TEXT, TEXT>,
  updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cassdio_meta.seed_history (
  idempotency_key TEXT PRIMARY KEY,
  description TEXT,
  executed_at TIMESTAMP,
  success BOOLEAN
);

CREATE TABLE IF NOT EXISTS cassdio_meta.workspaces (
  workspace_id UUID PRIMARY KEY,
  name TEXT,
  description TEXT,
  locale TEXT,
  timezone TEXT,
  signup_mode TEXT,
  owner_member_id UUID,
  status TEXT,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cassdio_meta.members (
  member_id UUID PRIMARY KEY,
  workspace_id UUID,
  display_name TEXT,
  email TEXT,
  email_verified BOOLEAN,
  password_hash TEXT,
  password_algorithm TEXT,
  mfa_status TEXT,
  locale TEXT,
  timezone TEXT,
  last_login_at TIMESTAMP,
  password_changed_at TIMESTAMP,
  status TEXT,
  auth_provider TEXT,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cassdio_meta.roles (
  role_id UUID PRIMARY KEY,
  name TEXT,
  description TEXT,
  system_role BOOLEAN,
  scope_type TEXT,
  permissions SET<TEXT>,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cassdio_meta.role_assignments (
  assignment_id UUID PRIMARY KEY,
  member_id UUID,
  role_id UUID,
  scope_type TEXT,
  scope_id TEXT,
  approval_source TEXT,
  granted_by TEXT,
  granted_at TIMESTAMP,
  effective_from TIMESTAMP,
  expires_at TIMESTAMP,
  status TEXT
);

CREATE TABLE IF NOT EXISTS cassdio_meta.query_policies (
  policy_id UUID PRIMARY KEY,
  name TEXT,
  query_type TEXT,
  description TEXT,
  rules MAP<TEXT, TEXT>,
  examples LIST<TEXT>,
  enabled BOOLEAN,
  priority INT,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cassdio_meta.workflow_policies (
  policy_id UUID PRIMARY KEY,
  name TEXT,
  workflow_type TEXT,
  description TEXT,
  approval_steps LIST<TEXT>,
  priority INT,
  enabled BOOLEAN,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cassdio_meta.audit_logs (
  event_id UUID PRIMARY KEY,
  event_type TEXT,
  actor TEXT,
  target_type TEXT,
  target_id TEXT,
  details MAP<TEXT, TEXT>,
  created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cassdio_meta.managed_clusters (
  cluster_id UUID PRIMARY KEY,
  name TEXT,
  environment TEXT,
  contact_points LIST<TEXT>,
  port INT,
  local_datacenter TEXT,
  cassandra_version TEXT,
  keyspace_count INT,
  table_count INT,
  owner_member_id UUID,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  status TEXT
);

CREATE TABLE IF NOT EXISTS cassdio_meta.managed_clusters_by_name (
  name TEXT PRIMARY KEY,
  cluster_id UUID,
  environment TEXT,
  status TEXT,
  updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cassdio_meta.managed_cluster_credentials (
  cluster_id UUID PRIMARY KEY,
  username_ciphertext TEXT,
  password_ciphertext TEXT,
  secret_reference_ciphertext TEXT,
  tls_enabled BOOLEAN,
  ssl_settings_ciphertext TEXT,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cassdio_meta.managed_cluster_health_snapshots (
  cluster_id UUID,
  snapshot_id UUID,
  status TEXT,
  node_count INT,
  up_node_count INT,
  schema_agreement BOOLEAN,
  keyspace_count INT,
  table_count INT,
  pending_compactions INT,
  repairs_status TEXT,
  checked_at TIMESTAMP,
  PRIMARY KEY (cluster_id, snapshot_id)
);

CREATE TABLE IF NOT EXISTS cassdio_meta.workspace_members (
  workspace_id UUID,
  member_id UUID,
  membership_status TEXT,
  default_workspace BOOLEAN,
  joined_at TIMESTAMP,
  invited_by UUID,
  last_selected_at TIMESTAMP,
  PRIMARY KEY (workspace_id, member_id)
);

CREATE TABLE IF NOT EXISTS cassdio_meta.member_sessions (
  session_id UUID PRIMARY KEY,
  member_id UUID,
  refresh_token_hash TEXT,
  token_family_id UUID,
  previous_token_hash TEXT,
  device_name TEXT,
  ip_address TEXT,
  user_agent_hash TEXT,
  status TEXT,
  expires_at TIMESTAMP,
  rotated_at TIMESTAMP,
  revoked_at TIMESTAMP,
  created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cassdio_meta.permission_bindings (
  binding_id UUID PRIMARY KEY,
  role_id UUID,
  action TEXT,
  resource_pattern TEXT,
  effect TEXT,
  condition TEXT,
  expires_at TIMESTAMP,
  created_at TIMESTAMP
);

INSERT INTO cassdio_meta.workspaces
(workspace_id, name, description, locale, timezone, signup_mode, owner_member_id, status, created_at, updated_at)
VALUES (
  00000000-0000-2003-8000-000000000001,
  'Default',
  'Default Cassdio workspace',
  'ko-KR',
  'Asia/Seoul',
  'MANUAL_APPROVAL',
  00000000-0000-2003-8000-000000000002,
  'ACTIVE',
  toTimestamp(now()),
  toTimestamp(now())
) IF NOT EXISTS;

INSERT INTO cassdio_meta.members
(member_id, workspace_id, display_name, email, email_verified, password_hash, password_algorithm, mfa_status, locale, timezone, last_login_at, password_changed_at, status, auth_provider, created_at, updated_at)
VALUES (
  00000000-0000-2003-8000-000000000002,
  00000000-0000-2003-8000-000000000001,
  'Super Admin',
  'admin@cassdio.local',
  true,
  '$2a$10$bTBNIalVlzP2LaqCw7gQfe0tSTVSk/aJIj4loTnCta.jeT1mdVpa.',
  'BCRYPT',
  'DISABLED',
  'ko-KR',
  'Asia/Seoul',
  null,
  toTimestamp(now()),
  'ACTIVE',
  'LOCAL',
  toTimestamp(now()),
  toTimestamp(now())
) IF NOT EXISTS;

INSERT INTO cassdio_meta.workspace_members
(workspace_id, member_id, membership_status, default_workspace, joined_at, invited_by, last_selected_at)
VALUES (
  00000000-0000-2003-8000-000000000001,
  00000000-0000-2003-8000-000000000002,
  'ACTIVE',
  true,
  toTimestamp(now()),
  null,
  toTimestamp(now())
) IF NOT EXISTS;

INSERT INTO cassdio_meta.roles
(role_id, name, description, system_role, scope_type, permissions, created_at, updated_at)
VALUES (
  00000000-0000-2003-8000-000000000101,
  'Super Admin',
  'Full application administration access.',
  true,
  'APPLICATION',
  {'*'},
  toTimestamp(now()),
  toTimestamp(now())
) IF NOT EXISTS;

INSERT INTO cassdio_meta.roles
(role_id, name, description, system_role, scope_type, permissions, created_at, updated_at)
VALUES (
  00000000-0000-2003-8000-000000000102,
  'DBA',
  'Cluster, schema, and DDL administration.',
  true,
  'WORKSPACE',
  {'cluster:*', 'schema:*', 'query:ddl:*', 'workflow:review:dba'},
  toTimestamp(now()),
  toTimestamp(now())
) IF NOT EXISTS;

INSERT INTO cassdio_meta.roles
(role_id, name, description, system_role, scope_type, permissions, created_at, updated_at)
VALUES (
  00000000-0000-2003-8000-000000000103,
  'Security Admin',
  'Security policy, masking, and permission administration.',
  true,
  'WORKSPACE',
  {'security:*', 'policy:*', 'permission:*', 'workflow:review:security'},
  toTimestamp(now()),
  toTimestamp(now())
) IF NOT EXISTS;

INSERT INTO cassdio_meta.roles
(role_id, name, description, system_role, scope_type, permissions, created_at, updated_at)
VALUES (
  00000000-0000-2003-8000-000000000104,
  'Data Reader',
  'Read-only data access through approved SELECT queries.',
  true,
  'WORKSPACE',
  {'query:select', 'schema:read', 'catalog:read'},
  toTimestamp(now()),
  toTimestamp(now())
) IF NOT EXISTS;

INSERT INTO cassdio_meta.roles
(role_id, name, description, system_role, scope_type, permissions, created_at, updated_at)
VALUES (
  00000000-0000-2003-8000-000000000105,
  'Data Analyst',
  'Analyst access with limited DML request capability.',
  true,
  'WORKSPACE',
  {'query:select', 'query:dml:request', 'export:request', 'schema:read', 'catalog:read'},
  toTimestamp(now()),
  toTimestamp(now())
) IF NOT EXISTS;

INSERT INTO cassdio_meta.role_assignments
(assignment_id, member_id, role_id, scope_type, scope_id, approval_source, granted_by, granted_at, effective_from, expires_at, status)
VALUES (
  00000000-0000-2003-8000-000000000201,
  00000000-0000-2003-8000-000000000002,
  00000000-0000-2003-8000-000000000101,
  'APPLICATION',
  'cassdio',
  'BOOTSTRAP',
  'System',
  toTimestamp(now()),
  toTimestamp(now()),
  null,
  'ACTIVE'
) IF NOT EXISTS;

INSERT INTO cassdio_meta.audit_logs
(event_id, event_type, actor, target_type, target_id, details, created_at)
VALUES (
  00000000-0000-2003-8000-000000000503,
  'SUPER_ADMIN_CREATED',
  'System',
  'member',
  '00000000-0000-2003-8000-000000000002',
  {'phase': '2', 'milestone': '3', 'source': 'docker-init'},
  toTimestamp(now())
) IF NOT EXISTS;

INSERT INTO cassdio_meta.seed_history
(idempotency_key, description, executed_at, success)
VALUES (
  'docker-init-super-admin',
  'Create default workspace, super admin, roles, and assignment',
  toTimestamp(now()),
  true
) IF NOT EXISTS;
