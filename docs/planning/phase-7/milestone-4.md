# Phase 7 - Milestone 4: 승인된 쿼리 실행

## 개요

승인 당시의 쿼리와 실제 실행 쿼리가 동일한지 검증하고, 만료되지 않은 approval token으로만 실행합니다.

## 포함된 Features

### Feature 1. Query Hash 생성
**설명**: normalized CQL, cluster, keyspace, parameters를 기반으로 승인 검증용 hash를 생성한다.

**체크리스트**:
- [ ] CQL normalize 규칙 정의
- [ ] context 포함 hash 생성
- [ ] parameterized query hash 지원
- [ ] review 결과와 hash 연결

---

### Feature 2. Approval Token 검증
**설명**: 승인된 쿼리 실행 시 token, query hash, 사용자, 만료, scope를 검증한다.

**체크리스트**:
- [ ] token 발급/저장 모델 정의
- [ ] expiry 검증
- [ ] actor/scope 검증
- [ ] 재사용 가능 여부 정책 적용

---

### Feature 3. Final Validation 구현
**설명**: 실행 직전 권한, 정책, schema 변경 여부를 재검증한다.

**체크리스트**:
- [ ] permission 재평가
- [ ] policy version 변경 감지
- [ ] schema snapshot 변경 감지
- [ ] 실패 시 승인 재요청 처리

---

### Feature 4. Execution Audit 저장
**설명**: review, approval, execution result, masked CQL, affected rows를 하나의 이력으로 저장한다.

**체크리스트**:
- [ ] correlation id 연결
- [ ] 성공/실패/timeout 결과 저장
- [ ] literal masking 적용
- [ ] workflow 상태 업데이트

## 완료 기준

- [ ] 승인된 hash와 다른 query는 실행되지 않는다.
- [ ] 실행 결과가 audit와 query history에서 추적된다.
