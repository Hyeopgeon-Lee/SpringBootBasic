# 🌱 SpringBootBasic

**Spring Boot 3.4.5 · Java 17** 기반의 교육/실습용 프로젝트입니다. MVC, MyBatis, Ehcache(JCache), WebSocket 채팅, Naver Papago 번역(Feign), OpenWeatherMap 연동, Tess4J(OCR), 메일 발송 등을 한 프로젝트에서 단계적으로 실습할 수 있도록 구성했습니다.

<p align="left">
  <img alt="java" src="https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white">
  <img alt="spring-boot" src="https://img.shields.io/badge/Spring%20Boot-3.4.5-6DB33F?logo=springboot&logoColor=white">
  <img alt="build" src="https://img.shields.io/badge/Build-Maven%203.9+-C71A36?logo=apachemaven&logoColor=white">
  <img alt="license" src="https://img.shields.io/badge/License-Apache--2.0-blue">
</p>

---

## ✨ 주요 기능
- **공지게시판(Notice)**: 목록/등록/수정/삭제, 조회수
- **회원(User)**: 가입/로그인/아이디·비밀번호 찾기
- **메일 발송**: Naver SMTP 기반 단순 메일 전송
- **Papago 번역/언어감지**: Spring Cloud OpenFeign + `NaverApiConfig`
- **날씨(OpenWeatherMap)**: One Call 3.0 API, **Ehcache 캐시** 적용
- **OCR**: Tess4J(5.x) 기반 이미지 텍스트 추출
- **웹소켓 채팅**: `/ws/*/*/*` 핸들러, 다국어 번역 채팅 예제 포함
- **크롤링 예제**: 일일 식단, 영화 랭킹 수집 등 (Jsoup)

> ⚠️ 수업 예제로 제공되는 코드이므로 보안/예외처리/운영모드 최적화가 최소화되어 있습니다. 실제 서비스 시 **보안, 비밀키 외부화, SQL 호환성** 등을 반드시 검토하세요.

---

## 🧱 기술 스택
- **Spring Boot**: Web, Cache(JCache), WebSocket, Mail
- **Spring Cloud OpenFeign** (2024.0.2 BOM)
- **MyBatis**
- **DB**: MariaDB (예제), *일부 Mapper는 Oracle 함수 사용 주석 존재*
- **Cache**: Ehcache 3 (JCache 표준 API)
- **OCR**: Tess4J 5.6.0
- **크롤링**: Jsoup 1.18.x
- **기타**: Lombok, Commons-IO

---

## 📁 프로젝트 구조(요약)
```
SpringBootBasic/
├─ src/main/java/kopo/poly/
│  ├─ controller/      # REST & MVC 컨트롤러 (Notice, User, Mail, Weather, Papago, OCR, Chat 등)
│  ├─ service/         # 서비스 인터페이스
│  │  └─ impl/         # 서비스 구현체 (WeatherService 등)
│  ├─ mapper/          # MyBatis 매퍼 인터페이스
│  ├─ config/          # CacheConfig, NaverApiConfig, WebSoketConfig
│  ├─ dto/             # DTO 클래스들 (UserInfoDTO, WeatherDTO 등)
│  ├─ util/            # 유틸리티 (EncryptUtil, NetworkUtil, DateUtil 등)
│  └─ chat/            # WebSocket ChatHandler
├─ src/main/resources/
│  ├─ application.properties
│  ├─ logback.xml
│  ├─ mapper/*.xml     # MyBatis SQL 매퍼
│  └─ static/
│     ├─ html/         # 데모 페이지 (translate.html, detect_lang.html, today_weather.html)
│     └─ css|js
├─ pom.xml, mvnw, LICENSE, README.md
```

---

## ⚙️ 빠른 시작
### 1) 필수 요건
- **JDK 17**
- **Maven 3.9+** (동봉된 `mvnw` 사용 권장)
- **MariaDB 10.x+** (또는 Oracle 사용 시 Mapper SQL 호환성 조정 필요)

### 2) DB 준비 (MariaDB 예시)
```sql
CREATE DATABASE myDB DEFAULT CHARACTER SET utf8mb4;
CREATE USER 'poly'@'%' IDENTIFIED BY '강력한_비밀번호';
GRANT ALL PRIVILEGES ON myDB.* TO 'poly'@'%';
FLUSH PRIVILEGES;
```

### 3) 환경설정
> **중요**: 민감정보(KEY, 비밀번호)는 Git에 올리지 마세요. 로컬/서버 환경 변수나 `application-local.properties` 등으로 분리하세요.

`src/main/resources/application.properties` 예시(가이드):
```properties
# Server
server.port=11000

# MariaDB (URL 표기 주의: // 필요)
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.url=jdbc:mariadb://127.0.0.1:3306/myDB
spring.datasource.username=poly
spring.datasource.password=********

# Mail (Naver SMTP 예시)
spring.mail.host=smtp.naver.com
spring.mail.port=465
spring.mail.username=<YOUR_NAVER_EMAIL>
spring.mail.password=<YOUR_NAVER_APP_PASSWORD>
spring.mail.default-encoding=UTF-8
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.ssl.enable=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.naver.com

# OCR
ocr.model.data=/path/to/tessdata   # (프로젝트 내 프로퍼티 키는 orc.model.data 로 되어 있어 수정 권장)

# OpenWeatherMap
weather.api.key=<YOUR_OPENWEATHERMAP_API_KEY>

# Naver Papago
naver.papago.clientId=<YOUR_NAVER_CLIENT_ID>
naver.papago.clientSecret=<YOUR_NAVER_CLIENT_SECRET>

# 캐시 로그(필요 시)
logging.level.org.springframework.cache=TRACE
logging.level.org.springframework.cache.interceptor=TRACE
```

환경 분리를 원하면 프로필로 `application-local.properties` 를 만들고 실행 시 `--spring.profiles.active=local` 을 지정하세요.

### 4) 실행
```bash
# 1) 의존성 설치 및 실행
./mvnw spring-boot:run
# 또는 패키징 후 실행
./mvnw -DskipTests package
java -jar target/SpringBootBasic-0.0.1-SNAPSHOT.jar
```

서버가 기동되면 기본 포트 `http://localhost:11000` 으로 접근합니다.

---

## 🧪 빠른 점검(Quick Smoke Test)
### REST 엔드포인트
- **날씨**
  ```bash
  curl "http://localhost:11000/weather/getWeather?lat=37.5665&lon=126.9780"
  ```
- **Papago 언어감지**
  ```bash
  curl -X POST http://localhost:11000/papago/detectLangs        -d "text=안녕하세요. 반갑습니다"
  ```
- **Papago 번역** (한국어→영어)
  ```bash
  curl -X POST http://localhost:11000/papago/translate        -d "source=ko" -d "target=en" -d "text=안녕하세요"
  ```
- **메일 발송**
  ```bash
  curl -X POST http://localhost:11000/mail/sendMail        -d "toMail=receiver@example.com"        -d "title=테스트" -d "contents=본문입니다"
  ```
- **OCR 업로드 폼**: `GET /ocr/uploadImage` (업로드 필드명: `fileUpload`)

### 데모 페이지(정적)
- 번역: `http://localhost:11000/html/translate.html`
- 언어감지: `http://localhost:11000/html/detect_lang.html`
- 오늘의 날씨: `http://localhost:11000/html/today_weather.html`

---

## 🔌 WebSocket 채팅
- 핸들러 등록: `WebSoketConfig` → `addHandler(chatHandler, "/ws/*/*/*")`
- 세션 속성: `roomName`, `userName`, `roomNameHash`, `langCode`
- 샘플 화면 라우팅: `/chat/intro`, `/chat/chatroom`, `/global/intro` 등

> 프런트엔드는 수업 중 실습용 화면을 사용합니다. 실제 사용 시 클라이언트(JS)에서 `new WebSocket("ws://HOST:11000/ws/{room}/{user}/{lang}")` 형태로 연결하세요.

---

## 🧊 캐시(Ehcache JCache)
- `@EnableCaching` + `JCacheCacheManager`
- 캐시명: `weather` (기본 TTL 예: 10분)
- **KeyGenerator**: 위·경도 문자열을 **소수점 3자리 반올림**해 캐시 키 생성 → 근접 좌표 요청 캐시 히트율 향상
- 적용 메서드 예: `WeatherService#getWeather(... )` 에 `@Cacheable(cacheNames = "weather", key = ...)`

> 캐시 동작 로그가 필요하면 위 `logging.level` 설정을 TRACE 로 두고 확인하세요.

---

## 🗺️ API 엔드포인트 요약
> 주요 컨트롤러 기준 (세부 파라미터는 각 컨트롤러 참고)

| 모듈 | 메서드 | 경로 |
|---|---|---|
| Chat | GET  | `/chat/intro` |
| Chat | POST | `/chat/chatroom` |
| GlobalChat | GET  | `/global/intro` |
| GlobalChat | POST | `/global/chatroom` |
| Notice | GET  | `/notice/noticeList` |
| Notice | GET  | `/notice/noticeReg` |
| Notice | POST | `/notice/noticeInsert` |
| Notice | GET  | `/notice/noticeInfo` |
| Notice | GET  | `/notice/noticeEditInfo` |
| Notice | POST | `/notice/noticeUpdate` |
| Notice | POST | `/notice/noticeDelete` |
| User | GET  | `/user/userRegForm` |
| User | POST | `/user/getUserIdExists` |
| User | POST | `/user/getEmailExists` |
| User | POST | `/user/insertUserInfo` |
| User | GET  | `/user/login` |
| User | POST | `/user/loginProc` |
| User | GET  | `/user/loginResult` |
| User | GET  | `/user/searchUserId` |
| User | POST | `/user/searchUserIdProc` |
| User | GET  | `/user/searchPassword` |
| User | POST | `/user/searchPasswordProc` |
| User | POST | `/user/newPasswordProc` |
| Mail | GET  | `/mail/mailForm` |
| Mail | POST | `/mail/sendMail` |
| Weather | GET  | `/weather/getWeather` |
| OCR | GET  | `/ocr/uploadImage` |
| OCR | POST | `/ocr/readImage` |
| Movie | GET  | `/movie/collectMovieRank` |
| Movie | GET  | `/movie/getMovieRank` |
| Papago | POST | `/papago/detectLangs` |
| Papago | POST | `/papago/translate` |

---

## ⚠️ 주의/메모
- `application.properties` 의 **DB URL** 은 `jdbc:mariadb://...` (**`//` 포함**) 형식을 사용하세요.
- 일부 `mapper/*.xml` 은 예제로 **Oracle 함수(예: `TO_CHAR`)** 사용 부분이 있으므로, MariaDB 사용 시 SQL을 변환해야 합니다.
- 메일/외부 API 키는 **반드시 외부화**하고, 깃 저장소에 올리지 마세요.
- 운영 반영 전 **입력값 검증, 예외 처리, 인증/권한(Spring Security)** 등을 강화해야 합니다.

---

## 🧑‍💻 로컬 개발 팁
- 롬복이 동작하지 않으면 IDE(인텔리J) 플러그인을 설치하고 **Annotation Processing** 을 켜세요.
- 프로필 분리: `application-local.properties` + `--spring.profiles.active=local`
- 로그 설정: `logback.xml` 에서 레벨 조정

---

## 📜 라이선스
- 본 저장소는 **Apache-2.0** 라이선스를 따릅니다. 자세한 내용은 [LICENSE](./LICENSE) 를 참고하세요.

---

## 🙋‍♀️ 문의
- **한국폴리텍대학 서울강서캠퍼스 빅데이터소프트웨어과**  
- **이협건 교수** · hglee67@kopo.ac.kr  
- (홍보) 입학 상담 오픈채팅방: <https://open.kakao.com/o/gEd0JIad>

---

> README는 수업/시연 흐름에 맞게 **간결 + 실전 위주**로 구성했습니다. 필요 시 **Docker/K8s 배포 가이드**, **테스트 코드 예시**, **API 스펙 문서화(Swagger)** 등을 추가해 확장하세요.
