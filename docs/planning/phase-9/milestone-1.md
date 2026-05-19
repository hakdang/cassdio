# Phase 9 - Milestone 1: Table Creation Request

## 개요

CREATE TABLE 요청을 단순 CQL 문자열이 아니라 운영 검수 가능한 구조화 데이터로 받습니다. 요청에는 테이블 설계, owner, 예상 사용량, 컬럼별 데이터 등급, 마스킹 룰 후보가 포함됩니다.

## 요청 Payload 예시

```json
{
  "clusterId": "prod-a",
  "keyspace": "commerce",
  "tableName": "orders_by_user",
  "ownerTeam": "commerce-platform",
  "description": "사용자별 주문 조회 테이블",
  "expectedUsage": {
    "estimatedRows": 100000000,
    "readPattern": "USER_PARTITION_LOOKUP",
    "writePattern": "ORDER_EVENT_APPEND",
    "retentionDays": 1095
  },
  "primaryKey": {
    "partitionKeys": ["user_id"],
    "clusteringKeys": ["ordered_at", "order_id"],
    "clusteringOrder": {
      "ordered_at": "DESC"
    }
  },
  "columns": [
    {
      "name": "user_id",
      "type": "uuid",
      "description": "사용자 ID",
      "classification": "INTERNAL",
      "maskingRule": null
    },
    {
      "name": "buyer_email",
      "type": "text",
      "description": "구매자 이메일",
      "classification": "CONFIDENTIAL",
      "maskingRule": {
        "ruleType": "EMAIL",
        "preview": "ab***@example.com",
        "appliesTo": ["QUERY_RESULT", "EXPORT", "AUDIT_DETAIL"]
      }
    }
  ],
  "tableOptions": {
    "compaction": "SizeTieredCompactionStrategy",
    "compression": "LZ4Compressor",
    "defaultTimeToLive": 0,
    "gcGraceSeconds": 864000
  }
}
```

## 컬럼 분류

| Classification | 의미 | 기본 처리 |
|---|---|---|
| PUBLIC | 공개되어도 무방한 값 | 마스킹 불필요 |
| INTERNAL | 내부 식별자 또는 운영 데이터 | 권한에 따라 표시 |
| CONFIDENTIAL | 개인정보, 고객 식별 정보, 계약 정보 | 마스킹 룰 권장 또는 필수 |
| RESTRICTED | 토큰, secret, 결제 정보, 법적 민감 정보 | 마스킹 룰 필수, export 승인 필수 |

## 포함된 Features

### Feature 1. Table Create Request 모델 구현
**설명**: CREATE TABLE 요청 정보를 workflow payload와 별도 조회용 테이블에 저장한다.

**체크리스트**:
- [ ] request id와 workflow id 연결
- [ ] target cluster/keyspace/table 저장
- [ ] owner, description, expected usage 저장
- [ ] status, risk level, latest review result 저장

---

### Feature 2. CREATE TABLE CQL Parser 연동
**설명**: CREATE TABLE CQL에서 table name, columns, primary key, table options를 추출한다.

**체크리스트**:
- [ ] column name/type 추출
- [ ] partition/clustering key 추출
- [ ] table option 추출
- [ ] wizard 입력값과 CQL 불일치 검증

---

### Feature 3. Table Creation Wizard UI 구현
**설명**: 컬럼, primary key, table option, owner, 민감도, 마스킹 룰을 단계별로 입력한다.

**체크리스트**:
- [ ] 기본 정보 step
- [ ] column definition step
- [ ] primary key design step
- [ ] table option step
- [ ] column classification / masking rule step
- [ ] review & submit step

---

### Feature 4. Column Classification 입력 구현
**설명**: 각 컬럼의 데이터 등급을 지정하고 catalog seed 정보로 저장한다.

**체크리스트**:
- [ ] PUBLIC / INTERNAL / CONFIDENTIAL / RESTRICTED 등급 제공
- [ ] 등급별 필수 설명 정책
- [ ] 민감 등급 컬럼 highlight
- [ ] catalog metadata payload 생성

---

### Feature 5. Masking Rule Binding 입력 구현
**설명**: 특정 컬럼에 어떤 마스킹 룰을 적용할지 지정한다.

**체크리스트**:
- [ ] masking rule type 선택
- [ ] role/action별 적용 조건 설정
- [ ] query result/export/audit 적용 범위 선택
- [ ] sample value preview

---

### Feature 6. Request Snapshot 저장
**설명**: 제출 시점의 CQL, 구조화 payload, column classification, masking rule binding을 보존한다.

**체크리스트**:
- [ ] submitted snapshot 생성
- [ ] revision마다 snapshot version 증가
- [ ] 승인 시점 snapshot lock
- [ ] audit와 workflow detail에서 snapshot 조회

## 완료 기준

- [ ] 테이블 생성 요청에 컬럼별 데이터 등급과 마스킹 룰 후보가 포함된다.
- [ ] 민감 컬럼이 있는데 마스킹 룰이 없으면 자동 검수 finding이 생성된다.
- [ ] 요청 payload는 workflow, approval, catalog seed에서 재사용 가능하다.
