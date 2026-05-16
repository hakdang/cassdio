# Phase 8 - Milestone 4: Query Approval Workflow

## 개요

Query Review 결과가 REQUIRE_APPROVAL일 때 workflow request를 생성하고 승인 후 실행 권한을 발급합니다.

## 포함된 Features

### Feature 1. Query Review와 Workflow 연결
**설명**: 자동 검수 결과가 approval을 요구하면 workflow request를 생성한다.

**체크리스트**:
- [ ] review result to workflow payload mapping
- [ ] query hash 저장
- [ ] risk level별 approval route 생성
- [ ] duplicate request 방지

---

### Feature 2. Query Approval Detail UI 구현
**설명**: 승인자가 CQL, 검수 결과, 사람 입력, risk, 영향 범위를 확인한다.

**체크리스트**:
- [ ] CQL diff/preview 표시
- [ ] finding list 표시
- [ ] human review form 표시
- [ ] 승인/반려 comment 입력

---

### Feature 3. Approval Token 발급
**설명**: 승인 완료 시 query hash와 scope가 묶인 execution token을 발급한다.

**체크리스트**:
- [ ] token expiry 정책
- [ ] actor/scope 제한
- [ ] 재사용 정책
- [ ] 실행 후 token 상태 변경

## 완료 기준

- [ ] 위험 query는 승인 전 실행되지 않는다.
- [ ] 승인 후에도 query hash가 달라지면 실행되지 않는다.
