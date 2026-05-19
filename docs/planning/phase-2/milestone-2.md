# Phase 2 - Milestone 2: Metadata Cassandra 초기화

## 📝 개요

메타데이터를 저장할 Cassandra keyspace를 생성하고, schema migration 시스템을 구축합니다. 다중 인스턴스 환경에서 안전한 초기화를 위한 Lock 메커니즘도 구현합니다.

## ✅ 포함된 Features

### Feature 1. cassdio_meta Keyspace 생성 기능 구현
**설명**: metadata keyspace가 없으면 replication 설정에 따라 자동 생성한다.

**설정 옵션**:
- Replication factor 설정
- Replication strategy (SimpleStrategy/NetworkTopologyStrategy)
- Durable writes 설정

**체크리스트**:
- [x] KeyspaceCreationService 구현
- [x] CQL 스크립트 작성
- [x] 기존 keyspace 확인 로직
- [x] 생성 실패 시 롤백 처리

> Cassandra DDL은 transaction rollback을 제공하지 않으므로, `CREATE KEYSPACE IF NOT EXISTS` 기반 idempotent 생성과 bootstrap 실패 시 installation state 미완료/lock failed 기록으로 재시도 가능하게 처리한다.

---

### Feature 2. Metadata Schema Migration 구조 구현
**설명**: Cassandra 기반 schema migration 테이블과 migration 실행 구조를 구현한다.

**Migration Table**:
```
CREATE TABLE cassdio_meta.schema_migrations (
  version TEXT PRIMARY KEY,
  description TEXT,
  executed_at TIMESTAMP,
  execution_time INT,
  success BOOLEAN
)
```

**체크리스트**:
- [x] SchemaMigration 테이블 정의
- [x] Migration 스크립트 관리 구조
- [x] Migration 실행 엔진
- [x] Migration 이력 저장
- [ ] Rollback 지원 (선택사항)

> Rollback은 Cassandra schema migration 운영 특성상 선택사항으로 남긴다. 실패 migration은 `success = false`로 기록되고 재시도 시 다시 실행 가능하다.

---

### Feature 3. Bootstrap Lock 구현
**설명**: 다중 인스턴스에서 중복 초기화를 막기 위한 bootstrap lock을 구현한다.

**구현 방식**:
- Cassandra LWT (Lightweight Transaction) 활용
- Lock 테이블 생성
- 타임아웃 및 자동 해제 메커니즘

**체크리스트**:
- [x] BootstrapLock 테이블 정의
- [x] LWT 기반 lock 획득 로직
- [x] 타임아웃 처리
- [x] 장애 시 lock 해제 메커니즘

---

### Feature 4. Installation State 저장 기능 구현
**설명**: 설치 ID, bootstrap 완료 여부, schema version 등을 저장한다.

**저장할 정보**:
- Installation ID (UUID)
- Bootstrap completion time
- Schema version
- Cassdio version
- 초기 설정값들

**체크리스트**:
- [x] InstallationState 테이블 정의
- [x] 상태 저장 로직
- [x] 상태 조회 API
- [x] 상태 업데이트 로직

---

### Feature 5. Idempotent Seed 구조 구현
**설명**: 초기화 실패 후 재시도해도 중복 데이터가 생성되지 않도록 seed 로직을 구성한다.

**구현 전략**:
- 모든 seed 데이터에 고유 ID 할당
- Upsert 기반 저장 (insert if not exists)
- Idempotency key 활용

**체크리스트**:
- [x] Seed 데이터 정의
- [x] Idempotent 저장 로직
- [x] 중복 감지 및 처리
- [x] Seed 실패 시 재시도 로직

---

## 📋 체크리스트

- [x] Feature 1 완료
- [x] Feature 2 완료
- [x] Feature 3 완료
- [x] Feature 4 완료
- [x] Feature 5 완료
- [x] 다중 인스턴스 환경 테스트

> 다중 인스턴스 검증은 LWT lock 획득 실패, release 후 재획득, bootstrap table 생성 전후 순서에 대한 단위 테스트로 우선 보장한다.

## 🔗 관련 문서

- [Milestone 1: Metadata DB 접근 기반 구현](./milestone-1.md)
- [Milestone 3: 초기 기본 데이터 생성](./milestone-3.md)

---

**예상 소요 시간**: 1-2주
