# Phase 4 - Milestone 1: Cluster CRUD

## 개요

기존 Cassdio의 Multi Cassandra Cluster 관리 기능을 기본으로 유지하고, credential 보안, version compatibility, session lifecycle, audit를 보강합니다.

## 기존 기능 Parity

- Multi Cassandra Cluster 등록
- AuthCredentials 기반 cluster 연결
- Cluster 목록/상세 조회
- Cluster 수정/삭제
- 전체 session clear
- 단일 cluster session clear
- Cassandra version compatibility 검증

## 포함된 Features

### Feature 1. Cluster 등록 API 구현
**설명**: 관리 대상 Cassandra 클러스터를 등록할 수 있는 API를 구현한다.

**체크리스트**:
- [ ] contact points, port, local datacenter 입력
- [ ] username/password 또는 secret reference 입력
- [ ] TLS 옵션 입력
- [ ] Cassandra version compatibility 검증
- [ ] 연결 테스트 후 저장

---

### Feature 2. Cluster 목록 / 상세 API 구현
**설명**: 등록된 cluster 목록과 상세 정보를 조회한다.

**체크리스트**:
- [ ] password/secret 값은 기본 응답에서 제외
- [ ] environment, owner, version, session status 표시
- [ ] 최근 연결 오류 표시
- [ ] cluster detail API 구현

---

### Feature 3. Cluster 수정 / 삭제 API 구현
**설명**: cluster 설정을 변경하거나 삭제한다.

**체크리스트**:
- [ ] credential rotation 지원
- [ ] contact point/datacenter 변경 지원
- [ ] 삭제 전 session cleanup
- [ ] 삭제 audit 저장

---

### Feature 4. Cluster Session Clear 구현
**설명**: 기존 기능인 전체 session clear와 단일 cluster session clear를 유지하고 운영 audit를 추가한다.

**체크리스트**:
- [ ] POST /cluster/session/clear 기능
- [ ] POST /cluster/{clusterId}/session/clear 기능
- [ ] session clear 전후 상태 audit
- [ ] 권한 검증과 확인 UI

---

### Feature 5. Cluster 관리 UI 구현
**설명**: cluster 등록, 목록, 상세, 수정, 삭제, session clear를 수행하는 화면을 구현한다.

**체크리스트**:
- [ ] Cluster 목록 화면
- [ ] Cluster 등록/수정 modal
- [ ] 연결 테스트 feedback
- [ ] version compatibility 결과 표시
- [ ] session clear action

---

**예상 소요 시간**: 1-2주
