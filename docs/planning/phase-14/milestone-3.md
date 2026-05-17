# Phase 14 - Milestone 3: Repair / Compaction Console

## 개요

Repair와 compaction 상태를 조회하고, 안전 정책을 만족하는 경우 운영 action으로 확장할 수 있는 기반을 만듭니다.

## 포함된 Features

### Feature 1. Repair Status 조회
**설명**: repair 진행 상태와 최근 이력을 조회한다.

**체크리스트**:
- [ ] repair history 수집
- [ ] keyspace/table별 repair age 표시
- [ ] running repair 상태 표시
- [ ] 실패 이력 표시

---

### Feature 2. Repair Recommendation 구현
**설명**: 오래 repair되지 않은 keyspace/table을 탐지한다.

**체크리스트**:
- [ ] recommendation rule 정의
- [ ] table size/risk 반영
- [ ] owner notification 연결
- [ ] change calendar 연결

---

### Feature 3. Compaction Console 구현
**설명**: pending/running compaction, compaction history, backlog를 표시한다.

**체크리스트**:
- [ ] compactionstats 수집
- [ ] history 조회
- [ ] backlog trend 표시
- [ ] table별 compaction 상태 표시

---

### Feature 4. Safe Operation Action 설계
**설명**: repair/cleanup/compaction trigger는 policy와 workflow를 거쳐 실행한다.

**체크리스트**:
- [ ] operation policy 정의
- [ ] approval workflow 연결
- [ ] maintenance window 검증
- [ ] execution audit 저장

## 완료 기준

- [ ] repair/compaction 상태를 cluster/table 단위로 확인할 수 있다.
- [ ] 실행성 operation은 승인과 일정 검증 없이는 수행되지 않는다.
