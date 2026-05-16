# Phase 13 - Milestone 3: Import

## 개요

CSV import를 preview, type validation, approval, batch execution, failure report 기반으로 안전하게 수행합니다.

## 기존 기능 Parity

- Import sample CSV 다운로드
- CSV upload import
- Table column order 기반 import
- Batch type 선택
- per commit size 설정

## 포함된 Features

### Feature 1. CSV Import Preview 구현
**설명**: CSV 파일을 업로드하고 column mapping preview를 제공한다.

**체크리스트**:
- [ ] import sample CSV 다운로드
- [ ] delimiter/encoding/header 감지
- [ ] sample row preview
- [ ] column mapping UI
- [ ] invalid row 표시

---

### Feature 2. Import Type Validation 구현
**설명**: CSV 값이 Cassandra column type에 맞는지 검증한다.

**체크리스트**:
- [ ] primitive type 변환 검증
- [ ] collection/UDT 검증
- [ ] null/empty 처리 정책
- [ ] validation report 생성

---

### Feature 3. Import Approval Workflow 구현
**설명**: PROD, 대량 import, 민감 table 변경은 approval을 요구한다.

**체크리스트**:
- [ ] import risk 산출
- [ ] workflow payload 생성
- [ ] preview snapshot 연결
- [ ] 승인 후 execution token 발급

---

### Feature 4. Batch Execution 구현
**설명**: batch size, retry, partial success 정책을 적용해 import를 실행한다.

**체크리스트**:
- [ ] batch type 선택
- [ ] per commit size 설정
- [ ] batch size 설정
- [ ] retry 정책
- [ ] failure row report
- [ ] execution audit 저장

## 완료 기준

- [ ] import 전 type validation과 preview가 완료된다.
- [ ] import 실행 결과와 실패 row를 추적할 수 있다.
