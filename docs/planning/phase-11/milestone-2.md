# Phase 11 - Milestone 2: Audit 조회 UI

## 개요

Audit log를 운영자가 검색, 상세 확인, timeline 추적, 제한된 export까지 수행할 수 있게 합니다.

## 포함된 Features

### Feature 1. Audit Log 목록 API 구현
**설명**: 기간, 사용자, action, resource, decision, risk 기준으로 audit log를 조회한다.

**체크리스트**:
- [ ] cursor pagination
- [ ] filter 조건 정의
- [ ] 정렬 기준 정의
- [ ] 권한별 조회 범위 제한

---

### Feature 2. Audit Log Detail UI 구현
**설명**: 특정 audit event의 상세 정보를 보여준다.

**체크리스트**:
- [ ] actor/action/resource 표시
- [ ] masked CQL 표시
- [ ] request/workflow 연결
- [ ] result/error detail 표시

---

### Feature 3. Timeline View 구현
**설명**: review, approval, execution, failure를 correlation id 기준으로 묶어 보여준다.

**체크리스트**:
- [ ] correlation id 조회
- [ ] workflow decision timeline
- [ ] query execution timeline
- [ ] schema/data diff 연결

---

### Feature 4. Audit Export 제한
**설명**: audit export는 보안 권한과 별도 승인 정책을 적용한다.

**체크리스트**:
- [ ] export permission 검증
- [ ] sensitive field masking
- [ ] export approval 연동
- [ ] download audit 저장

## 완료 기준

- [ ] 주요 운영 행위를 UI에서 검색하고 상세 추적할 수 있다.
- [ ] audit export도 별도 audit로 남는다.
