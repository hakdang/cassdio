# Phase 2 - Milestone 1: Metadata DB 접근 기반 구현

## 개요

기본 기능 구현을 먼저 진행하기 위해 최초 실행 Bootstrap, setup wizard, bootstrap token 처리는 뒤 Phase로 이관합니다.

이번 Milestone에서는 임시 Metadata DB 설정 인터페이스를 만들고, 애플리케이션의 기본 기능들이 해당 인터페이스를 통해 Metadata DB 정보를 조회해 동작할 수 있도록 기반을 구성합니다. 이후 최초 실행 과제가 진행되면 동일 인터페이스의 구현체를 bootstrap 결과 기반으로 교체합니다.

## 설계 방향

- 최초 실행 여부 판단, wizard, super admin/workspace/initial cluster 입력 흐름은 이번 Milestone 범위에서 제외한다.
- Metadata DB 연결 정보는 임시 인터페이스를 통해 조회한다.
- 서비스 코드는 설정값의 출처를 알지 않고 인터페이스에만 의존한다.
- 초기 구현은 application config, environment variable, test fixture 등 기본 기능 개발에 충분한 출처를 사용할 수 있다.
- 뒤 Phase에서 최초 실행 처리가 완료되면 인터페이스 구현체만 변경해 bootstrap 저장소의 설정을 사용한다.

## 포함된 Features

### Feature 1. Metadata DB 설정 조회 인터페이스 구현
**설명**: Metadata DB 연결 정보를 제공하는 임시 인터페이스를 정의한다.

**제공 정보**:
- Contact points
- Port
- Local datacenter
- Keyspace
- Username / Password 또는 credential reference
- TLS 사용 여부 및 관련 옵션

**체크리스트**:
- [x] MetadataDbConfigProvider 인터페이스 정의
- [x] MetadataDbConfig 값 객체 정의
- [x] 민감 정보 마스킹/노출 기준 정의
- [x] 단위 테스트 작성

---

### Feature 2. 임시 Metadata DB 설정 구현체 작성
**설명**: 기본 기능 개발 중 사용할 임시 설정 구현체를 작성한다.

**구현 방식**:
- application.yml 기반 기본 구현체
- 환경 변수 override 지원
- 테스트용 in-memory/fake 구현체

**체크리스트**:
- [x] Application property binding 구현
- [x] 기본값 및 필수값 validation
- [x] Test fixture 구현
- [x] 설정 누락 시 명확한 에러 반환

---

### Feature 3. Metadata DB 연결 클라이언트 기반 구현
**설명**: 임시 인터페이스에서 조회한 Metadata DB 정보로 Cassandra session/client를 생성하는 기반을 구현한다.

**처리 항목**:
- Metadata DB 설정 조회
- Cassandra driver session 생성
- Keyspace 선택
- timeout 및 retry 기본값 적용

**체크리스트**:
- [x] MetadataCassandraClientFactory 구현
- [x] 연결 lifecycle 관리
- [x] 설정 변경 시 재연결 전략 초안 정의
- [x] 연결 실패 예외 모델 정의

---

### Feature 4. Metadata DB 상태 조회 API 구현
**설명**: 현재 Metadata DB 설정과 연결 상태를 확인할 수 있는 내부 API를 구현한다.

**응답 예시**:
```json
{
  "configured": true,
  "source": "APPLICATION_CONFIG",
  "keyspace": "cassdio_meta",
  "connected": true,
  "schema_version": null
}
```

**체크리스트**:
- [x] /api/metadata/status API 구현
- [x] 연결 가능 여부 확인
- [x] 설정 source 표시
- [x] 민감 정보 응답 제외
- [x] API 테스트 작성

---

### Feature 5. 기본 기능에서 Metadata DB 설정 인터페이스 사용
**설명**: 이후 Phase의 기본 기능들이 Metadata DB 정보를 직접 설정 파일에서 읽지 않고 임시 인터페이스를 통해 조회하도록 사용 패턴을 확립한다.

**적용 기준**:
- Metadata DB 접근이 필요한 service는 MetadataDbConfigProvider에 의존한다.
- bootstrap 완료 여부나 최초 실행 상태를 기본 기능 코드에 섞지 않는다.
- 뒤 Phase의 bootstrap 구현체 교체를 고려해 인터페이스 계약을 작게 유지한다.

**체크리스트**:
- [x] 서비스 의존성 주입 패턴 정리
- [x] 샘플 service 또는 repository 적용
- [x] 테스트에서 fake provider 사용
- [x] 구현체 교체 시나리오 문서화

---

## 이번 Milestone에서 제외

- 최초 실행 wizard
- `/api/bootstrap/status`
- `/api/bootstrap/test-connection`
- Bootstrap token 검증
- Super Admin, Workspace, 초기 Cluster 입력 흐름
- Metadata keyspace 자동 생성 및 migration 실행

위 항목은 Metadata DB 접근 기반과 기본 기능 구현이 안정화된 뒤 별도 Phase/Milestone에서 진행합니다.

## 구현 확인 결과

- `MetadataDbConfigProvider`, `MetadataDbConfig`, `MetadataDbProperties`로 설정 조회 인터페이스와 application property binding이 구현되어 있다.
- `MetadataDbConfig.masked()`와 `/api/metadata/status` 응답 구조는 password/credential을 노출하지 않는다.
- `CassandraCqlExecutor`가 Cassandra driver session 생성과 lifecycle 관리를 담당한다. 문서의 `MetadataCassandraClientFactory` 항목은 별도 클래스명 대신 CQL executor adapter로 구현된 것으로 본다.
- `CassandraCqlExecutor`는 현재 config와 active session config를 비교하고, 설정이 변경되면 기존 session을 닫은 뒤 새 session을 생성한다.
- `MetadataStatusService`와 `MetadataController`로 `/api/metadata/status`가 구현되어 있으며, 연결 여부와 설정 source를 반환한다.
- bootstrap/migration repository와 service들이 `MetadataDbConfigProvider`에 의존하므로 설정 출처 교체 가능성이 검증된다.
- `MetadataControllerTests`로 status API 응답과 credential 미노출을 검증한다.

## 체크리스트

- [x] Feature 1 완료
- [x] Feature 2 완료
- [x] Feature 3 완료
- [x] Feature 4 완료
- [x] Feature 5 완료
- [x] 인터페이스 교체 가능성 검증

## 관련 문서

- [Milestone 2: Metadata Cassandra 초기화](./milestone-2.md)

---

**예상 소요 시간**: 1주
