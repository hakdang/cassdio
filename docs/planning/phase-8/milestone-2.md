# Phase 8 - Milestone 2: 가입 Workflow

## 개요

Cassdio 사용자 가입을 AUTO_SIGNUP, MANUAL_APPROVAL, INVITE_ONLY 모드로 운영할 수 있게 합니다.

## 포함된 Features

### Feature 1. Signup Mode 설정
**설명**: workspace 또는 system 단위 가입 정책을 설정한다.

**체크리스트**:
- [ ] AUTO_SIGNUP / MANUAL_APPROVAL / INVITE_ONLY 정의
- [ ] workspace별 override 지원
- [ ] default role 연결
- [ ] 설정 변경 audit 저장

---

### Feature 2. 수동 가입 승인 Workflow 구현
**설명**: 가입 요청 후 관리자 승인에 따라 사용자를 활성화한다.

**체크리스트**:
- [ ] signup request 생성
- [ ] approval step 생성
- [ ] 승인 시 user 활성화
- [ ] 반려/만료 처리

---

### Feature 3. Invite 기반 가입 구현
**설명**: 초대 token, expiry, workspace membership, default role을 관리한다.

**체크리스트**:
- [ ] invite token 발급
- [ ] token 만료/취소 처리
- [ ] 초대 수락 UI 구현
- [ ] membership 생성

## 완료 기준

- [ ] 가입 정책에 따라 user lifecycle이 다르게 동작한다.
- [ ] 가입 요청과 승인/반려 이력이 audit에 남는다.
