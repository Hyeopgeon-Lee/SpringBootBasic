# SpringBootBasic

Spring Boot **4.1.1** / Java **17** 교육용 프로젝트입니다.

JSP + Spring MVC, MyBatis + MariaDB, Ehcache JCache, OpenFeign/Papago,
OpenWeatherMap, WebSocket 채팅, Tess4J OCR, SMTP 메일 발송을 실습합니다.

## 기술 스택

| 구성 요소 | 버전 / 방식 |
| --- | --- |
| Spring Boot | 4.1.1 |
| Java | 17 |
| Spring Cloud | 2025.1.3 (OpenFeign) |
| MyBatis Spring Boot Starter | 4.1.0 |
| Spring MVC | `spring-boot-starter-webmvc` |
| 화면 | JSP/JSTL (WAR 패키징) |
| DB | MariaDB JDBC, MyBatis |
| Cache | Ehcache 3 + JCache, `@Cacheable` |
| JSON | Jackson 3 (`tools.jackson.databind.ObjectMapper`) |
| OCR | Tess4J |

## 시작하기

Java 17, Maven Wrapper, MariaDB를 준비합니다. `sql/` 및 `src/main/resources/mapper/` 내에서 해당 실습에 필요한 SQL 스키마를 확인하고 생성해야 합니다.

```bash
# Linux / macOS
export DB_URL='jdbc:mariadb://localhost:3306/myDB'
export DB_USERNAME='poly'
export DB_PASSWORD='YOUR_DB_PASSWORD'
export MAIL_USERNAME='YOUR_MAIL_ACCOUNT'
export MAIL_PASSWORD='YOUR_MAIL_PASSWORD'
export WEATHER_API_KEY='YOUR_WEATHER_API_KEY'
export NAVER_CLIENT_ID='YOUR_CLIENT_ID'
export NAVER_CLIENT_SECRET='YOUR_CLIENT_SECRET'
export OCR_MODEL_DATA='/absolute/path/to/tessdata'
export OCR_UPLOAD_DIR='/tmp/springbootbasic-ocr'

./mvnw -B -ntp clean verify
./mvnw spring-boot:run
```

Windows PowerShell에서는 `$env:DB_PASSWORD = "..." ` 형식으로 환경변수를 설정할 수 있습니다.

설정 파일 `src/main/resources/application.properties`에는 더 이상 실사용 비밀번호나 API 키를 포함하지 않습니다.

| 환경변수 | 용도 |
| --- | --- |
| `SERVER_PORT` | HTTP 포트 (기본 11000) |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | MariaDB JDBC 연결 |
| `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD` | SMTP |
| `WEATHER_API_KEY` | OpenWeatherMap |
| `NAVER_CLIENT_ID`, `NAVER_CLIENT_SECRET` | Papago |
| `OCR_MODEL_DATA` | Tess4J 학습 데이터 폴더 |
| `OCR_UPLOAD_DIR` | OCR 임시 업로드 폴더 |
| `APP_AES_KEY` | 기존 이메일 암호문 호환용 AES-128 키 (UTF-8 정확히 16바이트) |

### 기존 데이터의 AES 키 관련 주의

기존 코드에는 고정된 AES-CBC 키와 IV가 포함되어 있었습니다.
수정본은 AES 키를 `APP_AES_KEY` 환경변수에서 읽으며, 값을 설정하지 않은 경우
이메일 암·복호화가 필요한 기능이 명확한 오류를 반환합니다.
**기존에 저장된 이메일은 기존 암호화 키가 없으면 복호화할 수 없습니다.**
운영 데이터를 유지해야 한다면 관리자가 비밀키를 안전하게 확보하고, 데이터를 인증된 암호화 방식으로 별도 마이그레이션해야 합니다.
현재 예제의 AES-CBC 고정 IV 및 비밀번호의 SHA-256 해시는 **운영용 보안 구현이 아닙니다**.

## 주요 학습 기능

- 공지사항: `/notice/noticeList` 목록/등록/수정/삭제, MyBatis 및 캐시
- 회원: `/user/userRegForm`, `/user/login`, 이메일 인증 예제
- 이메일 발송: `/mail/mailForm`, `/mail/sendMail` (로그인 세션 필요)
- 날씨: `GET /weather/getWeather?lat=37.5665&lon=126.9780`
- Papago: `POST /papago/detectLangs`, `POST /papago/translate`
- OCR: `GET /ocr/uploadImage`, `POST /ocr/readImage` (최대 5MB)
- WebSocket 채팅: `/ws/{room}/{user}/{language}`
- HTML 예제: `/html/translate.html`, `/html/detect_lang.html`, `/html/today_weather.html`

OCR은 `OCR_MODEL_DATA` 디렉터리에 `kor.traineddata`가 필요합니다.
임시 업로드 파일은 이미지 검증과 OCR 처리 후 삭제합니다.

### Spring Boot 4 전환 시 변경 내용

- Spring MVC 전용 스타터, Spring Cloud 2025.1.x, MyBatis 4.x 사용
- Java 코드에서 Jackson 2의 `com.fasterxml.jackson.databind.ObjectMapper`를 Jackson 3 API로 변경
- 날씨 좌표 유효성 검사, 유닉스 타임스탬프 `Long` 처리 보완
- 민감한 비밀번호·API 응답·메일 본문 로그 제거
- 메일 발송 엔드포인트 접근 시 로그인 검사
- 회원가입 이메일 6자리 인증번호를 JSON으로 반환하지 않고 서버 세션에서 확인 (유효기간 10분, 최대 5회 검사)
- WebSocket의 모든 Origin 허용 제거, 접속자 목록 스냅샷 순회
- OCR 임시 파일의 고유 파일명과 삭제 처리
- GitHub Actions에서 Java 17 / Maven `clean verify` 검증

## 테스트

```bash
./mvnw -B -ntp clean verify
```

`/.github/workflows/maven.yml`에서 Pull Request 및 push 시 컴파일, 단위 테스트, WAR 패키징을 검증합니다.
GitHub Actions에서 실제 DB, SMTP, OCR 모델, Papago 및 Weather API를 제공하지 않으므로,
**외부 서비스 호출·DB CRUD·JSP 렌더링은 별도의 통합 테스트가 필요합니다.**

## 반드시 알아둘 보안 제한

이 프로젝트는 보안·인증 프레임워크 구현을 완료한 운영 서비스가 아니라 **교육용 실습 저장소**입니다.

1. 기존 저장소의 Git 이력에도 과거 DB·SMTP 계정과 외부 API 인증정보가 남아 있을 수 있습니다.
   설정 파일에서 제거했더라도 노출된 실제 인증정보는 즉시 폐기·재발급해야 합니다.
2. 고정 IV 기반 기존 AES-CBC 이메일 암호화와 단순 SHA-256 비밀번호 저장은
   운영 환경에 적합하지 않습니다. BCrypt/Argon2 등으로 **기존 데이터 마이그레이션**을 계획해야 합니다.
3. 비밀번호 재설정 흐름은 서버 측 사용자 존재 확인을 강화했으나,
   **별도의 이메일 토큰 인증, CSRF 방어, 요청 제한을 추가하기 전에는 외부 배포해서는 안 됩니다.**
4. WebSocket 채팅 및 게시판의 세밀한 사용자별 권한 검사는 별도 실습으로 구현해야 합니다.

라이선스: [Apache License 2.0](LICENSE).
