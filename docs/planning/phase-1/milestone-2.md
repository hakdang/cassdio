# Phase 1 - Milestone 2: 공통 Backend 구조 구성

## 📝 개요

모든 백엔드 API에서 사용될 공통 구조를 정의합니다. 표준화된 응답 형식, 에러 코드 체계, 전역 예외 처리, 검증 구조, 테스트 기반 API 문서화를 구성합니다.

## ✅ 포함된 Features

### Feature 1. 공통 API Response 모델 정의
**설명**: 성공/실패 응답 구조를 표준화한다.

**요구사항**:
```kotlin
// 성공 응답
{
  "code": "200",
  "message": "Success",
  "data": { ... },
  "timestamp": 1234567890
}

// 실패 응답
{
  "code": "E001",
  "message": "Invalid request",
  "timestamp": 1234567890
}
```

**체크리스트**:
- [ ] ApiResponse<T> 제네릭 클래스 작성
- [ ] 성공/실패 응답 빌더 메서드 구현
- [ ] 페이징 응답 모델 정의
- [ ] 공통 DTO 작성 및 테스트

---

### Feature 2. 공통 Error Code 체계 정의
**설명**: API 에러 코드, 메시지, HTTP status 매핑 체계를 정의한다.

**예시**:
| 코드 | 메시지 | HTTP Status |
|------|--------|-------------|
| E001 | Invalid request | 400 |
| E002 | Unauthorized | 401 |
| E003 | Forbidden | 403 |
| E999 | Internal server error | 500 |

**체크리스트**:
- [ ] ErrorCode enum 정의
- [ ] HTTP Status 매핑 구현
- [ ] 에러 메시지 다국어 지원 구조
- [ ] 에러 로깅 정책 수립

---

### Feature 3. Global Exception Handler 구현
**설명**: Spring Boot 전역 예외 처리 구조를 구현한다.

**처리할 예외**:
- ValidationException
- UnauthorizedException
- ForbiddenException
- NotFoundException
- ConflictException
- RuntimeException
- 기타 일반 예외

**체크리스트**:
- [ ] @RestControllerAdvice 클래스 작성
- [ ] 예외별 @ExceptionHandler 메서드 구현
- [ ] 예외 로깅 및 추적 ID 추가
- [ ] 테스트 케이스 작성

---

### Feature 4. 공통 Validation 구조 구성
**설명**: Request DTO validation과 validation error 응답 구조를 구성한다.

**포함 사항**:
- JSR-380 (Jakarta Bean Validation)
- 커스텀 validator 작성
- Validation error 응답 표준화

**체크리스트**:
- [ ] @Validated 적용
- [ ] ConstraintViolationException 처리
- [ ] 필드별 에러 메시지 구성
- [ ] 다국어 메시지 지원

---

### Feature 5. Spring REST Docs 기반 API 문서화 구성
**설명**: API 테스트 케이스를 기반으로 API 문서를 생성하도록 Spring REST Docs를 설정한다. 테스트가 없는 API는 문서가 생성되지 않도록 하여, 문서와 테스트가 함께 유지되게 한다.

**구성 요소**:
- Spring REST Docs 의존성 추가
- MockMvc 또는 WebTestClient 기반 문서화 테스트
- Asciidoctor 기반 문서 조립
- REST Docs snippets 생성
- 테스트 성공 시에만 API docs 산출

**체크리스트**:
- [ ] Spring REST Docs 라이브러리 추가
- [ ] Asciidoctor Gradle task 구성
- [ ] 공통 request/response snippet 규칙 정의
- [ ] 공통 error response 문서화 테스트 작성
- [ ] health/version API 문서화 테스트 작성
- [ ] 문서화 테스트가 실패하면 docs build도 실패하도록 구성
- [ ] 테스트가 없는 API는 docs에 포함하지 않는 원칙 문서화

**문서 생성 원칙**:

```text
API Test Case 작성
  -> REST Docs snippet 생성
  -> Asciidoctor 문서 조립
  -> build/docs/asciidoc 산출
```

새 API를 추가할 때는 다음을 반드시 포함한다.

- 정상 응답 테스트
- validation 실패 테스트
- 인증/권한 실패 테스트가 필요한 API라면 401/403 테스트
- request fields / response fields / path parameters / query parameters snippet

---

## 📋 체크리스트

- [ ] Feature 1 완료
- [ ] Feature 2 완료
- [ ] Feature 3 완료
- [ ] Feature 4 완료
- [ ] Feature 5 완료
- [ ] 통합 테스트 및 문서화

## 🔗 관련 문서

- [Milestone 1: 기본 프로젝트 세팅](./milestone-1.md)
- [Milestone 3: 공통 Frontend Layout 구성](./milestone-3.md)

---

**예상 소요 시간**: 1-2주
