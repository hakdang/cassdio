# Phase 3 - Milestone 4: Permission Engine

## ✅ 포함된 Features

### Feature 1. Resource Path 규칙 구현
**설명**: cluster:{id}/keyspace:{name}/table:{name} 형태의 resource path 규칙을 구현한다.

**체크리스트**:
- [ ] ResourcePath 모델 정의
- [ ] Parser 구현
- [ ] Validator 구현

---

### Feature 2. Permission Evaluator 구현
**설명**: 로그인한 member의 role assignment와 permission binding을 기반으로 권한을 평가한다.

**체크리스트**:
- [ ] PermissionEvaluator 구현
- [ ] 권한 캐싱
- [ ] 평가 로직

---

### Feature 3. Explicit DENY 우선 처리 구현
**설명**: 명시적 DENY가 ALLOW보다 우선하도록 권한 평가 규칙을 구현한다.

**체크리스트**:
- [ ] DENY 우선 처리 로직
- [ ] 테스트 케이스

---

### Feature 4. Scope 상속 평가 구현
**설명**: cluster, keyspace, table, column 계층 권한을 평가할 수 있도록 한다.

**체크리스트**:
- [ ] 계층 구조 정의
- [ ] 상속 로직 구현

---

### Feature 5. Effective Permission 조회 API 구현
**설명**: 특정 member의 최종 유효 권한을 조회하는 API를 구현한다.

**체크리스트**:
- [ ] API 구현
- [ ] 응답 포맷 정의

---

### Feature 6. Permission Simulator 구현
**설명**: member, action, resource를 입력하면 허용/거부 사유를 설명하는 시뮬레이터를 구현한다.

**체크리스트**:
- [ ] Simulator API 구현
- [ ] Simulator UI 구현
- [ ] 상세 설명 제공

---

### Feature 7. Auth Principal 연동
**설명**: JWT 인증 filter에서 만든 MemberPrincipal을 permission evaluator 입력으로 사용한다.

**체크리스트**:
- [ ] MemberPrincipal 모델 정의
- [ ] workspace context 검증
- [ ] session status 검증
- [ ] permission denied audit 저장

## 📋 체크리스트

- [ ] 모든 Features 완료
- [ ] 통합 테스트

---

**예상 소요 시간**: 2-3주
