# Phase 1 - Milestone 1: 기본 프로젝트 세팅

## 📝 개요

프로젝트의 기본 구조를 설정하고, 백엔드와 프론트엔드의 초기 프로젝트를 생성합니다. 로컬 개발을 위한 Docker Compose 환경도 구성합니다.

## ✅ 포함된 Features

### Feature 1. Monorepo 프로젝트 구조 구성
**설명**: 프론트엔드와 백엔드를 하나의 저장소에서 관리할 수 있도록 기본 디렉터리 구조를 구성한다.

**체크리스트**:
- [x] 저장소 초기화 및 기본 폴더 구조 생성
- [x] README.md 작성
- [x] .gitignore 구성
- [x] 라이선스 파일 추가

---

### Feature 2. Spring Boot API 프로젝트 생성
**설명**: Java/Kotlin 기반 Spring Boot API 서버를 생성하고 기본 실행 환경을 구성한다.

**체크리스트**:
- [x] Gradle 기반 멀티모듈 프로젝트 구성 (cassdio-core, cassdio-web)
- [x] Spring Boot 3.4+ 설정
- [x] Kotlin 설정
- [x] 기본 package 구조 생성
- [x] application.yml 설정

---

### Feature 3. React Vite 프로젝트 생성
**설명**: React, TypeScript, Vite 기반 프론트엔드 프로젝트를 생성한다.

**체크리스트**:
- [x] Vite + React + TypeScript 프로젝트 생성
- [x] 기본 폴더 구조 (components, pages, utils, hooks 등)
- [x] package.json 의존성 관리
- [x] environment 설정 구조

---

### Feature 4. 공통 개발 환경 구성
**설명**: Formatter, linter, test, build script, Git ignore, editor config 등을 구성한다.

**체크리스트**:
- [x] Prettier / ESLint (Frontend)
- [x] Kotlin Linter / Formatter (Backend)
- [x] .editorconfig 설정
- [x] Pre-commit hooks 구성
- [x] GitHub Actions workflows 기본 구성

---

### Feature 5. Docker Compose 로컬 환경 구성
**설명**: 로컬 개발용 Cassandra, 필요 시 PostgreSQL 또는 보조 서비스를 docker-compose로 실행할 수 있게 한다.

**체크리스트**:
- [x] docker-compose.yml 파일 작성
- [x] Cassandra 버전 지원 (3.11, 4.0, 4.1, 5.0)
- [x] 개발용 초기 설정 스크립트
- [x] 로컬 환경 문서화

---

### Feature 6. Health Check API 구현
**설명**: /api/health, /api/version 등 기본 상태 확인 API를 구현한다.

**체크리스트**:
- [x] GET /api/health - 서버 상태 확인
- [x] GET /api/version - 버전 정보 반환
- [x] Spring Boot Actuator 활성화
- [x] 기본 구현 및 테스트

---

### Feature 7. GitHub Actions 기본 CI 구성
**설명**: backend build/test, frontend build/test가 PR마다 실행되도록 CI를 구성한다.

**체크리스트**:
- [x] Backend build/test workflow 작성
- [x] Frontend build/test workflow 작성
- [x] PR 트리거 설정
- [x] 빌드 실패 시 체크 활성화
- [x] Artifacts 저장 설정

---

## 📋 체크리스트

- [x] Feature 1 완료
- [x] Feature 2 완료
- [x] Feature 3 완료
- [x] Feature 4 완료
- [x] Feature 5 완료
- [x] Feature 6 완료
- [x] Feature 7 완료
- [x] 전체 테스트 및 문서화

## 🔗 관련 문서

- [Milestone 2: 공통 Backend 구조 구성](./milestone-2.md)

---

**예상 소요 시간**: 1-2주
