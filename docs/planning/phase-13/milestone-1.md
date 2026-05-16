# Phase 13 - Milestone 1: Data Protection

## 개요

데이터 보호는 테이블 생성 요청 단계에서 지정한 column classification과 masking rule을 기반으로 동작합니다. 같은 컬럼이라도 사용자 role, action, 화면, export 여부에 따라 원본 표시 또는 마스킹 표시가 달라질 수 있습니다.

## Masking Rule 종류

| Rule Type | 예시 원본 | 표시 예시 | 설명 |
|---|---|---|---|
| FULL | `secret-value` | `******` | 전체 값을 숨긴다. |
| PARTIAL | `abcdef1234` | `abc****234` | 앞/뒤 일부만 노출한다. |
| EMAIL | `user@example.com` | `us***@example.com` | 이메일 local part를 부분 마스킹한다. |
| PHONE | `01012345678` | `010****5678` | 전화번호 중간 자리를 숨긴다. |
| CARD | `1234567812345678` | `123456******5678` | 카드/계좌류 숫자를 부분 마스킹한다. |
| HASH | `customer-1` | `sha256:...` | 원본 복원이 불가능한 hash로 표시한다. |
| TOKEN_PREFIX | `sk_live_xxxxx` | `sk_live_****` | token prefix만 남기고 숨긴다. |
| NULLIFY | `value` | `null` | 권한이 없으면 값 자체를 null로 반환한다. |

## Masking Rule Binding 기준

| 기준 | 설명 |
|---|---|
| resource | cluster/keyspace/table/column 단위로 적용 |
| action | QUERY_RESULT, EXPORT, AUDIT_DETAIL, API_RESPONSE 등에 적용 |
| principal | role, user, workspace 기준 예외 허용 |
| condition | PROD 환경, 대량 export, 외부 공유 여부 등 조건 |
| precedence | column rule이 table default rule보다 우선 |

## 포함된 Features

### Feature 1. Sensitive Data Type 모델 구현
**설명**: email, phone, card number, token 등 민감정보 유형을 정의한다.

**체크리스트**:
- [ ] sensitive data type enum 정의
- [ ] classification과 sensitive type 매핑
- [ ] 기본 masking rule 추천
- [ ] table creation request와 catalog에서 재사용

---

### Feature 2. Column Masking Policy 구현
**설명**: role과 resource에 따라 column 값을 마스킹하는 정책을 구현한다.

**체크리스트**:
- [ ] masking rule 모델 정의
- [ ] masking rule binding 모델 정의
- [ ] role/action/resource 조건 평가
- [ ] 원본 표시 권한 예외 처리

---

### Feature 3. Result Masking Engine 구현
**설명**: query result grid, API response, export pipeline에서 동일한 masking engine을 사용한다.

**체크리스트**:
- [ ] row/column 단위 masking 적용
- [ ] collection/UDT 내부 값 masking 지원
- [ ] masking applied metadata 반환
- [ ] export와 query result 동일 규칙 적용

---

### Feature 4. Masking Preview 구현
**설명**: 테이블 생성 요청 또는 catalog 설정 화면에서 샘플 값 기준 masking 결과를 보여준다.

**체크리스트**:
- [ ] masking preview API
- [ ] sample value 입력
- [ ] rule별 결과 표시
- [ ] role/action별 preview 비교

---

### Feature 5. Masking Audit 구현
**설명**: 민감 컬럼이 마스킹되어 표시되었는지 또는 원본으로 표시되었는지 audit에 남긴다.

**체크리스트**:
- [ ] masking applied flag 저장
- [ ] original value는 audit에 저장하지 않음
- [ ] 원본 조회 권한 사용 이력 저장
- [ ] export masking 여부 저장

## 완료 기준

- [ ] 테이블 생성 시 지정한 컬럼별 masking rule이 catalog와 policy에 저장된다.
- [ ] query result와 export에서 동일한 masking rule이 적용된다.
- [ ] 사용자는 어떤 컬럼이 어떤 rule로 마스킹되었는지 확인할 수 있다.
