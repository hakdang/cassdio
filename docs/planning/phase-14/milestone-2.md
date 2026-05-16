# Phase 14 - Milestone 2: Runbook & Playbook

## 개요

장애 대응 문서와 진단 쿼리 묶음을 Cassdio 안에서 관리하고 incident/workflow와 연결합니다.

## 포함된 Features

### Feature 1. Runbook Library 구현
**설명**: 장애 대응 문서를 콘솔에서 관리할 수 있게 한다.

**체크리스트**:
- [ ] runbook 모델 정의
- [ ] version 관리
- [ ] owner/escalation 연결
- [ ] markdown rendering

---

### Feature 2. Query Playbook 구현
**설명**: 자주 쓰는 진단 쿼리 묶음을 관리한다.

**체크리스트**:
- [ ] playbook 모델 정의
- [ ] query template parameter
- [ ] 실행 권한 검증
- [ ] 실행 결과 저장

---

### Feature 3. Incident Checklist 구현
**설명**: incident 유형별 확인 항목과 담당자 comment를 관리한다.

**체크리스트**:
- [ ] checklist template
- [ ] item status/comment
- [ ] incident timeline 연결
- [ ] completion audit

---

### Feature 4. Runbook Execution Log 구현
**설명**: runbook 실행 이력과 담당자 comment를 저장한다.

**체크리스트**:
- [ ] execution log 모델
- [ ] actor/time/result 저장
- [ ] 관련 alert/workflow 연결
- [ ] report export 제한

## 완료 기준

- [ ] alert/incident에서 관련 runbook을 바로 확인할 수 있다.
- [ ] 진단 playbook 실행도 권한과 audit를 따른다.
