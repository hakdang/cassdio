# Phase 8 - Milestone 3: 권한 요청 Workflow

## 개요

사용자가 필요한 role 또는 resource permission을 요청하고, 승인 후 권한 binding이 자동 생성되게 합니다.

## 포함된 Features

### Feature 1. Role 요청 Workflow 구현
**설명**: 사용자가 특정 role과 scope를 요청할 수 있게 한다.

**체크리스트**:
- [ ] role request payload 정의
- [ ] scope 선택 UI 구현
- [ ] owner/security approver 산출
- [ ] 승인 후 role assignment 생성

---

### Feature 2. Resource Permission 요청 Workflow 구현
**설명**: cluster/keyspace/table/action 단위 권한을 요청할 수 있게 한다.

**체크리스트**:
- [ ] resource picker 구현
- [ ] action picker 구현
- [ ] allow/deny effect 정책 적용
- [ ] permission binding 생성

---

### Feature 3. 임시 권한 만료 구현
**설명**: 권한 요청에 expiry를 부여하고 만료 시 자동 회수한다.

**체크리스트**:
- [ ] expiry 필드와 최대 기간 정책
- [ ] 만료 전 알림
- [ ] 만료 후 binding 비활성화
- [ ] 회수 audit 저장

## 완료 기준

- [ ] 승인된 권한 요청이 실제 permission engine 결과에 반영된다.
- [ ] 임시 권한은 만료 후 자동으로 효력을 잃는다.
