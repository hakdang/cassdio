# Phase 8 - Milestone 6: Table Creation Workflow

## 개요

테이블 생성 요청을 workflow로 관리합니다. 요청자는 CREATE TABLE 정보와 컬럼별 데이터 분류, 마스킹 룰을 제출하고, 승인자는 승인/반려뿐 아니라 보류와 보완 요청을 할 수 있습니다.

## Table Creation Workflow 상태 흐름

```text
DRAFT
  -> SUBMITTED
  -> UNDER_REVIEW
  -> NEEDS_REVISION
  -> SUBMITTED
  -> PENDING_APPROVAL
  -> ON_HOLD
  -> PENDING_APPROVAL
  -> APPROVED
  -> EXECUTED
```

반려나 취소가 발생하면 `REJECTED` 또는 `CANCELED`로 종료됩니다.

## 요청 데이터

| 항목 | 설명 |
|---|---|
| target cluster/keyspace | 테이블을 생성할 대상 |
| table name | 생성할 테이블 이름 |
| columns | 컬럼명, Cassandra type, nullable 여부, 설명 |
| primary key | partition key, clustering key, clustering order |
| table options | compaction, compression, TTL, gc_grace_seconds 등 |
| owner metadata | table owner, service/team, escalation contact |
| expected usage | 예상 row 수, write/read pattern, retention |
| column classification | PUBLIC, INTERNAL, CONFIDENTIAL, RESTRICTED 등급 |
| masking rule bindings | 컬럼별 적용할 masking rule과 적용 조건 |

## 포함된 Features

### Feature 1. Table Creation Request Workflow 생성
**설명**: 테이블 생성 요청을 workflow request로 생성하고 table creation payload를 저장한다.

**체크리스트**:
- [ ] request_type `TABLE_CREATION` 정의
- [ ] target cluster/keyspace resource path 저장
- [ ] CREATE TABLE CQL과 wizard 입력값 동시 저장
- [ ] 요청 payload schema version 관리

---

### Feature 2. 컬럼 분류 및 마스킹 룰 입력
**설명**: 요청자가 컬럼별 데이터 분류와 마스킹 룰을 지정한다.

**체크리스트**:
- [ ] column classification 필수/선택 정책 정의
- [ ] 민감 등급 컬럼은 masking rule 필수화
- [ ] masking rule 미리보기 제공
- [ ] export/query 화면 적용 범위 표시

---

### Feature 3. 자동 검수 결과 연결
**설명**: table design 검수와 민감 데이터 검수 finding을 workflow에 연결한다.

**체크리스트**:
- [ ] primary key finding 연결
- [ ] table option finding 연결
- [ ] sensitive column finding 연결
- [ ] masking rule 누락 finding 연결

---

### Feature 4. 보류 처리
**설명**: 승인자가 일정, owner 확인, 외부 change ticket 대기 등의 이유로 요청을 보류한다.

**체크리스트**:
- [ ] HOLD reason code 정의
- [ ] 보류 만료일 또는 재검토일 지정
- [ ] 보류 comment 필수화
- [ ] 보류 해제 시 PENDING_APPROVAL 복귀

---

### Feature 5. 보완 요청 처리
**설명**: 승인자가 요청자에게 특정 필드 보완을 요구한다.

**체크리스트**:
- [ ] revision requested fields 지정
- [ ] 컬럼 설명/분류/masking rule 보완 요구
- [ ] PK/table option 보완 요구
- [ ] 보완 제출 후 자동 검수 재실행

---

### Feature 6. 승인 후 실행 및 후처리
**설명**: 승인 완료 후 DDL 실행 token을 발급하고 실행 결과를 catalog와 masking policy에 반영한다.

**체크리스트**:
- [ ] query hash / approval token 검증
- [ ] CREATE TABLE 실행
- [ ] table/column catalog 생성
- [ ] masking policy binding 생성
- [ ] schema change audit 저장

## 완료 기준

- [ ] 테이블 생성 요청은 승인, 반려, 보류, 보완 요청을 모두 지원한다.
- [ ] 민감 컬럼은 마스킹 룰 없이는 승인 완료될 수 없다.
- [ ] 테이블 생성 후 catalog와 masking policy가 자동 반영된다.
