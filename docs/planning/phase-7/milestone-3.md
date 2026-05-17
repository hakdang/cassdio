# Phase 7 - Milestone 3: 사람 검수 입력

## 개요

자동 검수로 판단하기 어려운 운영 맥락을 사용자가 입력하고, 승인자가 같은 정보를 기반으로 검토할 수 있게 합니다.

## 포함된 Features

### Feature 1. Human Review Template 모델 구현
**설명**: SELECT, DML, DDL, Export 등 작업 유형별 입력 폼 템플릿을 정의한다.

**체크리스트**:
- [ ] operation type별 필수 필드 정의
- [ ] environment/risk별 추가 필드 조건 정의
- [ ] template version 관리
- [ ] workflow payload와 매핑

---

### Feature 2. DML Review Form 구현
**설명**: 변경 사유, 대상 row 수, before query, rollback plan을 입력한다.

**체크리스트**:
- [ ] 변경 사유 필수화
- [ ] 예상 영향 범위 입력
- [ ] before snapshot 필요 여부 표시
- [ ] rollback CQL 또는 복구 계획 입력

---

### Feature 3. DDL Review Form 구현
**설명**: schema 변경 영향, owner 확인, migration window, rollback strategy를 입력한다.

**체크리스트**:
- [ ] 대상 keyspace/table 표시
- [ ] owner review 필요 여부 표시
- [ ] maintenance window 연결
- [ ] rollback 가능/불가 사유 입력

---

### Feature 4. Evidence 첨부
**설명**: ticket link, incident id, change request id 등 외부 근거를 workflow와 연결한다.

**체크리스트**:
- [ ] evidence URL validation
- [ ] incident/change ticket field 제공
- [ ] 첨부 metadata audit 저장
- [ ] 승인 상세 화면 노출

## 완료 기준

- [ ] 위험 쿼리 제출 시 사람 검수 입력값이 workflow payload에 포함된다.
- [ ] 승인자는 자동 finding과 사람 입력값을 함께 볼 수 있다.
