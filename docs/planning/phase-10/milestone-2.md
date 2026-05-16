# Phase 10 - Milestone 2: Safe Data Change

## 개요

UPDATE/DELETE/INSERT 같은 데이터 변경 작업을 preview, snapshot, approval, audit 기반으로 안전하게 실행합니다.

## 포함된 Features

### Feature 1. DML 변경 전 Preview 구현
**설명**: 실행 전 CQL, 대상 row, 변경 전후 값을 미리 보여준다.

**체크리스트**:
- [ ] PK 기반 대상 row 조회
- [ ] 변경 전후 diff 표시
- [ ] 예상 affected row 표시
- [ ] 위험 finding 표시

---

### Feature 2. Before Snapshot 저장
**설명**: 데이터 변경 전 row snapshot을 저장해 추적과 복구 판단에 사용한다.

**체크리스트**:
- [ ] snapshot 모델 정의
- [ ] sensitive value masking
- [ ] snapshot retention 정책
- [ ] audit event 연결

---

### Feature 3. Row-level 검증
**설명**: full primary key 조건, expected row count, TTL/timestamp 사용을 검증한다.

**체크리스트**:
- [ ] PK 조건 필수화
- [ ] row count mismatch 차단
- [ ] TTL/timestamp risk finding
- [ ] LWT 사용 여부 표시

---

### Feature 4. Rollback Aid 구현
**설명**: rollback CQL 초안과 실행 이력을 제공한다.

**체크리스트**:
- [ ] rollback CQL 생성
- [ ] rollback 가능/불가 판단
- [ ] rollback 실행도 workflow 연결
- [ ] 변경 이력 timeline 표시

## 완료 기준

- [ ] 데이터 변경 전 preview와 snapshot이 생성된다.
- [ ] 모든 변경 작업은 audit log에서 추적된다.
