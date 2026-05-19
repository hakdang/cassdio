# Phase 13 - Milestone 2: Export

## 개요

Query result와 table data export를 masking, policy, approval, audit 기반으로 안전하게 제공합니다.

## 포함된 Features

### Feature 1. Query Result Export 구현
**설명**: 쿼리 결과를 CSV/JSON으로 export한다.

**체크리스트**:
- [ ] CSV/JSON formatter
- [ ] masking 적용
- [ ] row limit 적용
- [ ] download audit 저장

---

### Feature 2. Export Policy 검증 구현
**설명**: 대량 export, 민감 컬럼 포함, PROD export 여부에 따라 승인 필요 여부를 판단한다.

**체크리스트**:
- [ ] export policy 모델
- [ ] sensitive column 검증
- [ ] row count threshold 검증
- [ ] workflow 연동

---

### Feature 3. Async Export Job 구현
**설명**: 큰 export를 비동기 job으로 실행하고 만료되는 download를 제공한다.

**체크리스트**:
- [ ] export job 모델
- [ ] progress/status API
- [ ] secure download token
- [ ] file retention/cleanup

---

### Feature 4. Export 이력 조회
**설명**: export 요청, 승인, 실행, download 이력을 조회한다.

**체크리스트**:
- [ ] export history API
- [ ] workflow/audit 연결
- [ ] download count 표시
- [ ] 실패 사유 표시

## 완료 기준

- [ ] 민감/대량 export는 정책에 따라 승인 없이는 실행되지 않는다.
- [ ] export 결과와 download가 audit로 추적된다.
