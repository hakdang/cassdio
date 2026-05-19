# Phase 8 - Milestone 5: Break Glass Workflow

## 개요

장애 상황에서 짧은 시간 동안 높은 권한을 부여하되, 사유와 사후 감사가 강제되는 긴급 접근 흐름을 제공합니다.

## 포함된 Features

### Feature 1. Emergency Access Request 구현
**설명**: incident id, 사유, scope, 만료 시간을 포함한 긴급 권한 요청을 생성한다.

**체크리스트**:
- [ ] incident id 필수화
- [ ] scope와 action 제한
- [ ] 짧은 expiry 강제
- [ ] 긴급 요청 전용 audit severity 적용

---

### Feature 2. Break Glass 승인 정책 구현
**설명**: 보안 관리자 또는 2인 승인 등 긴급 접근 승인 정책을 적용한다.

**체크리스트**:
- [ ] approval route 분리
- [ ] security reviewer 필수화
- [ ] 업무 시간 외 알림
- [ ] 반려/만료 처리

---

### Feature 3. 자동 회수 및 사후 리뷰
**설명**: 만료 시 권한을 자동 회수하고 사후 리뷰 task를 생성한다.

**체크리스트**:
- [ ] expiry job 구현
- [ ] session reset 옵션
- [ ] post-review workflow 생성
- [ ] 긴급 접근 보고서 생성

## 완료 기준

- [ ] 긴급 권한은 항상 짧은 만료와 사후 audit을 가진다.
- [ ] 만료 후 permission engine에서 권한이 제거된다.
