# Phase 2 - Milestone 4: 최초 관리 대상 Cluster 등록

## 📝 개요

초기 실행 시 관리 대상이 될 첫 번째 Cassandra 클러스터를 등록합니다. 연결 테스트, 정보 저장, 자격증명 암호화, 권한 부여를 수행합니다.

## ✅ 포함된 Features

### Feature 1. Initial Managed Cluster 연결 테스트
**설명**: 관리 대상 Cassandra 클러스터 연결 정보를 검증한다.

**테스트 항목**:
- Contact points 도달 가능성
- 인증 정보 검증
- Cassandra version 확인
- System keyspace 조회 가능성
- 권한 확인 (권한이 충분한지)

**체크리스트**:
- [x] ClusterConnectionService 구현
- [x] 연결 테스트 로직
- [x] 상세한 에러 메시지
- [x] 테스트 결과 반환

---

### Feature 2. Managed Cluster Metadata 저장
**설명**: 초기 관리 대상 cluster 정보를 cassdio_meta에 저장한다.

**저장할 정보**:
- Cluster name
- Environment (DEV, STAGING, PROD)
- Contact points
- Cassandra version
- Keyspace/Table count
- 생성/수정 시간
- Owner 정보

**체크리스트**:
- [x] Cluster 모델 정의
- [x] Cluster 저장소 구현
- [x] 클러스터 정보 저장 로직
- [x] 중복 확인 및 방지

---

### Feature 3. Cluster Credential 암호화 저장
**설명**: 관리 대상 cluster 인증 정보를 암호화해 저장한다.

**암호화 정보**:
- Username
- Password
- SSL keystore/truststore 설정

**암호화 전략**:
- AES-256 기반 암호화
- Master key 별도 관리 (환경변수/Vault)
- 복호화는 필요할 때만

**체크리스트**:
- [x] EncryptionService 구현
- [x] 비밀번호 암호화 로직
- [x] 복호화 로직
- [x] Master key 관리 전략
- [x] 보안 테스트

---

### Feature 4. Initial Cluster DBA 권한 부여 옵션 구현
**설명**: Super Admin에게 최초 등록 클러스터에 대한 DBA 권한을 선택적으로 부여한다.

**권한 부여**:
- Role: DBA
- Scope: 특정 cluster
- 선택사항: Keyspace level 권한도 부여 가능

**체크리스트**:
- [x] 권한 부여 옵션 설정 구현
- [x] RoleAssignment 생성 로직
- [x] 권한 부여 선택 처리
- [x] 권한 이력 기록

---

### Feature 5. 초기 Cluster Health Check 실행
**설명**: 등록된 cluster의 기본 상태, version, keyspace count 등을 확인한다.

**확인할 정보**:
- 노드 상태 (UP/DOWN)
- 노드 수
- Schema agreement 상태
- Keyspace 수
- Table 수
- Pending compactions
- Repairs 상태

**체크리스트**:
- [x] ClusterHealthChecker 구현
- [x] 각 지표 수집 로직
- [x] 초기 값 저장
- [x] Health status 결정 로직
- [x] 알림/경고 설정 (선택 제외)

---

## 📋 체크리스트

- [x] Feature 1 완료
- [x] Feature 2 완료
- [x] Feature 3 완료
- [x] Feature 4 완료
- [x] Feature 5 완료
- [x] 전체 통합 테스트
- [ ] 여러 버전의 Cassandra 호환성 테스트

## 🔗 관련 문서

- [Milestone 3: 초기 기본 데이터 생성](./milestone-3.md)
- [Phase 4 - Milestone 1: Cluster CRUD](../phase-4/milestone-1.md)

---

**예상 소요 시간**: 1-2주
