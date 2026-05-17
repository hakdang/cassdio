# Cassdio - Kotlin Spring Boot Multi-Project Setup

## 프로젝트 구조

```
cassdio/
├── cassdio-core/           # 핵심 비즈니스 로직
│   ├── src/main/kotlin
│   │   └── kr/hakdang/cassdio/core
│   │       ├── config/     # 설정 클래스
│   │       ├── domain/     # 도메인 모델
│   │       ├── repository/ # 저장소 인터페이스
│   │       ├── service/    # 서비스 로직
│   │       └── util/       # 유틸리티
│   ├── src/test/kotlin
│   └── build.gradle
│
├── cassdio-web/            # 웹 애플리케이션
│   ├── src/main/kotlin
│   │   └── kr/hakdang/cassdio/web
│   │       ├── config/     # 웹 설정 (CORS, MVC, 예외처리)
│   │       ├── controller/  # REST 컨트롤러
│   │       └── dto/        # 데이터 전송 객체
│   ├── src/test/kotlin
│   ├── src/main/resources
│   │   ├── application.yml
│   │   └── logback-spring.xml
│   └── build.gradle
│
├── build.gradle            # 멀티프로젝트 설정
├── settings.gradle         # 모듈 설정
└── gradle/                 # Gradle 래퍼
```

## 주요 설정 사항

### ✅ 완료된 설정

1. **Gradle 업그레이드**
   - Gradle: 8.11
   - Spring Boot: 3.4.2
   - Java: 21

2. **Kotlin 지원**
   - Kotlin JVM: 2.1.10
   - Kotlin Spring Plugin: 2.1.10
   - Jackson Kotlin 모듈 추가

3. **멀티프로젝트 구조**
   - cassdio-core: 기본 라이브러리 모듈
   - cassdio-web: 메인 Spring Boot 웹 애플리케이션

4. **Core 모듈 기능**
   - Cassandra 드라이버 통합
   - 캐시 설정 (Caffeine)
   - 기본 엔티티 및 저장소 인터페이스
   - 로거 유틸

5. **Web 모듈 기능**
   - Spring MVC 설정
   - CORS 설정
   - 요청 로깅 인터셉터
   - 전역 예외 처리
   - REST API 응답 래퍼
   - 페이징 DTO

6. **로깅**
   - SLF4J + Logback
   - 파일/콘솔 로깅
   - 프로필별 설정 (dev, prod)

## 빌드 및 실행

### 빌드
\`\`\`bash
./gradlew build
\`\`\`

### 테스트 실행
\`\`\`bash
./gradlew test
\`\`\`

### 애플리케이션 실행
\`\`\`bash
./gradlew bootRun
\`\`\`

### JAR 파일 생성 및 실행
\`\`\`bash
./gradlew bootJar
java -jar cassdio-web/build/libs/cassdio.jar
\`\`\`

## API 엔드포인트

### 헬스 체크
\`\`\`bash
GET /api/health
\`\`\`

### 애플리케이션 정보
\`\`\`bash
GET /api/info
\`\`\`

## Kotlin 확장 함수

### 로거 사용

\`\`\`kotlin
// 어디서나 사용 가능
val logger = LoggerUtil.getLogger(MyClass::class.java)

// 또는 인라인 함수 사용
inline fun <reified T> T.logger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}
\`\`\`

## 의존성

### Core 모듈
- Spring Boot Starter
- DataStax Java Driver (Cassandra)
- Jackson (JSON 처리)
- Apache Commons (Lang, Collections, CSV)
- Google Guava
- Caffeine (캐싱)
- Lombok

### Web 모듈
- Spring Boot Starter Web
- Spring Boot Starter Validation
- Spring Boot Starter Actuator
- Jackson Kotlin 모듈

## 개발 가이드

### 새로운 Entity 생성
\`\`\`kotlin
package kr.hakdang.cassdio.core.domain

data class MyEntity(
    override val id: String? = null,
    val name: String,
    override val createdAt: Long = System.currentTimeMillis(),
    override val updatedAt: Long = System.currentTimeMillis()
) : BaseEntity
\`\`\`

### 새로운 Repository 생성
\`\`\`kotlin
package kr.hakdang.cassdio.core.repository

class MyRepository : BaseRepository<MyEntity, String> {
    override fun findById(id: String): MyEntity? {
        // 구현
        return null
    }
    
    override fun findAll(): List<MyEntity> {
        // 구현
        return emptyList()
    }
    
    // 나머지 메서드 구현...
}
\`\`\`

### 새로운 Service 생성
\`\`\`kotlin
package kr.hakdang.cassdio.core.service

import org.springframework.stereotype.Service

@Service
class MyService(private val repository: MyRepository) : BaseService<MyEntity, String> {
    override fun getById(id: String): MyEntity? {
        return repository.findById(id)
    }
    
    // 나머지 메서드 구현...
}
\`\`\`

### 새로운 Controller 생성
\`\`\`kotlin
package kr.hakdang.cassdio.web.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kr.hakdang.cassdio.web.dto.ApiResponse

@RestController
@RequestMapping("/api/my")
class MyController(private val service: MyService) {
    
    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ResponseEntity<ApiResponse<MyEntity>> {
        val data = service.getById(id)
        return if (data != null) {
            ResponseEntity.ok(ApiResponse.success(data))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
\`\`\`

## 프로필별 설정

### Development 실행
\`\`\`bash
./gradlew bootRun --args='--spring.profiles.active=dev'
\`\`\`

### Production 실행
\`\`\`bash
./gradlew bootRun --args='--spring.profiles.active=prod'
\`\`\`

## 문제 해결

### Gradle 캐시 초기화
\`\`\`bash
./gradlew clean
\`\`\`

### Gradle Wrapper 업데이트
\`\`\`bash
./gradlew wrapper
\`\`\`

## 라이선스
MIT License
