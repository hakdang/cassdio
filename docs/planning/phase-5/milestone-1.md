# Phase 5 - Milestone 1: Schema Explorer

## 개요

기존 Cassdio의 keyspace/table/column/definition/UDT 조회 기능을 유지하고, catalog와 governance로 확장 가능한 schema explorer를 구현합니다.

## 기존 기능 Parity

- Keyspace 목록 조회
- Keyspace 이름 목록 캐시/갱신
- Keyspace 상세와 describe 조회
- Table 목록 cursor 조회
- Table detail, describe, column list 조회
- UDT type 목록/상세 조회
- Keyspace drop
- Table drop / truncate

## 포함된 Features

### Feature 1. Keyspace 목록 / 상세 조회
**설명**: 선택한 cluster의 keyspace 목록, 이름 목록, 상세 정보, describe CQL을 조회한다.

**체크리스트**:
- [ ] user/system keyspace 구분
- [ ] keyspace name cache refresh 옵션
- [ ] replication/detail 정보 표시
- [ ] query editor 지원 여부 표시

---

### Feature 2. Table 목록 / 상세 조회
**설명**: keyspace별 table 목록과 table detail을 조회한다.

**체크리스트**:
- [ ] cursor 기반 table 목록
- [ ] table detail 조회
- [ ] CREATE TABLE definition 표시
- [ ] partition/clustering key 구분

---

### Feature 3. Column Metadata 조회
**설명**: table column metadata를 조회하고 type, key kind, order를 표시한다.

**체크리스트**:
- [ ] column list API
- [ ] partition/clustering/static/regular 구분
- [ ] collection/UDT type 표시
- [ ] column catalog metadata와 결합 가능한 구조

---

### Feature 4. UDT Type 조회
**설명**: 기존 UDT type 목록/상세 조회 기능을 유지한다.

**체크리스트**:
- [ ] UDT type 목록 API
- [ ] UDT type 상세 API
- [ ] field name/type 표시
- [ ] table column에서 UDT type link 제공

---

### Feature 5. Schema 위험 작업 Guard
**설명**: 기존 keyspace drop, table drop, truncate 기능을 유지하되 권한, 확인, workflow 기반으로 보호한다.

**체크리스트**:
- [ ] keyspace drop action
- [ ] table drop action
- [ ] table truncate action
- [ ] ADMIN 권한 검증
- [ ] PROD 환경 workflow 연결

---

### Feature 6. Schema Explorer UI
**설명**: keyspace, table, column, UDT, definition을 탐색하는 화면을 구현한다.

**체크리스트**:
- [ ] keyspace navigation
- [ ] table detail modal/page
- [ ] column list table
- [ ] describe CQL viewer
- [ ] UDT detail viewer

---

**예상 소요 시간**: 1-2주
