# Phase 2 - Milestone 1: 최초 실행 Bootstrap 설계 구현

## 📝 개요

Cassdio 최초 실행 시 초기화 상태를 확인하고, 메타데이터 Cassandra 연결 정보를 입력 받는 Bootstrap 프로세스를 구현합니다.

## ✅ 포함된 Features

### Feature 1. Bootstrap 상태 확인 API 구현
**설명**: Cassdio가 초기화되어 있는지 확인하는 /api/bootstrap/status API를 구현한다.

**응답 형식**:
```json
{
  "initialized": false,
  "status": "PENDING_METADATA_CONFIG",
  "metadata_cassandra_configured": false
}
```

**체크리스트**:
- [ ] BootstrapController 작성
- [ ] BootstrapService 구현
- [ ] 상태 저장소 구현
- [ ] API 테스트 작성

---

### Feature 2. Metadata Cassandra 연결 정보 입력 UI 구현
**설명**: 콘솔용 Cassandra 연결 정보를 입력하는 setup wizard 화면을 구현한다.

**입력 필드**:
- Contact points (호스트:포트)
- Username / Password
- SSL 설정
- Datacenter 선택

**체크리스트**:
- [ ] Bootstrap Setup Wizard UI 작성
- [ ] Form validation 구현
- [ ] 입력 필드별 안내 메시지
- [ ] Next/Back 네비게이션

---

### Feature 3. Metadata Cassandra 연결 테스트 API 구현
**설명**: 입력한 Cassandra 연결 정보로 system.local 등을 조회해 연결 가능 여부를 검증한다.

**테스트 항목**:
- 네트워크 연결성
- 인증 정보 검증
- system.local 조회
- 권한 확인

**체크리스트**:
- [ ] /api/bootstrap/test-connection API 구현
- [ ] Cassandra driver 연결 테스트
- [ ] 에러 처리 및 메시지 반환
- [ ] 타임아웃 설정

---

### Feature 4. Bootstrap Token 검증 기능 구현
**설명**: 초기화 API 보호를 위해 서버에서 발급한 bootstrap token 검증 기능을 구현한다.

**기능**:
- 서버 시작 시 일회용 token 생성
- Token 기반 API 접근 제어
- 초기화 완료 후 token 만료

**체크리스트**:
- [ ] BootstrapTokenProvider 구현
- [ ] Token 생성 및 저장 로직
- [ ] 인터셉터 또는 필터로 token 검증
- [ ] Token 만료 처리

---

### Feature 5. Bootstrap Setup Wizard 구현
**설명**: 초기 실행 시 metadata Cassandra, super admin, workspace, initial cluster 정보를 입력하는 wizard를 구현한다.

**Wizard 단계**:
1. Metadata Cassandra 연결 설정
2. Super Admin 계정 생성
3. Workspace 정보 입력
4. 초기 Cluster 정보 입력 (선택사항)
5. 초기화 완료 확인

**체크리스트**:
- [ ] Multi-step 형태의 wizard 구현
- [ ] 각 단계별 데이터 임시 저장
- [ ] 이전 단계로 돌아가기 기능
- [ ] 진행률 표시
- [ ] 완료 후 결과 화면

---

## 📋 체크리스트

- [ ] Feature 1 완료
- [ ] Feature 2 완료
- [ ] Feature 3 완료
- [ ] Feature 4 완료
- [ ] Feature 5 완료
- [ ] 전체 Integration 테스트

## 🔗 관련 문서

- [Milestone 2: Metadata Cassandra 초기화](./milestone-2.md)

---

**예상 소요 시간**: 1-2주
