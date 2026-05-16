# Phase 8 - Milestone 1: Workflow Core

## 개요

가입, 권한 요청, query approval, table creation, break glass 등 모든 운영 요청을 공통 workflow 모델로 처리합니다.

## 공통 Workflow 상태

| Status | 의미 | 주요 사용처 |
|---|---|---|
| DRAFT | 요청자가 작성 중인 상태 | 테이블 생성 wizard 임시 저장 |
| SUBMITTED | 요청이 제출된 상태 | 가입, 권한, query, table creation |
| UNDER_REVIEW | 자동 검수 또는 담당자 검토 중 | table design review, security review |
| PENDING_APPROVAL | 승인자 결정을 기다리는 상태 | DBA/Owner/Security 승인 |
| ON_HOLD | 승인자가 판단을 보류한 상태 | 정보는 충분하지만 일정/정책/외부 이슈 대기 |
| NEEDS_REVISION | 요청자 보완이 필요한 상태 | 컬럼 분류 누락, masking rule 누락, PK 설계 보완 |
| APPROVED | 승인 완료 상태 | 실행 token 발급 가능 |
| REJECTED | 반려 상태 | 실행 불가 |
| EXECUTED | 승인된 작업이 실행 완료된 상태 | DDL/DML/query/import/export |
| CANCELED | 요청자가 취소한 상태 | 실행 불가 |

## 포함된 Features

### Feature 1. Workflow Request 모델 구현
**설명**: 모든 요청을 공통 모델로 저장한다.

**체크리스트**:
- [ ] request_id, request_type, requester_id, workspace_id 정의
- [ ] resource_type, resource_path, environment 저장
- [ ] payload_json, payload_schema_version 저장
- [ ] risk_level, due_at, created_at, updated_at 저장

---

### Feature 2. Status 관리 구현
**설명**: 요청 상태 전환 규칙과 상태별 허용 action을 정의한다.

**체크리스트**:
- [ ] 상태 전환 state machine 정의
- [ ] ON_HOLD 전환 사유 필수화
- [ ] NEEDS_REVISION 보완 필드 지정
- [ ] 상태 변경 audit 저장

---

### Feature 3. Approval Step 구현
**설명**: 요청 유형과 risk에 따라 승인 단계를 구성한다.

**체크리스트**:
- [ ] step order, approver rule, required decision 모델
- [ ] Keyspace Owner / Security Reviewer / DBA step 지원
- [ ] delegated approver 지원
- [ ] step timeout과 escalation 지원

---

### Feature 4. Decision 저장 구현
**설명**: 승인, 반려, 보류, 보완 요청 결정을 이력으로 저장한다.

**체크리스트**:
- [ ] APPROVE / REJECT / HOLD / REQUEST_REVISION decision 정의
- [ ] decision comment 필수 정책
- [ ] HOLD reason code 정의
- [ ] revision requested fields 저장

---

### Feature 5. Task Inbox 구현
**설명**: 승인자와 요청자가 처리해야 할 workflow task를 조회한다.

**체크리스트**:
- [ ] 내 승인함 API
- [ ] 내 요청함 API
- [ ] 보류/보완 필요 필터
- [ ] risk/resource/type별 필터

---

### Feature 6. Workflow Event Audit 구현
**설명**: workflow 생성, 제출, 상태 변경, decision, 실행을 audit log로 남긴다.

**체크리스트**:
- [ ] workflow event type 정의
- [ ] actor/action/resource 연결
- [ ] payload 변경 diff 저장
- [ ] external ticket/evidence link 저장

## 완료 기준

- [ ] 모든 요청 유형이 같은 workflow status와 decision 모델을 사용한다.
- [ ] 보류와 보완 요청이 승인/반려와 구분되어 이력화된다.
- [ ] workflow event가 audit log에서 추적된다.
