# Existing Cassdio Feature Parity Check

## 기준

GitHub `hakdang/cassdio`의 README와 주요 API/UI 코드를 기준으로 기존 기능이 v2 planning에 포함되어 있는지 점검한다.

## 기존 공개 기능

| 기존 기능 | 기존 위치 | v2 반영 위치 | 보강 방향 |
|---|---|---|---|
| Multi Cassandra Cluster | README, `ClusterApi` | Phase 4 M1 | credential 보안, version compatibility, audit 추가 |
| AuthCredentials 기반 cluster 등록 | `ClusterRegisterRequest`, `ClusterApi` | Phase 4 M1 | secret reference, rotation, TLS 옵션 추가 |
| Cluster 목록/상세/수정/삭제 | `ClusterApi` | Phase 4 M1 | password masking, owner/environment metadata 추가 |
| 전체/단일 session clear | `ClusterApi` | Phase 4 M1 | 권한 검증, 확인 UI, audit 추가 |
| Node monitoring | `ClusterNodeApi` | Phase 12 M1/M2 | health dashboard, alert, time-series 확장 |
| Connected client 조회 | `ClusterClientApi` | Phase 12 M1 | client metric과 dashboard 연결 |
| Compaction history 조회 | `CompactionMonitoringApi` | Phase 12 M1/M2 | keyspace filter, trend, alert 연결 |
| Keyspace 목록/상세/describe | `ClusterKeyspaceApi` | Phase 5 M1 | catalog, owner, schema diff 연결 |
| Keyspace drop | `ClusterKeyspaceApi` | Phase 5 M1, Phase 7-8 | admin permission, workflow guard 추가 |
| Table 목록/상세/definition | `ClusterTableApi` | Phase 5 M1 | catalog, sensitive column metadata 연결 |
| Column metadata 조회 | `ClusterTableApi` | Phase 5 M1 | column classification, masking policy 연결 |
| UDT type 목록/상세 | `ClusterUDTTypeApi` | Phase 5 M1 | table column link, UDT detail viewer 보강 |
| Table drop/truncate | `ClusterTableApi` | Phase 5 M1, Phase 8-9 | workflow approval, risk/audit 추가 |
| Table row 조회 | `ClusterTableRowApi` | Phase 10 M1 | query builder, paging state stack, masking 추가 |
| CSV import sample 다운로드 | `ClusterTableRowApi` | Phase 13 M3 | validation preview, template version 관리 |
| CSV import upload | `ClusterTableRowApi` | Phase 13 M3 | type validation, workflow, failure report 추가 |
| Batch type / per commit size | `ClusterTableRowApi` | Phase 13 M3 | safe batch execution policy 추가 |
| Simple Query Editor | README, `query-editor.js` | Phase 7 M1 | review, workflow, history, saved query 추가 |
| 선택 영역 query 실행 | `query-editor.js` | Phase 7 M1 | shortcut/help, audit 연결 |
| Ctrl/Command + Enter 실행 | `query-editor.js` | Phase 7 M1 | keyboard shortcut 유지 |
| Limit / consistency / timeout option | `query-editor.js` | Phase 7 M1, Phase 1 M3 | 개인화 설정 기반 기본값으로 확장 |
| Query tracing | `ClusterQueryApi`, `query-trace-view-modal.js` | Phase 7 M1 | trace event timeline과 audit 연결 |
| Cursor 기반 query result | `ClusterQueryApi` | Phase 7 M1, Phase 10 M1 | paging UX와 cursor 보안 개선 |
| Bootstrap 기반 초기 로딩 | `router.js`, `bootstrapApi` | Phase 2 | setup wizard, bootstrap lock, seed 확장 |
| Admin cluster page | `router.js` | Phase 4 M1, Phase 15 | system admin과 cluster onboarding 확장 |

## 누락 보강 결과

이번 점검으로 다음 항목을 planning 문서에 명시적으로 보강했다.

- Phase 4 M1: session clear, version compatibility, credential masking
- Phase 5 M1: UDT type, keyspace drop, table drop/truncate
- Phase 7 M1: selected query 실행, keyboard shortcut, query options, query trace
- Phase 10 M1: row data 조회, next cursor, rowHeader/columnList 결합
- Phase 12 M1: connected client, compaction history
- Phase 13 M3: import sample CSV, batch type, per commit size

## v2 향상 기준

기존 기능은 단순 유지가 아니라 다음 기준으로 향상한다.

1. 위험 작업은 권한, 확인, workflow, audit를 거친다.
2. 조회 결과는 column classification과 masking policy를 적용한다.
3. query 실행은 timeout, consistency, trace, review, approval, audit와 연결한다.
4. 기존 cursor 기반 조회는 paging state stack과 cursor 보안 정책으로 확장한다.
5. import/export는 preview, validation, approval, job history를 포함한다.
