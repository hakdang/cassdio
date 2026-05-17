# Phase 7 - Milestone 1: Query Workspace 기본 기능

## 개요

기존 Cassdio의 Simple Query Editor 기능을 기본으로 유지하고, query option, trace, selected query 실행, history, saved query, review 연동으로 확장합니다.

## 기존 기능 Parity

- CQL Query 실행
- Cluster / Keyspace context 실행
- 선택 영역 query 실행
- Ctrl/Command + Enter 실행
- Limit 선택
- Consistency Level 선택
- Query Timeout 입력
- Query Tracing On/Off
- Query trace modal
- 다음 cursor 기반 결과 조회

## 포함된 Features

### Feature 1. CQL Editor 구현
**설명**: CQL 작성용 editor를 구현한다. 기존 Ace Editor 기능을 유지하되, Monaco 전환 여부는 구현 시점에 결정한다.

**체크리스트**:
- [ ] CQL syntax highlight
- [ ] undo/redo toolbar
- [ ] selected query 실행
- [ ] Ctrl/Command + Enter 실행
- [ ] query clear/save/autosave 확장 지점

---

### Feature 2. Cluster / Keyspace Selector 구현
**설명**: query 실행 context를 선택한다.

**체크리스트**:
- [ ] cluster context 실행
- [ ] keyspace context 실행
- [ ] keyspace query command 미지원 version 처리
- [ ] 현재 context breadcrumb 표시

---

### Feature 3. Query Execute API 구현
**설명**: CQL을 실행하고 결과, cursor, trace 정보를 반환한다.

**체크리스트**:
- [ ] POST /cluster/{clusterId}/query
- [ ] POST /cluster/{clusterId}/keyspace/{keyspace}/query
- [ ] nextCursor 반환
- [ ] wasApplied 반환
- [ ] queryTrace 조건부 반환

---

### Feature 4. Query Option Panel 구현
**설명**: limit, consistency level, timeout, tracing 옵션을 설정한다.

**체크리스트**:
- [ ] limit 선택
- [ ] consistency level 선택
- [ ] query timeout 입력
- [ ] tracing on/off
- [ ] 개인화 설정의 query timeout 기본값 연동

---

### Feature 5. Result Grid 구현
**설명**: query 결과 row, header, column metadata를 표시한다.

**체크리스트**:
- [ ] row grid 표시
- [ ] rowHeader 표시
- [ ] columnList metadata 연결
- [ ] next cursor pagination
- [ ] collection/UDT/blob 값 표시 개선

---

### Feature 6. Query Trace View 구현
**설명**: tracing on으로 실행한 query의 trace id, coordinator, parameters, events를 modal로 표시한다.

**체크리스트**:
- [ ] tracing id 표시
- [ ] coordinator address 표시
- [ ] parameters 표시
- [ ] events timeline 표시
- [ ] source elapsed micros 표시

---

### Feature 7. Query History / Saved Query 구현
**설명**: 실행 이력과 저장 쿼리를 관리한다.

**체크리스트**:
- [ ] query history 저장
- [ ] saved query CRUD
- [ ] team/shared query scope
- [ ] audit와 연결

---

**예상 소요 시간**: 6-8주
