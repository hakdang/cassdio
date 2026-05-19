# Phase 1 - Milestone 3: 공통 Frontend Layout 구성

## 📝 개요

모든 페이지에서 사용될 기본 레이아웃 컴포넌트들을 구현합니다. 헤더, 사이드바, 풋터 등의 기본 UI 구조를 구성하고, Tailwind CSS 기반 스타일 토대와 공통 API client를 만듭니다.

## ✅ 포함된 Features

### Feature 1. App Layout 구현
**설명**: Header, LNB Sidebar, Content, Footer를 포함한 기본 콘솔 layout을 구현한다.

**구조**:
```
┌─────────────────────────────┐
│      Header (GNB)           │
├──────────┬──────────────────┤
│ LNB      │                  │
│ Sidebar  │   Content Area   │
│          │                  │
├──────────┴──────────────────┤
│      Footer                 │
└─────────────────────────────┘
```

**체크리스트**:
- [ ] Layout 컴포넌트 구조 설계
- [ ] Responsive Grid 구현
- [ ] 각 영역 기본 스타일 설정
- [ ] z-index 관리

---

### Feature 2. Header GNB 구현
**설명**: 프로젝트명 옆에 GNB 대메뉴가 표시되도록 Header를 구현한다.

**주요 메뉴**:
- Dashboard
- Cluster
- Query Workspace
- Schema
- Operations
- Admin

**체크리스트**:
- [ ] Header 컴포넌트 작성
- [ ] GNB 메뉴 구조 정의
- [ ] 메뉴별 아이콘 추가
- [ ] 메뉴 선택 상태 표시
- [ ] 반응형 디자인

---

### Feature 3. User Settings Menu 구현
**설명**: 우측 상단 개인 설정 아이콘과 드롭다운 메뉴를 구현한다.

**메뉴 항목**:
- Profile
- Settings
- Preferences
- Logout

**체크리스트**:
- [ ] 사용자 아이콘 컴포넌트 작성
- [ ] 드롭다운 메뉴 구현
- [ ] 프로필 정보 표시
- [ ] 로그아웃 기능
- [ ] 키보드 네비게이션

---

### Feature 4. GNB별 LNB Sidebar 구현
**설명**: 선택된 GNB에 따라 사이드 메뉴가 바뀌는 LNB 구조를 구현한다.

**사이드바 메뉴 예시**:
- Cluster 메뉴: Cluster List, Cluster Detail, Health
- Query Workspace 메뉴: Query Editor, Query History, Saved Queries
- Schema 메뉴: Keyspace, Table, Indexes

**체크리스트**:
- [ ] Sidebar 컴포넌트 작성
- [ ] 메뉴 구조 정의 및 라우팅 연동
- [ ] 선택된 메뉴 하이라이팅
- [ ] 서브메뉴 펼치기/접기
- [ ] 스크롤 가능한 메뉴

---

### Feature 5. Footer 구현
**설명**: 버전, workspace, metadata 상태, API 상태 등을 표시할 수 있는 Footer를 구현한다.

**표시 항목**:
- 버전 정보
- 현재 Workspace
- Metadata Cassandra 상태
- API 상태 (Online/Offline)

**체크리스트**:
- [ ] Footer 컴포넌트 작성
- [ ] 상태 정보 표시
- [ ] 실시간 상태 업데이트
- [ ] 반응형 디자인

---

### Feature 6. Page Header / Breadcrumb 공통 컴포넌트 구현
**설명**: 모든 화면에서 사용할 페이지 제목, 설명, breadcrumb 구조를 만든다.

**구성**:
```
Cluster > Prod Cluster > Tables > Users > Schema
┌─────────────────────────────┐
│ Table Name                  │
│ This is table description   │
└─────────────────────────────┘
```

**체크리스트**:
- [ ] PageHeader 컴포넌트 작성
- [ ] Breadcrumb 컴포넌트 구현
- [ ] 제목 및 설명 표시
- [ ] 액션 버튼 영역
- [ ] 반응형 디자인

---

### Feature 7. Empty / Error / Loading 상태 컴포넌트 구현
**설명**: 운영 콘솔에서 공통으로 사용할 상태 UI를 구현한다.

**상태 컴포넌트**:
- Empty State (데이터 없음)
- Error State (오류 발생)
- Loading State (로딩 중)
- Skeleton Loading

**체크리스트**:
- [ ] EmptyState 컴포넌트 작성 (아이콘, 메시지, 액션)
- [ ] ErrorState 컴포넌트 작성 (에러 코드, 메시지, 재시도)
- [ ] Loading 컴포넌트 작성 (Spinner, Progress)
- [ ] Skeleton 컴포넌트 작성
- [ ] 사용 예제 작성

### Feature 8. Login 페이지 구현
**설명**: login 전용 페이지와 비로그인 사용자 redirect 진입점을 구현한다. 실제 인증 API, JWT, refresh token 처리는 Phase 3 Milestone 2에서 구현한다.

**체크리스트**:
- [ ] `/login` route 구현
- [ ] email/password form UI 구현
- [ ] 비로그인 상태에서 보호 route 접근 시 `/login` redirect
- [ ] 로그인 후 원래 접근하려던 URL로 복귀할 redirect parameter 처리
- [ ] login error message 영역 구현
- [ ] Phase 3 auth API 연동을 위한 placeholder service 작성

---

### Feature 9. Tailwind CSS 기반 스타일 시스템 구성
**설명**: 운영 콘솔 UI를 빠르고 일관되게 구현할 수 있도록 Tailwind CSS를 설정하고, 공통 디자인 토큰을 정의한다.

**체크리스트**:
- [ ] Tailwind CSS 설치 및 Vite/PostCSS 연동
- [ ] `tailwind.config` content path 설정
- [ ] 색상, spacing, border radius, shadow 등 기본 token 정의
- [ ] dark mode 또는 theme 확장 가능 구조 준비
- [ ] 공통 class naming / component style convention 문서화
- [ ] 기본 layout과 상태 컴포넌트에 Tailwind 적용

---

### Feature 10. Axios 공통 API Client 인터페이스 구현
**설명**: token 관리, timeout 관리, base URL, error handling, refresh retry를 한 곳에서 제어할 수 있는 공통 axios client를 구성한다. 이후 개인화 설정에서 query timeout 등이 바뀌면 API timeout도 함께 변경될 수 있게 한다.

**기본 정책**:

| 항목 | 기본값 | 향후 확장 |
|---|---:|---|
| default API timeout | 3초 | 개인화 설정으로 변경 |
| query API timeout | 3초 | Query Editor preference에서 5초 등으로 변경 |
| refresh retry | 1회 | auth 정책 기반 조정 |
| base URL | env 기반 | workspace/environment별 override 가능 |

**체크리스트**:
- [ ] axios instance factory 구현
- [ ] request interceptor에서 access token 주입
- [ ] response interceptor에서 401 처리 및 refresh retry hook 준비
- [ ] timeout 값을 runtime config에서 읽도록 구성
- [ ] 개인화 설정 placeholder(`ApiClientSettings`) 정의
- [ ] query API와 일반 API timeout을 분리할 수 있는 interface 정의
- [ ] 공통 error shape를 `ApiResponse`와 매핑
- [ ] request correlation id header 확장 지점 준비

**설정 예시**:

```ts
type ApiClientSettings = {
  defaultTimeoutMs: number; // 기본 3000
  queryTimeoutMs: number;   // 기본 3000, 개인화에서 5000 등으로 변경 가능
  baseUrl: string;
};
```

```text
개인화 설정 변경
  -> ApiClientSettings 갱신
  -> axios client timeout 갱신
  -> query API 호출은 queryTimeoutMs 사용
  -> 일반 API 호출은 defaultTimeoutMs 사용
```

## 📋 체크리스트

- [ ] Feature 1 완료
- [ ] Feature 2 완료
- [ ] Feature 3 완료
- [ ] Feature 4 완료
- [ ] Feature 5 완료
- [ ] Feature 6 완료
- [ ] Feature 7 완료
- [ ] Feature 8 완료
- [ ] Feature 9 완료
- [ ] Feature 10 완료
- [ ] 전체 레이아웃 테스트
- [ ] 다양한 화면 크기 테스트
- [ ] 스토리북 또는 컴포넌트 카탈로그 작성

## 🔗 관련 문서

- [Milestone 2: 공통 Backend 구조 구성](./milestone-2.md)

---

**예상 소요 시간**: 1-2주
