# Phase 15 - Milestone 2: User Preferences

## 개요

사용자별 profile, UI preference, query editor preference, notification preference를 관리합니다.

## 포함된 Features

### Feature 1. Profile 화면 구현
**설명**: 사용자 프로필 정보를 조회/수정한다.

**체크리스트**:
- [ ] display name 수정
- [ ] locale/timezone 설정
- [ ] auth provider 표시
- [ ] profile 변경 audit

---

### Feature 2. 개인 설정 화면 구현
**설명**: 언어, timezone, theme, 기본 cluster/keyspace 등을 설정한다.

**체크리스트**:
- [ ] theme 설정
- [ ] default workspace 설정
- [ ] default cluster/keyspace 설정
- [ ] 설정 저장 API

---

### Feature 3. Query Editor Preference 구현
**설명**: font size, keymap, default limit, auto review 옵션을 관리한다.

**체크리스트**:
- [ ] editor font/keymap 설정
- [ ] default query limit
- [ ] auto review on/off
- [ ] saved query 기본 scope

---

### Feature 4. Notification Preference 구현
**설명**: 개인 알림 채널과 workflow 알림 수신 범위를 설정한다.

**체크리스트**:
- [ ] Slack/email 수신 설정
- [ ] approval 알림 설정
- [ ] alert 알림 설정
- [ ] quiet hours 설정

## 완료 기준

- [ ] 사용자의 기본 작업 환경이 preference로 저장된다.
- [ ] notification 수신 범위를 사용자가 제어할 수 있다.
