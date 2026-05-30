# RunSense_BackEnd
> 동양미래대 2026 졸업작품 런센스 백엔드

## 1. 기술 스택
 
* Language: Java 21
* Framework: Spring Boot 4.0.6
* Build Tool: Gradle
* Database: PostgreSQL

---

## 2. 종속성
* Spring Web: RESTful API 구축 및 내장 Apache Tomcat 제공

* Spring Security: 사용자 인증 및 권한 제어를 위한 보안 프레임워크

* JDBC API: 데이터베이스 연결 및 쿼리 실행을 위한 표준 API

* PostgreSQL Driver: PostgreSQL 데이터베이스와의 연결을 위한 드라이버

* Lombok: 보일러플레이트 코드(Getter/Setter, Constructor 등) 제거 및 생산성 향상

* springdoc-openapi-starter-webmvc-ui: Swagger UI와 OpenAPI 문서 자동 생성 및 화면 제공

---
## 2-1. Swagger 확인 방법

애플리케이션을 실행한 뒤 브라우저에서 `http://localhost:8080/v3/api-docs` 로 접속하면 OpenAPI JSON 문서를 확인할 수 있습니다.

Swagger UI 화면은 `http://localhost:8080/swagger-ui.html` 에서 확인할 수 있습니다.

---

## 2-2. JWT 로그인 방식

* 로그인 성공 시 쿠키 대신 JSON body로 `accessToken`, `refreshToken`, `tokenType`, `expiresIn`을 내려줍니다.
* `expiresIn`은 access token의 남은 유효시간을 초 단위로 의미합니다.
* `reissue`는 refresh token으로 새 토큰을 다시 받는 재발급 API입니다.
* 앱에서는 받은 토큰을 보안 저장소에 저장하고, 이후 요청마다 `Authorization: Bearer {accessToken}` 형식으로 전달합니다.


---
## 3. 폴더 구조
```text
src/
 └── main/
      ├── java/com/example/runsense/
      │    ├── domain/    
      │    │   ├── controller/          
      │    │   ├── service/          
      │    │   ├── repository/          
      │    │   ├── dto/          
      │    │   ├── entity/          
      │    ├─ global/                   # 전역에서 사용하는 기능 구현(설정, 예외 처리, 공통 응답 등)         
      │    │   └── config/                    
      └── resources/                    
           └── application.yml         
           
```

---

## 4. 네이밍 규칙

| 대상              | 규칙                  | 예시 |
|-----------------|---------------------|------|
| Class/Interface | PascalCase          | `UserService`, `UserDto` |
| Method/Variable | camelCase           | `getUserInfo`, `isLoggedIn` |
| Constant        | UPPER_SNAKE_CASE    | `API_TIMEOUT_MS` |
| Boolean         | `is/has/can` 접두사 사용 | `isActive`, `hasError` |
| Event Handler   | `handdle` 접두사 사용    | `handleLoginRequest` |
| Package         | lowerCamelCase      | Java/Spring 관례 준수 |

---


## 5. Git 컨벤션

### 브랜치 네이밍
- `feat/login`
- `fix/header-bug`
- `refactor/auth`

### 커밋 메시지

| 타입 | 설명 | 예시                  |
|------|------|---------------------|
| feat | 기능 추가 | `feat: 로그인 기능 구현`   |
| fix | 버그 수정 | `fix: 사용자 인증 버그 수정` |
| refactor | 리팩토링 | `refactor: 인증 로직 분리` |
| style | 코드 포맷 | `style: 들여쓰기 정리`    |
| docs | 문서 수정 | `docs: README 업데이트` |
| chore | 기타 작업 | `chore: 패키지 업데이트`   |

### PR 규칙
- `develop` 브랜치로 PR 생성
- 최소 1명 이상의 리뷰 승인 후 머지
- squash merge 사용

---

## 6. 기타 규칙

- **함수 길이**: 한 함수 50줄 이내 권장
- **매직 넘버 금지**: 해당 메소드 혹은 클래스 내에서 상수로 분리

---

_최종 수정일: 2026-05-24_