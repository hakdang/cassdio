# Phase 9 - Milestone 2: Table Design 자동 검수

## 개요

CREATE TABLE 요청의 데이터 모델링 품질과 운영 위험을 자동 검수합니다.

## 포함된 Features

### Feature 1. Table Name Convention 검증
**설명**: table/column 이름이 조직 naming convention과 Cassandra 제약을 만족하는지 확인한다.

**체크리스트**:
- [ ] naming rule 설정 모델
- [ ] reserved keyword 검증
- [ ] prefix/suffix 정책 검증
- [ ] finding message 제공

---

### Feature 2. Primary Key 검증
**설명**: partition key와 clustering key 설계가 유효한지 검토한다.

**체크리스트**:
- [ ] partition key 존재 여부 확인
- [ ] hot partition 위험 입력값 검토
- [ ] clustering order 검토
- [ ] full scan 위험 finding 생성

---

### Feature 3. Table Option 검증
**설명**: compaction, compression, TTL, gc_grace_seconds 등 운영 옵션을 검토한다.

**체크리스트**:
- [ ] compaction strategy allowlist
- [ ] compression 설정 검증
- [ ] TTL/gc_grace_seconds risk 판단
- [ ] PROD 환경 critical option 승인 요구

---

### Feature 4. Sensitive Data 검증
**설명**: 민감 컬럼 선언과 masking/export policy 필요 여부를 판단한다.

**체크리스트**:
- [ ] sensitive column 입력 확인
- [ ] 기본 masking policy 연결
- [ ] security reviewer 필요 여부 산출
- [ ] catalog seed payload 생성

---

### Feature 5. Masking Rule 누락 검증
**설명**: CONFIDENTIAL 또는 RESTRICTED 컬럼에 마스킹 룰이 지정되어 있는지 확인한다.

**체크리스트**:
- [ ] classification별 masking rule 필수 여부 정의
- [ ] masking rule 미지정 finding 생성
- [ ] 적용 범위 누락(query/export/audit) finding 생성
- [ ] NEEDS_REVISION 전환 조건 연결

---

### Feature 6. Masking Preview 검증
**설명**: 요청자가 지정한 masking rule이 샘플 값에 기대한 형태로 적용되는지 검증한다.

**체크리스트**:
- [ ] sample value 기반 preview 생성
- [ ] email/phone/card/token 타입별 validation
- [ ] irreversible masking rule 여부 표시
- [ ] 승인 상세 화면에 preview 노출

## 완료 기준

- [ ] CREATE TABLE 요청에 design finding과 risk level이 생성된다.
- [ ] 위험도가 높은 table은 추가 승인 단계가 요구된다.
- [ ] 민감 컬럼의 masking rule 누락은 승인 전 보완 요청으로 연결된다.
