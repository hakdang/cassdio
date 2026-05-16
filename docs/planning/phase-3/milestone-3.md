# Phase 3 - Milestone 3: Role 기반 권한 관리

## ✅ 포함된 Features

### Feature 1. Role 모델 구현
**설명**: system role과 custom role을 관리할 수 있는 모델을 구현한다.

**체크리스트**:
- [ ] Role 모델 정의
- [ ] Role 저장소 구현
- [ ] System role vs Custom role 구분

---

### Feature 2. Role Permission Binding 구현
**설명**: role에 action, resource path, effect를 연결하는 permission binding 구조를 구현한다.

**체크리스트**:
- [ ] Permission 모델 정의
- [ ] RolePermission 관계 구현
- [ ] Binding 저장소

---

### Feature 3. Member Role Assignment 구현
**설명**: member에게 role을 특정 scope로 부여하는 기능을 구현한다.

**체크리스트**:
- [ ] RoleAssignment 모델
- [ ] Scope 정의 (Application, Workspace, Cluster, Keyspace 등)
- [ ] 할당 API
- [ ] 승인 출처와 만료 시각 저장

---

### Feature 4. Role 관리 UI 구현
**설명**: role 목록, 생성, 수정, 상세, 권한 매핑 UI를 구현한다.

**체크리스트**:
- [ ] Role 목록 페이지
- [ ] Role 생성/수정 폼
- [ ] 권한 매핑 UI

---

### Feature 5. Member Role Assignment UI 구현
**설명**: member에게 role을 부여하거나 회수할 수 있는 화면을 구현한다.

**체크리스트**:
- [ ] Member Role 할당 페이지
- [ ] Role 선택 UI
- [ ] 할당 취소 기능

---

### Feature 6. Session 권한 변경 반영
**설명**: role이나 permission 변경 시 로그인 session의 권한 cache를 무효화한다.

**체크리스트**:
- [ ] permission cache invalidation
- [ ] role 변경 audit
- [ ] 다음 API 요청부터 변경 권한 적용
- [ ] 필요 시 member session 강제 revoke 옵션

## 📋 체크리스트

- [ ] 모든 Features 완료

---

**예상 소요 시간**: 1-2주
