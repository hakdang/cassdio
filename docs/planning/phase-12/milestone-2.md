# Phase 12 - Milestone 2: Monitoring Dashboard

## 개요

Cluster health, node 상태, latency, timeout, compaction backlog, slow query를 한 화면에서 판단할 수 있는 운영 dashboard를 구현합니다.

## 포함된 Features

### Feature 1. Cluster Health Dashboard 구현
**설명**: cluster health, node status, latency, timeout, pending compaction 요약을 보여준다.

**체크리스트**:
- [ ] health score 모델 정의
- [ ] 핵심 지표 summary card
- [ ] 환경별 cluster filter
- [ ] 위험 상태 강조 표시

---

### Feature 2. Node Detail Monitoring UI 구현
**설명**: node별 load, heap, thread pool, dropped message, repair age를 표시한다.

**체크리스트**:
- [ ] node list/detail 화면
- [ ] DC/rack grouping
- [ ] JMX/nodetool metric 표시
- [ ] 최근 오류 표시

---

### Feature 3. Slow Query Dashboard 구현
**설명**: Cassdio audit와 execution history에서 느린 쿼리와 실패 쿼리를 요약한다.

**체크리스트**:
- [ ] slow query 기준 설정
- [ ] 사용자/cluster/table별 집계
- [ ] timeout/retry/failed query 표시
- [ ] query detail 연결

---

### Feature 4. Monitoring Time Series 저장
**설명**: 수집된 metrics를 시간 단위로 저장해 trend를 볼 수 있게 한다.

**체크리스트**:
- [ ] metric sample 모델 정의
- [ ] retention/downsampling 정책
- [ ] p95/p99/rate 계산
- [ ] dashboard chart API

## 완료 기준

- [ ] 운영자가 cluster 위험 상태를 dashboard에서 즉시 확인할 수 있다.
- [ ] metric trend와 slow query가 audit history와 연결된다.
