# Phase 2 - Milestone 3: 초기 기본 데이터 생성

## 📝 개요

Cassdio 초기화 시 필요한 기본 데이터들을 자동으로 생성합니다. 기본 workspace, super admin 사용자, 역할, 권한, 정책 등을 seed 데이터로 생성합니다.

## ✅ 포함된 Features

### Feature 1. Default Workspace 생성
**설명**: 초기 workspace를 생성하고 기본 signup mode를 설정한다.

**생성 정보**:
- Workspace name: "Default"
- Description
- Signup mode: MANUAL_APPROVAL (기본값)

**체크리스트**:
- [ ] WorkspaceSeed 구현
- [ ] Default workspace 생성 로직
- [ ] Signup mode 설정
- [ ] Workspace owner 설정

---

### Feature 2. Super Admin Member 생성
**설명**: 최초 관리자 Member를 생성하고 비밀번호를 안전하게 해싱해 저장한다.

**저장 정보**:
- Display name
- Email
- Hashed password (bcrypt/scrypt)
- Member status: ACTIVE
- Auth provider: LOCAL

**체크리스트**:
- [ ] Member 모델 정의
- [ ] 비밀번호 해싱 로직 (bcrypt)
- [ ] Super Admin Member 생성
- [ ] 이메일 검증 (선택사항)

---

### Feature 3. System Roles Seed 생성
**설명**: Super Admin, DBA, Security Admin, Data Reader 등 기본 role을 생성한다.

**기본 역할들**:
- Super Admin: 모든 권한
- DBA: Cluster/DDL 관련 권한
- Security Admin: 권한/정책 관리
- Data Reader: SELECT 전용
- Data Analyst: 제한된 DML 권한

**체크리스트**:
- [ ] Role 모델 정의
- [ ] 각 role별 기본 권한 설정
- [ ] Role description 작성
- [ ] 기본 role 생성 로직

---

### Feature 4. Super Admin Role Assignment 생성
**설명**: Super Admin Member에게 application scope의 최고 권한을 부여한다.

**권한 할당**:
- Scope: Application level
- Role: Super Admin
- Effective from: 초기화 시점

**체크리스트**:
- [ ] RoleAssignment 모델 정의
- [ ] Super Admin Member에게 role 할당
- [ ] Scope 설정
- [ ] 할당 이력 기록

---

### Feature 5. 기본 Query Policy Seed 생성
**설명**: SELECT, DML, DDL, Admin CQL에 대한 보수적 기본 정책을 생성한다.

**기본 정책**:
- SELECT: LIMIT 필수, max rows 1000
- DML: WHERE 필수, 최대 행 수 제한
- DDL: 승인 필수
- Admin CQL: 차단

**체크리스트**:
- [ ] QueryPolicy 모델 정의
- [ ] 정책별 규칙 설정
- [ ] 정책 설명 및 예제
- [ ] 기본 정책 생성

---

### Feature 6. 기본 Workflow Policy Seed 생성
**설명**: 가입, 권한 요청, DML/DDL 승인, table creation approval 등 기본 workflow 정책을 생성한다.

**기본 정책**:
- Signup: 승인 필수
- Permission request: 매니저 승인 필수
- DML: DBA 승인 필수 (PROD 환경)
- DDL: DBA + Security 승인
- Table creation: Keyspace owner + DBA 승인

**체크리스트**:
- [ ] WorkflowPolicy 모델 정의
- [ ] 각 정책별 승인 단계 설정
- [ ] 우선순위 설정
- [ ] 정책 생성 로직

---

### Feature 7. 초기 Audit Log 저장
**설명**: 초기화 완료, super admin 생성, cluster 등록 등의 bootstrap audit log를 저장한다.

**기록할 이벤트**:
- Bootstrap started
- Metadata keyspace created
- Super admin created
- System roles created
- Default policies created
- Bootstrap completed

**체크리스트**:
- [ ] AuditLog 모델 정의
- [ ] 각 단계별 audit 이벤트 기록
- [ ] Actor를 "System" 으로 설정
- [ ] 상세 정보 저장

---

## 📋 체크리스트

- [ ] Feature 1 완료
- [ ] Feature 2 완료
- [ ] Feature 3 완료
- [ ] Feature 4 완료
- [ ] Feature 5 완료
- [ ] Feature 6 완료
- [ ] Feature 7 완료
- [ ] 전체 seed 데이터 생성 테스트

## 🔗 관련 문서

- [Milestone 2: Metadata Cassandra 초기화](./milestone-2.md)
- [Milestone 4: 최초 관리 대상 Cluster 등록](./milestone-4.md)

---

**예상 소요 시간**: 1-2주
