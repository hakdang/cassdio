# Phase 16 - Milestone 3: Release Readiness

## 개요

v2 release 전에 E2E, security scan, performance smoke, release checklist를 완료합니다.

## 포함된 Features

### Feature 1. E2E Test 구성
**설명**: 주요 사용자 플로우를 Playwright 등으로 테스트한다.

**체크리스트**:
- [ ] bootstrap flow
- [ ] cluster 등록 flow
- [ ] schema explorer flow
- [ ] query review/workflow flow

---

### Feature 2. Security Scan 구성
**설명**: dependency scan, secret scan, container scan을 CI에 추가한다.

**체크리스트**:
- [ ] dependency scan
- [ ] secret scan
- [ ] container image scan
- [ ] scan failure policy

---

### Feature 3. Performance Smoke Test 구성
**설명**: 큰 schema, paging, query result grid, metrics polling을 검증한다.

**체크리스트**:
- [ ] large schema load test
- [ ] paging state smoke
- [ ] result grid rendering check
- [ ] metrics polling overhead check

---

### Feature 4. Release Checklist 작성
**설명**: versioning, changelog, migration, rollback, known issues를 정리한다.

**체크리스트**:
- [ ] version/changelog
- [ ] migration guide
- [ ] rollback guide
- [ ] known issues

## 완료 기준

- [ ] release candidate가 주요 flow 테스트와 security scan을 통과한다.
- [ ] 운영 배포와 rollback 절차가 문서화되어 있다.
