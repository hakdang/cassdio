# Phase 12 - Milestone 1: Cassandra Admin Metrics Collector

## 개요

기존 Cassdio의 node monitoring, client list, compaction history 조회 기능을 유지하고, JMX/nodetool-equivalent/driver metrics 수집으로 확장합니다.

## 기존 기능 Parity

- Node 목록 조회
- Node 상세 조회
- Connected client 목록 조회
- Compaction history 조회
- Keyspace별 compaction history filter

## 포함된 Features

### Feature 1. Node Status Collector 구현
**설명**: 노드 UP/DOWN, state, DC, rack, load 정보를 수집한다.

**체크리스트**:
- [ ] node list 조회
- [ ] node detail 조회
- [ ] driver 관점 node status
- [ ] DC/rack/load 표시

---

### Feature 2. Client List Collector 구현
**설명**: Cassandra에 연결된 client 정보를 조회한다.

**체크리스트**:
- [ ] connected client list 조회
- [ ] address/port/user agent 정보 표시
- [ ] cluster별 client filter
- [ ] client count metric 생성

---

### Feature 3. Compaction History Collector 구현
**설명**: compaction history를 cluster/keyspace 기준으로 조회한다.

**체크리스트**:
- [ ] compaction history list
- [ ] keyspace filter
- [ ] table별 compaction summary
- [ ] monitoring dashboard 연결

---

### Feature 4. Schema Agreement Collector 구현
**설명**: cluster schema agreement 상태를 수집한다.

**체크리스트**:
- [ ] schema agreement status
- [ ] disagreement node 표시
- [ ] alert rule 연결

---

### Feature 5. Driver Metrics Collector 구현
**설명**: Cassdio driver 관점 latency, timeout, retry, connection pool 지표를 수집한다.

**체크리스트**:
- [ ] read/write latency
- [ ] timeout/retry count
- [ ] pool status
- [ ] failed query count

**예상 소요 시간**: 6-8주
