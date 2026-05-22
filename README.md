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
      │    │   └── dto/                    
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

_최종 수정일: 2026-05-22_