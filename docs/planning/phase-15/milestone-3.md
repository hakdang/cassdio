# Phase 15 - Milestone 3: External Integrations

## 개요

승인 요청, alert, workflow 상태 변경을 외부 시스템으로 전달하고 인증 연동 확장 지점을 준비합니다.

## 포함된 Features

### Feature 1. Slack Notification 연동
**설명**: 승인 요청, alert, workflow 상태 변경을 Slack으로 알린다.

**체크리스트**:
- [ ] Slack webhook 설정
- [ ] message template
- [ ] channel routing
- [ ] retry/failure audit

---

### Feature 2. Email Notification 연동
**설명**: 가입 승인, 권한 요청, workflow 결과를 email로 알린다.

**체크리스트**:
- [ ] SMTP 설정
- [ ] email template
- [ ] user preference 반영
- [ ] bounce/failure 기록

---

### Feature 3. Webhook Integration 구현
**설명**: 외부 incident/change system에 Cassdio event를 전달한다.

**체크리스트**:
- [ ] webhook endpoint 설정
- [ ] signing secret
- [ ] retry/backoff
- [ ] event delivery log

---

### Feature 4. SSO Provider Abstraction 준비
**설명**: OIDC/SAML 연동을 위한 provider abstraction을 준비한다.

**체크리스트**:
- [ ] auth provider interface
- [ ] local auth와 provider 분리
- [ ] group to role mapping 설계
- [ ] login audit 확장

## 완료 기준

- [ ] workflow와 alert 이벤트가 외부 채널로 전달된다.
- [ ] 향후 SSO 연동을 위한 구조가 열려 있다.
