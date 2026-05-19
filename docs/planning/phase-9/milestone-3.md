# Phase 9 - Milestone 3: Table Creation Approval

## 개요

테이블 생성 요청을 Keyspace Owner, Security Reviewer, DBA 승인 절차와 연결하고 승인된 DDL만 실행합니다.

## 포함된 Features

### Feature 1. Table Creation 승인 단계 생성
**설명**: 환경, 민감정보, risk level에 따라 승인 단계를 동적으로 생성한다.

**체크리스트**:
- [ ] approver rule 정의
- [ ] risk별 step 구성
- [ ] owner/security/DBA step 지원
- [ ] approval timeout 처리

---

### Feature 2. Table Creation Approval Detail UI 구현
**설명**: 승인자가 CQL, 설계 finding, 예상 데이터량, 운영 옵션, comment를 확인한다.

**체크리스트**:
- [ ] CREATE TABLE preview
- [ ] design finding 표시
- [ ] catalog metadata 표시
- [ ] 승인/반려 comment 입력

---

### Feature 3. 승인된 CREATE TABLE 실행
**설명**: query hash와 approval token을 검증한 뒤 DDL을 실행한다.

**체크리스트**:
- [ ] hash 검증
- [ ] token 검증
- [ ] 실행 직전 schema 중복 확인
- [ ] 실패 시 workflow 상태 반영

---

### Feature 4. Catalog 자동 등록
**설명**: 생성 완료 후 owner, description, tags, sensitive columns를 catalog에 반영한다.

**체크리스트**:
- [ ] table catalog 생성
- [ ] column catalog 생성
- [ ] schema change audit 저장
- [ ] metadata cache invalidation

---

### Feature 5. 보류 / 보완 요청 처리
**설명**: 승인자가 테이블 생성 요청을 보류하거나 요청자에게 보완을 요구한다.

**체크리스트**:
- [ ] ON_HOLD reason code 저장
- [ ] 재검토 예정일 저장
- [ ] NEEDS_REVISION 대상 필드 지정
- [ ] 보완 제출 후 자동 검수 재실행

---

### Feature 6. Masking Policy Binding 반영
**설명**: 승인된 컬럼별 마스킹 룰을 table/column catalog와 policy engine에 반영한다.

**체크리스트**:
- [ ] column catalog에 classification 저장
- [ ] masking rule binding 생성
- [ ] query result/export/audit 적용 범위 저장
- [ ] permission/policy cache invalidation

## 완료 기준

- [ ] 승인되지 않은 CREATE TABLE은 실행되지 않는다.
- [ ] 생성된 table은 schema explorer와 catalog에서 바로 조회된다.
- [ ] 승인된 masking rule이 데이터 조회와 export 정책에 적용된다.
