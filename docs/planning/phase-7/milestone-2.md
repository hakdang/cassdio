# Phase 7 - Milestone 2: Query 자동 검수

## 개요

CQL 실행 전에 interpreter, permission evaluator, policy engine, schema-aware validator를 조합해 자동 검수 결과를 생성합니다.

## 포함된 Features

### Feature 1. Query Review Engine 구현
**설명**: CQL 해석 결과, 사용자 권한, query policy, schema metadata를 조합해 실행 가능 여부를 판단한다.

**체크리스트**:
- [ ] Review input/output 모델 정의
- [ ] Permission / Policy / Schema validator 조합
- [ ] ALLOW, WARN, REQUIRE_APPROVAL, DENY decision 산출
- [ ] multi statement 검수 결과 병합

---

### Feature 2. Review Finding 모델 구현
**설명**: 검수 결과의 severity, code, message, suggestion, blocking 여부를 저장한다.

**체크리스트**:
- [ ] Finding severity 정의
- [ ] Finding code namespace 정의
- [ ] 사용자 수정 제안 메시지 구조화
- [ ] audit와 workflow에서 참조 가능한 id 생성

---

### Feature 3. Risk Score 계산
**설명**: environment, operation, row estimate, sensitive column, critical DDL 여부로 query risk를 계산한다.

**체크리스트**:
- [ ] LOW / MEDIUM / HIGH / CRITICAL 기준 정의
- [ ] PROD 환경 가중치 적용
- [ ] 민감 컬럼 포함 여부 반영
- [ ] approval policy와 연동

---

### Feature 4. Review API / UI 구현
**설명**: 실행 전 검수 API를 제공하고 editor 옆에 결과와 수정 제안을 표시한다.

**체크리스트**:
- [ ] POST /api/query/review API 구현
- [ ] finding list UI 구현
- [ ] blocking finding 실행 차단
- [ ] review 결과 저장 및 재조회

## 완료 기준

- [ ] SELECT/DML/DDL 기본 검수 결과가 decision으로 반환된다.
- [ ] 위험 query는 approval workflow로 넘길 수 있다.
- [ ] 검수 결과가 query history/audit와 연결된다.
