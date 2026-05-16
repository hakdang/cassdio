# Phase 10 - Milestone 1: Table Data Explorer

## 개요

기존 Cassdio의 table row 조회 기능을 유지하고, Cassandra paging state, column metadata, query builder, masking, export/import 연계로 확장합니다.

## 기존 기능 Parity

- Table row 조회
- nextCursor 기반 pagination
- rowHeader와 columnList 함께 반환
- table column metadata와 row data 결합

## 포함된 Features

### Feature 1. Table Data Query Builder 구현
**설명**: UI에서 조건, 컬럼, limit을 선택해 table data를 조회할 수 있게 한다.

**체크리스트**:
- [ ] partition key 조건 form
- [ ] clustering key 조건 form
- [ ] limit 설정
- [ ] 생성 CQL preview

---

### Feature 2. Cassandra Paging State 기반 Pagination 구현
**설명**: Cassandra paging state를 next cursor로 관리한다.

**체크리스트**:
- [ ] nextCursor 반환
- [ ] previous cursor stack
- [ ] cursor 만료/오류 처리
- [ ] cursor 값 노출 보안 검토

---

### Feature 3. Row Data Grid 구현
**설명**: table row와 column metadata를 함께 표시한다.

**체크리스트**:
- [ ] rowHeader 표시
- [ ] columnList metadata 연결
- [ ] collection/UDT/blob 표시
- [ ] row detail modal
- [ ] masking applied 표시

---

### Feature 4. Partition Key Finder 구현
**설명**: partition key 기반 조회를 쉽게 입력하도록 schema 기반 form을 제공한다.

**체크리스트**:
- [ ] partition key 자동 감지
- [ ] type별 input component
- [ ] 필수 key 누락 warning
- [ ] allow filtering warning

---

### Feature 5. Data Explorer Audit 구현
**설명**: table data 조회, cursor 이동, 민감 컬럼 조회 여부를 audit로 저장한다.

**체크리스트**:
- [ ] query actor/resource 저장
- [ ] row count 저장
- [ ] sensitive column 포함 여부 저장
- [ ] masking 여부 저장

**예상 소요 시간**: 4-6주
