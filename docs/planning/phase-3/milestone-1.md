# Phase 3 - Milestone 1: Member / Workspace 기본 관리

## ✅ 포함된 Features

### Feature 1. Member 모델 및 저장소 구현
**설명**: Cassdio 로그인 주체인 Member 정보를 Cassandra metadata에 저장하고 조회하는 구조를 구현한다.

**체크리스트**:
- [ ] Member 모델 정의 (id, email, displayName, status, authProvider, locale, timezone, createdAt 등)
- [ ] MemberRepository 구현
- [ ] CRUD API 구현
- [ ] 테스트 작성

**권장 Member 필드**:

| 필드 | 설명 |
|---|---|
| memberId | 내부 식별자. UUID/ULID 사용 |
| email | 로그인 ID. unique |
| displayName | 화면 표시 이름 |
| passwordHash | local login 사용 시 저장. 외부 SSO 전용 계정은 null 가능 |
| status | PENDING, ACTIVE, SUSPENDED, DISABLED, DELETED |
| authProvider | LOCAL, OIDC, SAML 등 |
| mfaEnabled | MFA 적용 여부 |
| locale/timezone | 사용자 기본 설정 |
| lastLoginAt | 마지막 로그인 시각 |
| passwordChangedAt | password 변경 시각. token 강제 무효화 기준 |

---

### Feature 2. Workspace 모델 및 저장소 구현
**설명**: workspace 정보를 저장하고 조회하는 구조를 구현한다.

**체크리스트**:
- [ ] Workspace 모델 정의
- [ ] WorkspaceRepository 구현
- [ ] 목록/상세/생성/수정/삭제 API

---

### Feature 3. Workspace Member 모델 구현
**설명**: workspace와 member의 membership 관계를 관리한다.

**체크리스트**:
- [ ] WorkspaceMember 모델 정의
- [ ] 관계 저장소 구현

**권장 WorkspaceMember 필드**:

| 필드 | 설명 |
|---|---|
| workspaceId | workspace 식별자 |
| memberId | member 식별자 |
| membershipStatus | INVITED, ACTIVE, SUSPENDED, LEFT |
| defaultWorkspace | 로그인 후 기본 진입 workspace 여부 |
| joinedAt | 가입 또는 초대 수락 시각 |
| invitedBy | 초대한 member |
| lastSelectedAt | 마지막 선택 시각 |

---

### Feature 4. Member 상태 관리 구현
**설명**: ACTIVE, PENDING, SUSPENDED, DISABLED, DELETED 등의 member 상태를 관리한다.

**상태들**:
- ACTIVE: 활성 member
- PENDING: 승인 대기
- SUSPENDED: 일시 정지
- DISABLED: 비활성화
- DELETED: 삭제 처리

**체크리스트**:
- [ ] MemberStatus enum 정의
- [ ] 상태 전이 로직
- [ ] 상태 변경 감사

---

### Feature 5. Member 목록 / 상세 UI 구현
**설명**: 관리자가 member를 조회하고 상태를 확인할 수 있는 화면을 구현한다.

**체크리스트**:
- [ ] Member 목록 페이지
- [ ] 필터 및 검색 기능
- [ ] Member 상세 페이지
- [ ] 상태 변경 UI

---

## 📋 체크리스트

- [ ] 모든 Features 완료
- [ ] API 테스트
- [ ] UI 테스트

---

**예상 소요 시간**: 1주
