# Phase 12 - Milestone 3: Alert & Incident

## 개요

수집된 metrics와 audit signal을 기반으로 alert rule을 평가하고 incident/runbook/notification으로 연결합니다.

## 포함된 Features

### Feature 1. Alert Rule 모델 구현
**설명**: disk, node down, timeout, compaction backlog 등 alert rule을 정의한다.

**체크리스트**:
- [ ] rule condition 모델
- [ ] severity 정의
- [ ] cluster/environment scope
- [ ] suppression/mute 설정

---

### Feature 2. Alert Evaluation Engine 구현
**설명**: 수집된 metrics를 기반으로 alert를 주기적으로 평가한다.

**체크리스트**:
- [ ] scheduler 구성
- [ ] threshold/window 평가
- [ ] recovery 조건 평가
- [ ] duplicate alert 방지

---

### Feature 3. Incident 연결 구현
**설명**: alert 발생 시 incident 상태, 담당자, runbook을 연결한다.

**체크리스트**:
- [ ] incident 모델 정의
- [ ] alert to incident mapping
- [ ] owner/escalation 연결
- [ ] incident timeline 저장

---

### Feature 4. Notification 연동
**설명**: Slack/email/webhook으로 alert와 incident 상태를 알린다.

**체크리스트**:
- [ ] notification template
- [ ] channel routing
- [ ] retry/failure audit
- [ ] resolved notification

## 완료 기준

- [ ] 주요 Cassandra 위험 상태가 alert로 생성된다.
- [ ] alert는 owner와 runbook이 연결된 incident로 추적된다.
