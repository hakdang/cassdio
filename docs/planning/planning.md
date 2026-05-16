# Cassdio v2 Planning

## 프로젝트 목표

Cassdio v2는 단순 Cassandra Web UI가 아니라, Cassandra 클러스터를 안전하게 조회, 탐색, 쿼리 실행, 모니터링, 감사할 수 있는 self-hosted Database Governance & Operations Console을 목표로 한다.

핵심 설계 축은 다음과 같다.

| 축 | 설명 |
|---|---|
| Cluster Management | 관리 대상 Cassandra 클러스터 등록, 연결, session, 운영 정보 관리 |
| Schema & Data Explorer | keyspace/table/column/schema/data를 안전하게 탐색 |
| Query Governance | CQL 해석, 권한 판단, 정책 검수, 승인 후 실행 |
| Workflow & Approval | 가입, 권한 요청, 위험 쿼리, DDL, export/import 승인 공통화 |
| Audit & Monitoring | 모든 행위 이력화, Cassandra 운영 지표 수집, alert/incident 연결 |

## 개발 원칙

1. Cassdio 자체 metadata는 `cassdio_meta` keyspace에 저장하고, 관리 대상 Cassandra와 분리 가능하게 설계한다.
2. 사용자는 Cassdio에 로그인하고, access token과 refresh token 기반 session을 유지한다.
3. Cassdio 권한/정책 엔진은 로그인한 Member의 workspace membership, role, permission binding을 기반으로 Cassandra 작업 가능 여부를 판단한다.
4. 쓰기성 작업(DML/DDL/import/export)은 실행 전 preview, risk score, workflow, audit을 거친다.
5. 승인된 쿼리는 query hash와 approval token으로 동일성을 검증한 뒤 실행한다.
6. 운영 지표는 JMX, nodetool-equivalent, system tables, driver metrics, audit metrics로 분리 수집한다.

## Phase 개요

| Phase | 주제 | 마일스톤 | 주요 목표 |
|-------|------|---------:|---------|
| 1 | Project Foundation | 3 | monorepo, backend/frontend 기반, 공통 UI layout |
| 2 | Bootstrap & Metadata Foundation | 4 | 최초 실행 wizard, metadata Cassandra 초기화, 기본 seed |
| 3 | Identity, Login, Role & Permission | 4 | member, login/JWT, workspace, role, permission engine |
| 4 | Cluster Management | 3 | cluster CRUD, Cassandra session, 운영 정보 |
| 5 | Schema Explorer & Catalog | 3 | schema 탐색, catalog, schema 변경 이력 |
| 6 | CQL Interpreter & Query Policy | 3 | CQL 해석, query policy, schema-aware validation |
| 7 | Query Workspace & Review | 4 | editor, 자동 검수, 사람 검수, 승인 실행 |
| 8 | Workflow & Approval | 6 | 공통 workflow, 가입/권한/query/table-creation/break-glass 승인 |
| 9 | Table Creation Governance | 3 | table 생성 요청, 설계 자동 검수, 승인 |
| 10 | Table Data Explorer & Safe Data Change | 2 | 데이터 탐색, 안전한 DML 변경 |
| 11 | Audit & History | 2 | 감사 로그 저장, 검색 UI |
| 12 | Monitoring & Metrics | 3 | Cassandra 지표 수집, dashboard, alert/incident |
| 13 | Data Protection, Export & Import | 3 | masking, export policy, import validation |
| 14 | Operations Automation | 3 | change calendar, runbook, repair/compaction console |
| 15 | Admin, Settings & Integrations | 3 | system admin, preference, Slack/email/webhook |
| 16 | Production Packaging & Release | 3 | Docker/Helm, 운영 문서, release readiness |

## 폴더 구조

Phase별 `README.md`는 두지 않는다. Phase 개요와 전체 설계는 이 문서에 모으고, 세부 실행 단위는 milestone 문서로 관리한다.

```text
docs/planning/
├── planning.md
├── legacy-feature-parity.md
├── phase-1/
│   ├── milestone-1.md
│   ├── milestone-2.md
│   └── milestone-3.md
├── phase-2/
│   ├── milestone-1.md
│   ├── milestone-2.md
│   ├── milestone-3.md
│   └── milestone-4.md
└── phase-3 ... phase-16/
```

## Phase별 상세 설계

### Phase 1. Project Foundation

프로젝트를 장기 운영 가능한 구조로 재정비한다. backend/frontend가 같은 release cadence로 움직일 수 있도록 monorepo, build, lint, test, local infra, 공통 API/UI 규칙을 먼저 고정한다.

| Milestone | Feature | 상세 기능 설계 |
|---|---|---|
| M1. 기본 프로젝트 세팅 | Monorepo 구조 | `cassdio-core`, `cassdio-web`, frontend app, docs, docker를 명확히 분리하고 root Gradle build로 묶는다. |
| M1 | Spring Boot API 프로젝트 | Kotlin/Spring Boot 3.x 기반 API shell, package convention, profile별 `application.yml`을 구성한다. |
| M1 | React Vite 프로젝트 | React, TypeScript, Vite 기반 app shell과 `components/pages/hooks/remotes/utils` 구조를 만든다. |
| M1 | 공통 개발 환경 | formatter, linter, test script, `.editorconfig`, CI entrypoint를 구성한다. |
| M1 | Docker Compose 로컬 환경 | Cassandra 3.11/4.x/5.x 검증용 compose 파일과 bootstrap 테스트용 환경을 제공한다. |
| M1 | Health Check API | `/api/health`, `/api/version`, readiness/liveness endpoint를 제공한다. |
| M1 | GitHub Actions CI | backend test/build, frontend test/build가 PR마다 실행되도록 한다. |
| M2. 공통 Backend 구조 | API Response | 성공/실패 응답 envelope, trace id, validation error shape를 표준화한다. |
| M2 | Error Code | domain별 error code, HTTP status, user-facing message, log level을 정의한다. |
| M2 | Global Exception Handler | Spring 전역 예외 처리와 audit 가능한 실패 응답 구조를 구현한다. |
| M2 | Validation 구조 | request DTO validation과 field error 응답을 일관화한다. |
| M2 | Spring REST Docs | API 테스트 케이스 기반으로 REST Docs snippet과 Asciidoctor 문서를 생성한다. |
| M3. 공통 Frontend Layout | App Layout | Header, GNB, LNB Sidebar, Content, Footer를 포함한 console layout을 구현한다. |
| M3 | Header GNB | Dashboard, Clusters, Schema, Query, Governance, Monitoring, Admin 중심으로 상단 탐색을 제공한다. |
| M3 | User Settings Menu | profile, preference, logout, current workspace/environment 진입점을 제공한다. |
| M3 | LNB Sidebar | 선택된 GNB에 따라 phase별 하위 메뉴를 전환한다. |
| M3 | Footer 상태 표시 | app version, metadata health, schema version, current workspace/environment를 표시한다. |
| M3 | Tailwind CSS | Tailwind 기반 디자인 토큰과 theme 확장 구조를 구성한다. |
| M3 | Axios 공통 Client | token, timeout, refresh retry, 개인화 설정 기반 timeout 변경을 한 곳에서 관리한다. |

### Phase 2. Bootstrap & Metadata Foundation

Cassdio 최초 실행 시 metadata Cassandra 정보를 입력받고, `cassdio_meta` keyspace와 기본 데이터를 idempotent하게 초기화한다. 여러 instance가 동시에 시작해도 bootstrap lock으로 중복 실행을 막는다.

| Milestone | Feature | 상세 기능 설계 |
|---|---|---|
| M1. 최초 실행 Bootstrap | Bootstrap 상태 API | `/api/bootstrap/status`에서 metadata config 존재, schema version, super admin 존재 여부를 반환한다. |
| M1 | Metadata Cassandra 입력 UI | setup wizard에서 contact points, port, datacenter, auth, TLS, keyspace 설정을 입력한다. |
| M1 | 연결 테스트 | 입력값으로 Cassandra 연결, schema agreement, 권한을 검증하고 실패 사유를 분리한다. |
| M1 | Bootstrap Token | 서버 로그 또는 env로 발급된 token을 wizard에서 확인해 무단 초기화를 방지한다. |
| M2. Metadata Cassandra 초기화 | `cassdio_meta` 생성 | NetworkTopologyStrategy 기반 keyspace를 생성하고 개발 환경 replication 예외를 지원한다. |
| M2 | Migration 구조 | Cassandra CQL migration table과 schema version 추적을 구현한다. |
| M2 | Bootstrap Lock | `bootstrap_locks` 테이블로 owner, status, ttl, heartbeat를 관리한다. |
| M2 | 재시도 가능 초기화 | 실패한 step부터 재개할 수 있도록 bootstrap step state를 저장한다. |
| M3. 초기 기본 데이터 | Default Workspace | 기본 workspace, locale, timezone, signup mode를 seed한다. |
| M3 | Super Admin | 최초 관리자 계정, password hash, MFA placeholder, system role을 생성한다. |
| M3 | System Roles | Super Admin, Workspace Admin, Security Admin, DBA, Viewer 등 기본 role을 seed한다. |
| M3 | 기본 정책 | query, review, workflow, export, masking 기본 정책을 seed한다. |
| M4. 최초 관리 Cluster | 연결 테스트 | 관리 대상 cluster 연결, datacenter, version, node status를 검증한다. |
| M4 | Cluster Metadata 저장 | owner, environment, contact points, credential reference, TLS 설정을 저장한다. |
| M4 | 초기 권한 부여 옵션 | Super Admin에게 최초 cluster DBA 권한을 부여할지 선택 가능하게 한다. |
| M4 | 초기 Audit | bootstrap actor, 입력값 fingerprint, 생성 리소스, 실패/성공 이벤트를 남긴다. |

### Phase 3. Identity, Login, Role & Permission

Cassandra 계정과 별개로 Cassdio 내부 Member, 로그인 session, 권한 모델을 둔다. 권한 scope는 Application, Workspace, Cluster, Keyspace, Table, Column 계층으로 내려간다.

| Milestone | Feature | 상세 기능 설계 |
|---|---|---|
| M1. Member/Workspace | Member 모델 | email, display name, status, auth provider, locale/timezone, last login을 저장한다. |
| M1 | Workspace 모델 | 조직/팀 단위 workspace와 owner, signup policy, default role을 관리한다. |
| M1 | Workspace Membership | member와 workspace 관계, 초대/활성/비활성 상태를 관리한다. |
| M1 | 가입 상태 | pending, active, suspended, deleted 상태와 전환 이력을 관리한다. |
| M2. Login & Token | Local Login API | email/password 기반 로그인과 실패 횟수 제한, disabled member 차단을 구현한다. |
| M2 | JWT Access Token | 짧은 만료 시간의 stateless access token을 발급하고 API 인증에 사용한다. |
| M2 | Refresh Token | 긴 만료 시간의 refresh token을 서버에 hash로 저장하고 rotation 방식으로 갱신한다. |
| M2 | Logout/Session 관리 | refresh token revoke, device/session 목록, 전체 로그아웃을 지원한다. |
| M3. Role 기반 권한 | Role 모델 | system role과 custom role을 구분하고 workspace/cluster scope를 가진다. |
| M3 | Permission Binding | action, resource pattern, effect(allow/deny), condition, expiry를 role에 연결한다. |
| M3 | Role Assignment | member에게 role을 부여하고 승인 출처, 만료, 부여자를 저장한다. |
| M3 | Column 권한 | 민감 컬럼 read/mask/export 권한을 별도 action으로 분리한다. |
| M4. Permission Engine | Resource Path | `workspace:{id}/cluster:{id}/keyspace:{name}/table:{name}/column:{name}` 규칙을 정의한다. |
| M4 | Evaluator | deny 우선, scope specificity, expiry, condition을 반영해 최종 결정을 계산한다. |
| M4 | Required Permission Resolver | CQL 해석 결과를 기반으로 필요한 action과 resource를 산출한다. |
| M4 | Cache & Invalidation | role/policy 변경 시 권한 cache를 무효화하고 audit event를 남긴다. |

### Phase 4. Cluster Management

관리 대상 Cassandra 클러스터를 등록하고 session lifecycle을 운영한다. credential은 암호화된 reference로 저장하고, cluster 환경과 owner를 policy 판단에 사용한다.

| Milestone | Feature | 상세 기능 설계 |
|---|---|---|
| M1. Cluster CRUD | 등록 API | contact points, datacenter, auth, TLS, environment, owner를 입력받는다. |
| M1 | 목록/상세 API | cluster 상태, 버전, owner, session 상태, 최근 오류를 함께 반환한다. |
| M1 | 수정/삭제 | credential rotation, contact point 변경, soft delete, session cleanup을 처리한다. |
| M1 | 연결 정보 암호화 | password/token은 encryption service를 통해 암호화하거나 secret reference로 저장한다. |
| M2. Cassandra Session | Session Manager | cluster별 session 생성, cache, 재연결, 종료, health check를 관리한다. |
| M2 | Connection Test | 저장 전 연결 가능성, auth, datacenter mismatch, TLS 오류를 분리한다. |
| M2 | Timeout/Retry | query type별 timeout, retry, consistency 기본값을 설정한다. |
| M2 | Session Reset | cluster별 또는 전체 session reset API와 audit을 제공한다. |
| M3. 운영 정보 | Environment | DEV/STAGING/PROD/DR/SANDBOX 환경 정보를 정책에 연결한다. |
| M3 | Owner | 담당 팀, DBA, security owner, escalation channel을 관리한다. |
| M3 | Version/Node | Cassandra version, node list, DC/rack, driver 관점 UP/DOWN을 조회한다. |
| M3 | Client/Compaction | connected client와 compaction history를 운영 화면에 연결한다. |

### Phase 5. Schema Explorer & Catalog

Schema 탐색은 Cassandra metadata 조회와 Cassdio catalog metadata를 결합한다. table owner, 설명, 민감 컬럼, 변경 이력을 governance의 입력으로 사용한다.

| Milestone | Feature | 상세 기능 설계 |
|---|---|---|
| M1. Schema Explorer | Keyspace 목록 | system keyspace 제외/포함 옵션과 replication 정보를 제공한다. |
| M1 | Table 목록 | table, view, materialized view, index를 구분해 표시한다. |
| M1 | Column Metadata | partition key, clustering key, static, regular, type, order를 표시한다. |
| M1 | UDT/Index/View | UDT, secondary index, materialized view metadata를 조회한다. |
| M1 | Create Statement | Cassandra system tables 기반 `CREATE TABLE` 유사 definition을 제공한다. |
| M2. Schema Catalog | Table Owner | owner user/team과 escalation contact를 등록한다. |
| M2 | Description | 비즈니스 설명, 사용 목적, data freshness, retention을 관리한다. |
| M2 | Sensitive Column | column별 민감도와 masking/export 정책을 연결한다. |
| M2 | Tags | domain, service, pii, criticality 태그를 지원한다. |
| M3. Change History | Schema Audit | CREATE/ALTER/DROP/TRUNCATE 등 변경 이력을 저장한다. |
| M3 | Diff View | 변경 전후 CQL과 column/option diff를 보여준다. |
| M3 | Snapshot | 주기적 schema snapshot을 저장해 drift를 감지한다. |
| M3 | Owner Review | 민감 schema 변경 시 owner review workflow와 연결한다. |

### Phase 6. CQL Interpreter & Query Policy

완전한 CQL compiler가 아니라 권한/정책 판단에 필요한 정보를 안정적으로 추출하는 interpreter를 만든다. SELECT, INSERT, UPDATE, DELETE, DDL, ADMIN 작업을 분류한다.

| Milestone | Feature | 상세 기능 설계 |
|---|---|---|
| M1. CQL 해석기 | Statement Splitter | 문자열, 주석, 세미콜론을 고려해 multi statement를 분리한다. |
| M1 | Operation Classifier | SELECT/DML/DDL/ADMIN/UNKNOWN으로 분류하고 위험도를 초기화한다. |
| M1 | Resource Extractor | keyspace, table, columns, where columns, limit, allow filtering을 추출한다. |
| M1 | Required Permission | operation별 `query:select`, `data:update`, `schema:create_table` 등을 계산한다. |
| M2. Query Policy | Policy 모델 | operation, resource pattern, environment, max rows, timeout, consistency, approval 조건을 저장한다. |
| M2 | SELECT Policy | LIMIT 필수, max rows, allow filtering, full scan, sensitive column 정책을 검증한다. |
| M2 | DML Policy | PK 조건 필수, expected row count, before snapshot, prod approval 조건을 검증한다. |
| M2 | DDL/Admin Policy | DROP/TRUNCATE/ALTER/CREATE ROLE 등 critical operation을 차단하거나 승인 요구한다. |
| M3. Schema-aware Validation | Schema Provider | partition/clustering key, column type, sensitive flag를 interpreter에 제공한다. |
| M3 | Partition Key 검증 | SELECT/UPDATE/DELETE where 절의 partition key 조건 유무를 확인한다. |
| M3 | Type/Column 검증 | 존재하지 않는 column, type mismatch, UDT/collection 접근을 검증한다. |
| M3 | Risk Score | environment, operation, row estimate, sensitive data, policy finding으로 risk를 계산한다. |

### Phase 7. Query Workspace & Review

쿼리 실행은 작성, 자동 검수, 사람 입력, 승인, 최종 검증, 실행, 감사 순서로 흐른다.

| Milestone | Feature | 상세 기능 설계 |
|---|---|---|
| M1. Query Workspace | CQL Editor | 기존 Ace Editor 기반 사용성을 유지하고, Monaco 전환 여부와 무관하게 syntax highlight, 선택 영역 실행, 단축키를 제공한다. |
| M1 | Context Selector | cluster, keyspace, consistency, timeout, readonly mode를 선택한다. |
| M1 | Execute SELECT | SELECT 중심 실행, result grid, elapsed time, row count를 표시한다. |
| M1 | Query Trace | tracing on으로 실행한 query의 tracing id, coordinator, parameters, events를 표시한다. |
| M1 | Saved Query/History | 개인/팀 query 저장, 최근 실행, 즐겨찾기, parameter template을 지원한다. |
| M2. Query 자동 검수 | Review Engine | interpreter, permission evaluator, policy engine, schema validator를 조합한다. |
| M2 | Finding 모델 | severity, code, message, suggestion, blocking 여부를 저장한다. |
| M2 | Review Decision | ALLOW, WARN, REQUIRE_APPROVAL, DENY를 반환한다. |
| M2 | Review API/UI | 실행 전 검수 결과와 수정 제안을 editor 옆에 표시한다. |
| M3. 사람 검수 입력 | Template 모델 | SELECT, DML, DDL, Export 유형별 review form template을 정의한다. |
| M3 | DML Form | 변경 사유, 대상 row 수, before query, rollback plan을 입력한다. |
| M3 | DDL Form | 영향 keyspace/table, owner review, migration window, rollback strategy를 입력한다. |
| M3 | Evidence 첨부 | ticket link, incident id, change request id를 연결한다. |
| M4. 승인된 쿼리 실행 | Query Hash | 승인 당시 normalized query hash를 저장하고 실행 시 재검증한다. |
| M4 | Approval Token | token, actor, query hash, expiry, scope를 검증한다. |
| M4 | Final Validation | 실행 직전 schema/policy/permission이 바뀌었는지 재확인한다. |
| M4 | Execution Audit | review, approval, execution result, masked CQL, affected rows를 연결 저장한다. |

### Phase 8. Workflow & Approval

가입, 권한 요청, query approval, table creation, break glass를 공통 Workflow 모델로 처리한다. Table creation workflow는 요청, 자동 검수, 보류, 보완 요청, 승인, 실행, catalog 반영까지 포함한다.

| Milestone | Feature | 상세 기능 설계 |
|---|---|---|
| M1. Workflow Core | Request 모델 | type, requester, resource, payload_json, status, risk, due_at을 저장한다. |
| M1 | Status 관리 | DRAFT, SUBMITTED, UNDER_REVIEW, PENDING_APPROVAL, ON_HOLD, NEEDS_REVISION, APPROVED, REJECTED, EXECUTED, CANCELED를 지원한다. |
| M1 | Approval Step | step order, approver rule, decision, comment, delegated approver를 관리한다. |
| M1 | Inbox API | 내 요청함, 승인함, 완료함, 필터/정렬/검색을 제공한다. |
| M2. 가입 Workflow | Signup Mode | AUTO_SIGNUP, MANUAL_APPROVAL, INVITE_ONLY 정책을 설정한다. |
| M2 | 수동 승인 | 가입 요청 후 workspace admin/security admin 승인으로 활성화한다. |
| M2 | 초대 기반 가입 | invite token, expiry, default role, workspace binding을 지원한다. |
| M2 | 가입 Audit | 신청, 승인, 반려, 만료 이벤트를 기록한다. |
| M3. 권한 요청 Workflow | Role 요청 | 사용자가 role과 scope를 요청하고 owner 승인을 받는다. |
| M3 | Resource Permission 요청 | cluster/keyspace/table/action 단위 임시/영구 권한을 요청한다. |
| M3 | Expiry 권한 | 임시 권한 만료, 사전 알림, 자동 회수를 처리한다. |
| M3 | 승인 후 Binding | 승인 완료 시 role assignment 또는 permission binding을 생성한다. |
| M4. Query Approval | Review 연동 | REQUIRE_APPROVAL decision에서 workflow request를 자동 생성한다. |
| M4 | Approval Detail UI | 승인자가 CQL, finding, risk, 사람 입력, diff를 확인한다. |
| M4 | 승인 정책 | environment/risk/resource owner/security reviewer/DBA step을 동적으로 구성한다. |
| M4 | 승인 후 실행 | approval token을 발급하고 query workspace에서 실행 가능하게 한다. |
| M5. Break Glass | 긴급 권한 요청 | incident id, 사유, scope, 짧은 만료 시간을 필수화한다. |
| M5 | 긴급 승인 정책 | 2인 승인 또는 security override 정책을 지원한다. |
| M5 | 고강도 Audit | 모든 행위를 별도 severity로 저장하고 사후 리뷰 task를 생성한다. |
| M5 | 자동 회수 | expiry 도달 시 권한 회수와 session reset을 수행한다. |
| M6. Table Creation Workflow | Table Creation 요청 | CREATE TABLE 요청과 컬럼별 masking rule 후보를 workflow payload로 저장한다. |
| M6 | 자동 검수 연동 | table design finding, 민감 컬럼 finding, masking rule 누락 여부를 workflow에 연결한다. |
| M6 | 보류/보완 요청 | 승인자가 ON_HOLD 또는 NEEDS_REVISION으로 전환하고 사유와 보완 필드를 지정한다. |
| M6 | 승인/실행 | 승인 완료 시 DDL 실행 token을 발급하고 table 생성 후 catalog와 masking policy를 반영한다. |

### Phase 9. Table Creation Governance

CREATE TABLE은 단순 DDL 승인이 아니라 데이터 모델링 리뷰, 민감 컬럼 선언, 컬럼별 마스킹 룰 지정, 운영 승인 절차를 포함한다.

| Milestone | Feature | 상세 기능 설계 |
|---|---|---|
| M1. Table Creation Request | Request 모델 | CREATE TABLE payload, target keyspace, owner, expected traffic, retention, column classification, masking rule 후보를 저장한다. |
| M1 | CQL Parser 연동 | table name, columns, primary key, options, indexes를 추출한다. |
| M1 | Wizard UI | 컬럼, primary key, compaction/compression, TTL, owner를 단계별 입력한다. |
| M1 | Request Snapshot | 승인 시점의 CQL, metadata, review finding을 보존한다. |
| M2. Table Design 자동 검수 | Naming 검증 | table/column naming convention과 reserved word를 검사한다. |
| M2 | Primary Key 검증 | partition key, clustering key, high-cardinality, hot partition 가능성을 검토한다. |
| M2 | Option 검증 | compaction, compression, default TTL, gc_grace_seconds, speculative retry를 검토한다. |
| M2 | Sensitive Data 검증 | 민감 컬럼 선언, masking/export policy, owner review 필요 여부, masking rule 누락 여부를 판단한다. |
| M3. Table Creation Approval | 승인 단계 생성 | risk와 environment에 따라 Keyspace Owner, Security Reviewer, DBA step을 생성한다. |
| M3 | Detail UI | CQL, 설계 finding, 예상 데이터량, 운영 옵션, reviewer comment를 보여준다. |
| M3 | 승인 실행 | 승인된 CREATE TABLE만 query hash/token으로 실행한다. |
| M3 | Catalog 자동 등록 | 생성 완료 후 owner, description, tags, sensitive columns, masking policy binding을 catalog에 반영한다. |

### Phase 10. Table Data Explorer & Safe Data Change

데이터 조회는 Cassandra paging state와 partition key 중심으로 안전하게 제공하고, 변경은 preview/snapshot/audit을 필수로 둔다.

| Milestone | Feature | 상세 기능 설계 |
|---|---|---|
| M1. Table Data Explorer | Query Builder | 조건, 컬럼, limit, order를 UI에서 선택해 SELECT CQL을 생성한다. |
| M1 | Paging State | next page와 previous stack을 관리하고 URL 공유 시 민감 state 노출을 막는다. |
| M1 | Partition Key Finder | partition key 조건을 쉽게 입력하도록 schema 기반 form을 제공한다. |
| M1 | Result Grid | collection/UDT/blob/timeuuid 타입 표현, column hide, copy, detail view를 제공한다. |
| M2. Safe Data Change | DML Preview | UPDATE/DELETE/INSERT 실행 전 CQL, 대상 row, 변경 전후 값을 표시한다. |
| M2 | Before Snapshot | 변경 전 row snapshot을 저장해 rollback 판단 자료로 남긴다. |
| M2 | Row-level 검증 | full primary key 조건, expected row count, TTL/timestamp 사용을 검증한다. |
| M2 | Rollback Aid | rollback CQL 초안과 실행 이력을 제공한다. |

### Phase 11. Audit & History

Audit은 보안 로그이면서 운영 추적 데이터다. actor, action, resource, decision, risk, masked CQL, result를 표준화한다.

| Milestone | Feature | 상세 기능 설계 |
|---|---|---|
| M1. Audit Logging Core | Audit 모델 | actor, action, resource, request id, decision, risk, result, trace id를 저장한다. |
| M1 | Query Audit | review, approval, execution, failure를 하나의 correlation id로 묶는다. |
| M1 | Masking | query literal, credential, sensitive column value를 audit 저장 전 마스킹한다. |
| M1 | Retention | audit retention, archive, export 제한 정책을 둔다. |
| M2. Audit 조회 UI | 목록 API | 기간, 사용자, action, resource, decision, risk로 검색한다. |
| M2 | Detail UI | 원인 request, workflow, policy finding, execution result를 연결 표시한다. |
| M2 | Diff/Timeline | 변경성 작업의 전후 diff와 승인 timeline을 보여준다. |
| M2 | Export 제한 | audit export는 security permission과 별도 approval을 요구한다. |

### Phase 12. Monitoring & Metrics

운영 dashboard는 "지금 안전한가"에 답해야 한다. 초기 MVP 지표는 node up/down, schema agreement, disk/load, read/write latency, timeout/retry, pending compaction, slow query, failed query다.

| Milestone | Feature | 상세 기능 설계 |
|---|---|---|
| M1. Metrics Collector | Node Status | UP/DOWN, state, DC, rack, load, tokens를 수집한다. |
| M1 | Client List | Cassandra connected client 목록을 조회하고 client count metric으로 연결한다. |
| M1 | Compaction History | cluster/keyspace별 compaction history를 조회하고 dashboard와 연결한다. |
| M1 | Schema Agreement | schema agreement 상태와 불일치 node를 감지한다. |
| M1 | JMX/Nodetool Collector | tablestats, tpstats, compactionstats, status를 equivalent API로 수집한다. |
| M1 | Driver Metrics | Cassdio driver latency, timeout, retry, pool 상태를 수집한다. |
| M2. Dashboard | Cluster Health | health score, node status, latency, timeout, pending compaction을 요약한다. |
| M2 | Node Detail | node별 load, heap, thread pool, dropped message, repair age를 표시한다. |
| M2 | Slow Query | Cassdio audit 기반 느린 쿼리, 실패 쿼리, 권한 거부 추이를 표시한다. |
| M2 | Time Series | metrics를 시간 단위로 저장하고 p95/p99/rate를 계산한다. |
| M3. Alert & Incident | Alert Rule | disk, node down, timeout, compaction backlog, schema disagreement rule을 정의한다. |
| M3 | Evaluation Engine | 수집 metric과 derived metric을 rule로 평가한다. |
| M3 | Incident 연결 | alert 발생 시 incident 상태, 담당자, runbook, notification을 연결한다. |
| M3 | Notification | Slack/email/webhook으로 alert와 workflow 상태를 알린다. |

### Phase 13. Data Protection, Export & Import

민감 데이터는 column catalog와 role/policy를 조합해 마스킹하고, export/import는 항상 정책 검증과 이력화를 거친다.

| Milestone | Feature | 상세 기능 설계 |
|---|---|---|
| M1. Data Protection | Sensitive Type | email, phone, card, token, secret 등 기본 민감 유형을 정의한다. |
| M1 | Masking Policy | role/resource/action에 따라 full/partial/hash masking을 적용한다. |
| M1 | Result Masking | query result grid와 export pipeline 모두에서 동일한 masking을 적용한다. |
| M1 | Policy Test | 특정 사용자와 column 조합으로 masking 결과를 preview한다. |
| M2. Export | Query Result Export | CSV/JSON export를 제공하고 masking/limit/approval 정책을 적용한다. |
| M2 | Export Policy | 대량 export, prod, 민감 컬럼 포함 여부로 approval 필요성을 판단한다. |
| M2 | Async Job | 큰 export는 job으로 실행하고 만료 URL 또는 secure download를 제공한다. |
| M2 | Export Audit | 요청자, query hash, row count, masking 여부, download 이벤트를 기록한다. |
| M3. Import | CSV Preview | 파일 업로드 후 delimiter, encoding, header, sample row, column mapping을 보여준다. |
| M3 | Type Validation | Cassandra column type, nullability, collection/UDT 변환을 검증한다. |
| M3 | Import Approval | prod/import volume/risk에 따라 workflow approval을 요구한다. |
| M3 | Batch Execution | batch size, retry, failure report, partial success 정책을 제공한다. |

### Phase 14. Operations Automation

승인된 운영 작업은 일정, freeze window, runbook과 연결되어야 한다. Repair/compaction은 조회 중심에서 시작해 안전한 실행으로 확장한다.

| Milestone | Feature | 상세 기능 설계 |
|---|---|---|
| M1. Change Calendar | Calendar 모델 | 승인된 DML/DDL/export/import/repair 작업의 예약 정보를 관리한다. |
| M1 | Maintenance Window | 환경별 작업 가능 시간과 blackout/freeze window를 설정한다. |
| M1 | Schedule Validation | 승인된 작업도 실행 시점이 정책상 가능한지 재검증한다. |
| M1 | Calendar UI | 월/주/일 view와 cluster/keyspace filter를 제공한다. |
| M2. Runbook & Playbook | Runbook Library | 장애 대응 문서, owner, version, checklist를 관리한다. |
| M2 | Query Playbook | 자주 쓰는 진단 쿼리 묶음과 실행 권한을 관리한다. |
| M2 | Incident Checklist | alert/incident 유형별 확인 항목을 workflow와 연결한다. |
| M2 | Execution Log | runbook 실행 이력과 담당자 comment를 저장한다. |
| M3. Repair/Compaction | Repair Status | 최근 repair 이력, 진행 상태, 오래된 table을 조회한다. |
| M3 | Recommendation | repair age, table size, risk를 기반으로 추천을 생성한다. |
| M3 | Compaction Console | pending/running compaction, compaction history, backlog를 표시한다. |
| M3 | Safe Action | repair/cleanup/compaction trigger는 policy와 workflow를 거쳐 실행한다. |

### Phase 15. Admin, Settings & Integrations

운영자가 시스템 설정, metadata 상태, 사용자 preference, 외부 notification을 관리한다.

| Milestone | Feature | 상세 기능 설계 |
|---|---|---|
| M1. System Admin | Settings UI | signup, default policy, retention, notification, security 설정을 관리한다. |
| M1 | Metadata 상태 | `cassdio_meta` health, schema version, migration history, backup 상태를 표시한다. |
| M1 | Seed 관리 | system role/policy seed version과 drift를 확인한다. |
| M1 | Cache 관리 | permission/policy/schema cache 상태와 수동 invalidate를 제공한다. |
| M2. User Preferences | Profile | display name, locale, timezone, auth provider 정보를 관리한다. |
| M2 | 개인 설정 | theme, default workspace, default cluster/keyspace를 설정한다. |
| M2 | Query Editor 설정 | font size, keymap, default limit, auto review 옵션을 관리한다. |
| M2 | Notification 설정 | 개인 알림 채널과 workflow 알림 수신 범위를 설정한다. |
| M3. Integrations | Slack | approval request, alert, workflow 상태 변경 알림을 보낸다. |
| M3 | Email | 가입 승인, 권한 요청, workflow 결과 알림을 보낸다. |
| M3 | Webhook | 외부 incident/change system에 event를 전달한다. |
| M3 | SSO placeholder | OIDC/SAML 연동을 위한 provider abstraction을 준비한다. |

### Phase 16. Production Packaging & Release

실제 운영 배포를 위한 image, compose, Helm, 문서, 테스트, security scan을 완성한다.

| Milestone | Feature | 상세 기능 설계 |
|---|---|---|
| M1. Deployment Packaging | Docker Image | API와 frontend static build를 포함한 production image를 생성한다. |
| M1 | Docker Compose | metadata Cassandra, Cassdio API, optional reverse proxy 예제를 제공한다. |
| M1 | Helm Chart | deployment, service, ingress, secret, configmap, probes를 정의한다. |
| M1 | External Secret | credential을 env/secret manager로 주입할 수 있게 한다. |
| M2. Documentation | README | 프로젝트 소개, 설치, 실행, 주요 기능, screenshot을 정리한다. |
| M2 | Architecture | metadata Cassandra, permission, workflow, query review 구조를 문서화한다. |
| M2 | Admin Guide | bootstrap, cluster 등록, 권한/정책, backup/restore 운영 절차를 작성한다. |
| M2 | API 문서 | Spring REST Docs 기반 API 문서와 주요 workflow 예제를 제공한다. |
| M3. Release Readiness | E2E Test | bootstrap, cluster 등록, schema 탐색, query review, workflow 플로우를 테스트한다. |
| M3 | Security Scan | dependency, secret, container scan을 CI에 추가한다. |
| M3 | Performance Smoke | 큰 schema, paging, query result grid, metrics polling을 검증한다. |
| M3 | Release Checklist | versioning, changelog, migration, rollback, known issues를 정리한다. |

## MVP 범위

처음 공개 가능한 MVP는 Phase 1~6 전체와 Phase 7의 SELECT 중심 workspace까지로 제한한다.

| 포함 | 제외 |
|---|---|
| bootstrap, metadata Cassandra 초기화 | 고도화된 import/export job |
| Super Admin, 기본 role/policy seed | break glass 운영 |
| cluster 등록과 session 관리 | repair/compaction 실행 |
| schema explorer와 table data explorer | SSO/SAML |
| SELECT query workspace와 기본 review | 대규모 alert/incident 자동화 |

## 개발 순서

1. Phase 1-2: 기반 시스템과 bootstrap 완성
2. Phase 3-4: 권한/cluster/session 기반 완성
3. Phase 5-6: schema explorer와 CQL policy engine 완성
4. Phase 7-9: query review, workflow, table creation governance
5. Phase 10-13: data explorer, audit, monitoring, data protection
6. Phase 14-16: 운영 자동화, 통합, production release

## 완료 기준

각 milestone은 다음 조건을 만족해야 완료로 본다.

- API, service, metadata schema, UI가 같은 feature scope로 연결되어 있다.
- permission, policy, audit 영향이 검토되어 있다.
- 주요 실패 케이스와 재시도 동작이 정의되어 있다.
- 운영 환경에서 필요한 설정, metric, log, 문서가 함께 갱신되어 있다.
