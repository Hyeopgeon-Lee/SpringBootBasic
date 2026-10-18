# SpringBootBasic — Spring Boot 4 웹 프로그래밍 실습

> **Java 17 + Spring Boot 4.1.1 기반 학생 실습 프로젝트**  
> JSP 화면부터 Spring MVC, MyBatis, 회원가입·로그인, 캐시, OpenAPI, OCR, WebSocket, 웹 크롤링까지 직접 실행하고 흐름을 이해하는 것을 목표로 합니다.

단순히 코드를 복사하는 것보다 **① 요청이 어디로 들어오는지 → ② 어떤 Java 메서드가 처리하는지 → ③ 데이터가 어떻게 이동하는지 → ④ 어떤 결과가 나오는지 → ⑤ 오류는 어떻게 확인하는지**를 설명할 수 있어야 합니다.

⚠️ 이 저장소는 **수업 예제**입니다. 일부 외부 서비스와 SQL은 추가 설정·개선이 필요하며, 보안 기능을 완성한 운영용 웹사이트가 아닙니다. 특히 비밀번호 복구 기능 등을 인터넷에 공개하지 마세요.

## 목차

1. [학습 목표와 권장 실습 순서](#1-학습-목표와-권장-실습-순서)
2. [사용 기술과 프로젝트 구조](#2-사용-기술과-프로젝트-구조)
3. [개발 환경 구축](#3-개발-환경-구축)
4. [예제 1 — Spring MVC와 JSP](#4-예제-1--spring-mvc와-jsp)
5. [예제 2 — MyBatis 공지사항 CRUD](#5-예제-2--mybatis-공지사항-crud)
6. [예제 3 — 회원가입과 로그인·세션](#6-예제-3--회원가입과-로그인세션)
7. [예제 4 — Ehcache 캐시](#7-예제-4--ehcache-캐시)
8. [예제 5 — 날씨 OpenAPI](#8-예제-5--날씨-openapi)
9. [예제 6 — Papago와 OpenFeign](#9-예제-6--papago와-openfeign)
10. [예제 7 — 이미지 OCR](#10-예제-7--이미지-ocr)
11. [예제 8 — WebSocket 채팅](#11-예제-8--websocket-채팅)
12. [예제 9 — SMTP 메일](#12-예제-9--smtp-메일)
13. [예제 10 — Jsoup 웹 크롤링](#13-예제-10--jsoup-웹-크롤링)
14. [Spring Boot 4 핵심 변경 사항](#14-spring-boot-4-핵심-변경-사항)
15. [테스트, 실습 과제, 문제 해결](#15-테스트-실습-과제-문제-해결)
16. [보안 및 실행 한계](#16-보안-및-실행-한계)

## 1. 학습 목표와 권장 실습 순서

수업을 마친 뒤 학생은 다음을 설명하고 구현할 수 있어야 합니다.

- HTTP `GET/POST`, URL, 요청 파라미터, 상태 코드, JSON, JSP의 역할을 구분한다.
- `Controller → Service → Mapper → DB` 계층을 따라가며 데이터를 추적한다.
- MyBatis 매퍼 인터페이스와 XML의 `namespace`·`id` 관계를 이해한다.
- `HttpSession`과 쿠키가 로그인 상태 유지에 어떻게 사용되는지 설명한다.
- `@Cacheable`과 `@CacheEvict`를 통해 DB 호출과 캐시 무효화를 비교한다.
- 외부 API 인증키를 환경변수로 관리하고, 예외·응답을 확인한다.
- WebSocket과 일반 HTTP의 통신 방식 차이를 설명한다.
- 단위 테스트와 외부 서비스 통합 테스트의 차이를 설명한다.

| 권장 순서 | 학습 주제 | 실습 결과물 |
| --- | --- | --- |
| 1 | Spring MVC, JSP, DTO | 요청에서 JSP 화면으로 이동하기 |
| 2 | MyBatis, MariaDB | 공지사항 CRUD |
| 3 | 세션·회원가입·로그인 | 로그인 및 중복 검사 흐름 추적 |
| 4 | Ehcache | 캐시 히트·미스 비교 |
| 5 | REST·외부 API | 위도·경도로 날씨 조회 |
| 6 | OpenFeign | 언어 감지 및 번역 |
| 7 | Multipart·OCR | 이미지 속 한글 추출 |
| 8 | WebSocket | 같은 채팅방에 메시지 전송 |
| 9 | SMTP | 로그인 후 메일 발송 |
| 10 | Jsoup | HTML 선택자 기반 데이터 수집 |

**권장 사전 학습:** Java 클래스·인터페이스, 컬렉션, 예외 처리, SQL `SELECT/INSERT/UPDATE/DELETE`, HTML·JavaScript 기초.

## 2. 사용 기술과 프로젝트 구조

| 구분 | 사용 기술 | 역할 |
| --- | --- | --- |
| 언어 | Java 17 | 서버 프로그램 |
| 프레임워크 | Spring Boot 4.1.1, Spring MVC | 웹 요청 처리 |
| 화면 | JSP/JSTL | 서버에서 HTML 렌더링 |
| 데이터베이스 | MariaDB, MyBatis 4.x | SQL 실행·DTO 매핑 |
| 캐시 | Spring Cache, Ehcache 3 / JCache | 반복 요청 결과 재사용 |
| API 호출 | OpenFeign, HTTP | 외부 서비스 호출 |
| JSON | Jackson 3 | Java 객체 ↔ JSON |
| 실시간 통신 | Spring WebSocket | 양방향 채팅 |
| OCR | Tess4J | 이미지 문자 인식 |
| 데이터 수집 | Jsoup | HTML 분석 |
| 메일 | Spring Mail | SMTP 전송 |
| 빌드 | Maven Wrapper, WAR | 빌드·배포 |

### 요청 처리 구조

```text
브라우저 (HTML/JSP + JavaScript)
   │  HTTP 요청: GET /notice/noticeList
   ▼
Controller  ── URL·파라미터 처리, Model/JSON 응답
   ▼
Service     ── 업무 처리, 트랜잭션, 캐시
   ▼
Mapper      ── MyBatis 인터페이스
   ▼
Mapper.xml  ── SELECT / INSERT / UPDATE / DELETE
   ▼
MariaDB
   │
   └── 조회 결과 → DTO → Service → Controller → JSP 또는 JSON
```

폴더별 역할:

```text
src/main/java/kopo/poly/
  controller/            URL 요청 처리
  service/               서비스 인터페이스
  service/impl/          업무 로직 구현
  mapper/                MyBatis Mapper 인터페이스
  dto/                   계층 간 데이터를 전달하는 객체
  config/                Cache, Feign, WebSocket 설정
  chat/                  WebSocket 메시지 처리
  util/                  공통 유틸리티
src/main/resources/
  application.properties 실행 환경 설정
  mapper/*.xml           SQL 매퍼
  static/                CSS, JavaScript, HTML
src/main/webapp/
  WEB-INF/views/         JSP 페이지 (직접 URL로 열지 않음)
src/test/java/           자동 테스트
.github/workflows/       GitHub Actions
```

**중요 개념 — DTO:** `NoticeDTO`는 게시글의 제목, 내용, 작성자 등을 여러 계층 사이에 전달합니다. DTO가 데이터베이스 테이블 그 자체인 것은 아닙니다.

## 3. 개발 환경 구축

### 3-1. 준비물

- JDK **17**, IntelliJ IDEA 또는 Eclipse/STS
- Git, Maven Wrapper (저장소의 `mvnw`/`mvnw.cmd` 사용)
- MariaDB 서버 및 해당 실습의 테이블
- Postman 또는 터미널 `curl`
- 선택 기능: SMTP 계정, 날씨/Papago API 키, Tesseract 한국어 학습 데이터

### 3-2. 내려받기와 컴파일

```bash
git clone https://github.com/Hyeopgeon-Lee/SpringBootBasic.git
cd SpringBootBasic

# Linux / macOS
./mvnw -B -ntp clean verify

# Windows PowerShell
.\mvnw.cmd -B -ntp clean verify
```

`verify`는 컴파일·테스트·WAR 패키징을 수행합니다. **빌드 성공이 실제 DB·외부 API 동작까지 보장하지는 않습니다.**

### 3-3. MariaDB 실습 DB 생성 및 확인

`application.properties`의 기본 연결 주소는 `jdbc:mariadb://127.0.0.1:3306/myDB`입니다.
**이번 버전에는 4개 MyBatis 매퍼와 일치하는 MariaDB용 DDL이 포함되어 있습니다.**

- [신규 실습 DB 생성: docs/sql/mariadb_schema.sql](docs/sql/mariadb_schema.sql)
- [선택: 게시판 실습 사용자 생성: docs/sql/mariadb_seed_demo.sql](docs/sql/mariadb_seed_demo.sql)
- [공지사항 SQL](src/main/resources/mapper/NoticeMapper.xml) / [회원 SQL](src/main/resources/mapper/UserInfoMapper.xml) / [영화 SQL](src/main/resources/mapper/MovieMapper.xml) / [OCR SQL](src/main/resources/mapper/OcrMapper.xml)

**처음 설치하는 학생**은 MariaDB 관리자 계정에서 다음을 실행합니다.

```bash
# Linux/macOS/Windows (mariadb 클라이언트를 설치한 환경)
mariadb -u root -p < docs/sql/mariadb_schema.sql

# DB 계정 poly에 myDB 권한을 별도로 부여받은 경우:
mariadb -u poly -p myDB < docs/sql/mariadb_seed_demo.sql
```

Windows PowerShell은 리디렉션 대신 다음과 같이 MariaDB 접속 후 파일을 가져올 수 있습니다.

```sql
SOURCE C:/study/SpringBootBasic/docs/sql/mariadb_schema.sql;
SOURCE C:/study/SpringBootBasic/docs/sql/mariadb_seed_demo.sql;
```

`SOURCE`의 경로는 자신의 프로젝트 위치로 수정합니다. 생성되는 테이블은 `USER_INFO`(회원), `NOTICE`(게시글), `MOVIE_INFO`(영화 수집), `OCR_INFO`(OCR 결과) 총 4개입니다. `USER01` 샘플 계정은 **공지사항 예제의 가상 작성자를 위한 데이터**이며 실제 로그인은 비활성화되어 있습니다.

**기존 DB를 사용하는 경우:** `CREATE TABLE IF NOT EXISTS`는 기존 테이블 구조를 수정하지 않습니다. 먼저 아래를 확인한 후 **백업 및 별도 실습 DB에서 스키마 마이그레이션**을 진행해야 합니다.

```sql
SHOW CREATE TABLE NOTICE;
SHOW CREATE TABLE MOVIE_INFO;
SHOW CREATE TABLE OCR_INFO;
```

수정된 INSERT는 `NOTICE.NOTICE_SEQ`, `MOVIE_INFO.SEQ`, `OCR_INFO.SEQ`가 각각 `AUTO_INCREMENT`인 것을 전제로 합니다. 기존 PK와 인덱스가 이 구조와 다르면 무작정 `ALTER TABLE`을 실행하지 마세요. 특히 기존 영화 테이블이 날짜와 번호를 복합 기본키로 쓰는 경우에는 기본키 및 데이터 관계를 먼저 분석해야 합니다.

### Oracle → MariaDB 문법 비교

| 기존 SQL | MariaDB 수정 SQL | 수정 이유 |
| --- | --- | --- |
| `TO_CHAR(REG_DT, 'YYYY-MM-DD')` | `DATE_FORMAT(REG_DT, '%Y-%m-%d')` | 날짜 표시 형식 |
| `SYSDATE` | `NOW()` | 현재 날짜·시간 함수 |
| `COLLECT_TIME LIKE #{collectTime} \|\| '%'` | `COLLECT_TIME = #{collectTime}` | 수집일이 `yyyyMMdd` 고정 길이이므로 정확히 비교 |
| `MAX(SEQ) + 1` | `AUTO_INCREMENT` 컬럼을 INSERT에서 제외 | 첫 등록의 NULL 문제·중복 번호 위험 개선 |
| 암시적 테이블 조인 | `INNER JOIN ... ON ...` | 조인 조건과 필터를 구분하기 쉬움 |

문자열을 부분 검색해야 한다면 MariaDB에서 `LIKE CONCAT(#{keyword}, '%')`처럼 쓰면 됩니다. 이 프로젝트의 영화 조회는 완전한 날짜(`20261018`)를 전달하므로 `=`가 더 적합합니다.

> 실습 DB 계정·암호는 `DB_USERNAME`, `DB_PASSWORD` 환경변수로 설정하세요. **DDL과 SQL 매퍼의 문법 호환성을 검증하는 것**과 외부 웹사이트·SMTP·OCR을 실제로 실행하는 것은 별개입니다.

### 3-4. 비밀정보는 환경변수로 설정

| 환경변수 | 용도 | 실습에 필요한 경우 |
| --- | --- | --- |
| `SERVER_PORT` | 웹 포트, 기본 11000 | 선택 |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | MariaDB 연결 | DB 실습 |
| `MAIL_USERNAME`, `MAIL_PASSWORD` | SMTP 인증 | 회원 이메일·메일 |
| `WEATHER_API_KEY` | OpenWeatherMap One Call | 날씨 |
| `NAVER_CLIENT_ID`, `NAVER_CLIENT_SECRET` | Papago API | 번역·채팅 번역 |
| `OCR_MODEL_DATA` | `kor.traineddata`가 있는 tessdata 디렉터리 | OCR |
| `OCR_UPLOAD_DIR` | 임시 업로드 폴더 | OCR |
| `APP_AES_KEY` | 기존 이메일 데이터 AES 복호화용, UTF-8 정확히 16바이트 | 회원 |

Windows PowerShell 예시:

```powershell
$env:DB_USERNAME = "poly"
$env:DB_PASSWORD = "각자_발급받은_DB_비밀번호"
$env:APP_AES_KEY = "본인_관리_16바이트_키"
.\mvnw.cmd spring-boot:run
```

Linux / macOS 예시:

```bash
export DB_USERNAME='poly'
export DB_PASSWORD='각자_발급받은_DB_비밀번호'
# 이메일 관련 실습을 하는 경우 APP_AES_KEY 등을 추가
./mvnw spring-boot:run
```

`APP_AES_KEY`의 표기 예시는 설명용이며, 실제 값은 **UTF-8 기준 16바이트**여야 합니다. 기존 DB 데이터를 복호화하려면 **해당 데이터를 암호화할 때 사용한 원래 키**가 필요합니다. 새 키를 임의로 지정하면 기존 데이터는 복호화되지 않습니다.

기본 주소: **http://localhost:11000**

Spring Boot 4에서 이 프로젝트는 JSP 사용을 위해 **WAR**로 빌드됩니다. 실제 생성 파일은 `target/SpringBootBasic-0.0.1-SNAPSHOT.war`입니다.

```bash
# 빌드 후 WAR 직접 실행 예시
java -jar target/SpringBootBasic-0.0.1-SNAPSHOT.war
```

## 4. 예제 1 — Spring MVC와 JSP

**학습 목표:** URL 매핑, Controller, Model, View Resolver의 역할을 이해합니다.

**관련 코드**

- [NoticeController.java](src/main/java/kopo/poly/controller/NoticeController.java)
- [noticeList.jsp](src/main/webapp/WEB-INF/views/notice/noticeList.jsp)
- [application.properties](src/main/resources/application.properties)

**실행:** 브라우저에서 `http://localhost:11000/notice/noticeList` 접속 (DB 준비 필요).

현재 코드에서 핵심 부분을 단순화해 설명하면 다음과 같습니다.

```java
@GetMapping("noticeList")
public String noticeList(HttpSession session, ModelMap model) throws Exception {
    // 실제 프로젝트의 서비스 호출 흐름을 발췌한 학습용 코드
    List<NoticeDTO> notices = noticeService.getNoticeList();
    model.addAttribute("rList", notices);
    return "notice/noticeList";
}
```

- `@GetMapping`: GET 요청을 메서드에 연결합니다. 클래스의 `@RequestMapping("/notice")`와 합쳐 최종 경로가 결정됩니다.
- `noticeService.getNoticeList()`: DB 조회를 서비스 계층에 위임합니다.
- `model.addAttribute("rList", notices)`: JSP가 사용할 데이터를 전달합니다.
- `return "notice/noticeList"`: 설정된 prefix/suffix에 따라 `/WEB-INF/views/notice/noticeList.jsp`를 렌더링합니다.
- `@Controller`는 주로 화면 이름을, `@RestController`는 주로 객체(JSON 등)를 응답하는 데 사용합니다.

**확인 과제:** `rList`라는 Model 이름을 JSP에서 어디서 사용하고 있는지 찾고, 제목·조회수 컬럼과 DTO 필드를 연결해 설명하세요.

## 5. 예제 2 — MyBatis 공지사항 CRUD

**학습 목표:** SQL, DTO, Mapper, Service가 어떻게 연결되는지 이해합니다.

**관련 코드**

- [NoticeController.java](src/main/java/kopo/poly/controller/NoticeController.java)
- [INoticeService.java](src/main/java/kopo/poly/service/INoticeService.java)
- [NoticeService.java](src/main/java/kopo/poly/service/impl/NoticeService.java)
- [INoticeMapper.java](src/main/java/kopo/poly/mapper/INoticeMapper.java)
- [NoticeMapper.xml](src/main/resources/mapper/NoticeMapper.xml)
- [NoticeDTO.java](src/main/java/kopo/poly/dto/NoticeDTO.java)

### 5-1. CRUD URL과 데이터

| 작업 | HTTP | URL | 입력 예 |
| --- | --- | --- | --- |
| 목록 조회 | GET | `/notice/noticeList` | 없음 |
| 등록 화면 | GET | `/notice/noticeReg` | 없음 |
| 등록 | POST | `/notice/noticeInsert` | `title`, `noticeYn`, `contents` |
| 상세 조회 | GET | `/notice/noticeInfo?nSeq=1` | 게시글 번호 |
| 수정 화면 | GET | `/notice/noticeEditInfo?nSeq=1` | 게시글 번호 |
| 수정 | POST | `/notice/noticeUpdate` | `nSeq`, `title`, `noticeYn`, `contents` |
| 삭제 | POST | `/notice/noticeDelete` | `nSeq` |

브라우저에서 등록 페이지를 이용하거나, 아래처럼 폼 데이터를 요청할 수 있습니다.

```bash
curl -i -X POST "http://localhost:11000/notice/noticeInsert" \
  --data-urlencode "title=Spring MVC 실습" \
  --data-urlencode "noticeYn=N" \
  --data-urlencode "contents=MyBatis를 이용한 게시글 저장"
```

응답은 JSP가 아닌 `MsgDTO`의 JSON 형태입니다. 아래는 **형태 설명 예시**로, 실제 성공 여부는 DB와 서비스 처리 상태에 달려 있습니다.

```json
{"msg":"등록되었습니다."}
```

### 5-2. Mapper 인터페이스와 MariaDB XML 연결

`INoticeMapper.java`에 선언된 메서드는 `NoticeMapper.xml`의 같은 이름을 가진 SQL 문과 연결됩니다.

```java
// 설명용 발췌 (실제 코드는 INoticeMapper.java 참조)
void insertNoticeInfo(NoticeDTO pDTO) throws Exception;
```

아래 코드는 **현재 SQL 매퍼에서 핵심 부분을 발췌**한 예입니다.

```xml
<insert id="insertNoticeInfo" parameterType="NoticeDTO"
        useGeneratedKeys="true" keyProperty="noticeSeq" keyColumn="NOTICE_SEQ">
    INSERT INTO NOTICE (TITLE, NOTICE_YN, CONTENTS, USER_ID, READ_CNT,
                        REG_ID, REG_DT, CHG_ID, CHG_DT)
    VALUES (#{title}, #{noticeYn}, #{contents}, #{userId}, 0,
            #{userId}, NOW(), #{userId}, NOW())
</insert>
```

- `namespace="kopo.poly.mapper.INoticeMapper"`: Java Mapper 인터페이스와 연결합니다.
- `id="insertNoticeInfo"`: 호출할 메서드 이름과 일치합니다.
- `#{title}`: DTO 필드를 안전하게 SQL 파라미터로 바인딩합니다.
- `NOW()`: MariaDB의 현재 시간을 등록일·수정일로 저장합니다.
- `AUTO_INCREMENT`: 게시글 번호를 DB에서 자동 생성합니다.
- `useGeneratedKeys` / `keyProperty`: 발급된 게시글 번호를 DTO로 돌려받습니다.
- `DATE_FORMAT(N.REG_DT, '%Y-%m-%d')`: DB 날짜 데이터를 `yyyy-MM-dd` 문자열로 조회합니다.

**예전 번호 생성 방식의 문제**: `MAX(NOTICE_SEQ)+1`은 비어 있는 테이블에서 `NULL`이 될 수 있으며 동시에 글을 등록할 때 번호가 중복될 수 있습니다. 그래서 **번호 생성 책임은 DB에 맡기는 것이 권장됩니다.**

### 예제 SQL 직접 실행

```sql
-- NOTICE에 연결된 USER_INFO의 작성자가 있어야
-- 게시글 목록의 INNER JOIN 결과에 노출됩니다.
SELECT N.NOTICE_SEQ, N.TITLE, U.USER_NAME,
       DATE_FORMAT(N.REG_DT, '%Y-%m-%d') AS REG_DT
FROM NOTICE N
INNER JOIN USER_INFO U ON N.USER_ID = U.USER_ID
ORDER BY N.REG_DT DESC;

-- 번호 자동 생성 상태 확인
SHOW CREATE TABLE NOTICE;
```

**개인 실습 과제:** 첫 게시글 두 개를 연속 등록하고 `NOTICE_SEQ`가 서로 다른지 확인하세요. `NOW()`와 `DATE_FORMAT()`가 저장·조회 단계에서 각각 어떤 역할을 담당하는지도 정리하세요.

### 5-3. 조회수와 트랜잭션

`NoticeService.getNoticeInfo(dto, true)`는 조회수를 증가시키고 상세정보를 조회합니다. 반면 수정을 위한 화면에서는 `false`를 전달해 조회수를 올리지 않습니다. `@Transactional`은 동일 서비스 작업에 포함된 DB 처리를 트랜잭션 범위로 관리합니다.

**확인 과제**

1. 글 목록 → 상세보기로 이동할 때 어떤 메서드 순서로 호출되는지 적으세요.
2. `true`와 `false`가 조회수에 어떤 차이를 만드는지 비교하세요.
3. 등록·수정·삭제 이후 `@CacheEvict`가 필요한 이유를 설명하세요.
4. 변경된 `DATE_FORMAT`, `NOW()`, `AUTO_INCREMENT`가 기존 Oracle SQL과 어떻게 다른지 비교해 보세요.

**주의:** 현재 목록 접근 시 `SESSION_USER_ID=USER01`을 넣는 부분은 초기 교육용 가상 사용자 처리입니다. **실제 로그인·접근권한 검사로 간주하면 안 됩니다.**

## 6. 예제 3 — 회원가입과 로그인·세션

**학습 목표:** 폼 요청, 중복 조회, 인증번호, 세션의 개념을 이해합니다.

**관련 코드**

- [UserInfoController.java](src/main/java/kopo/poly/controller/UserInfoController.java)
- [UserInfoService.java](src/main/java/kopo/poly/service/impl/UserInfoService.java)
- [UserInfoMapper.xml](src/main/resources/mapper/UserInfoMapper.xml)
- [userRegForm.jsp](src/main/webapp/WEB-INF/views/user/userRegForm.jsp)

| 기능 | URL | 주요 입력값 |
| --- | --- | --- |
| 회원가입 화면 | `GET /user/userRegForm` | 없음 |
| 아이디 중복 확인 | `POST /user/getUserIdExists` | `userId` |
| 이메일 중복·인증번호 발송 | `POST /user/getEmailExists` | `email` |
| 회원가입 | `POST /user/insertUserInfo` | `userId`, `password`, `email`, `authNumber` 등 |
| 로그인 화면 | `GET /user/login` | 없음 |
| 로그인 처리 | `POST /user/loginProc` | `userId`, `password` |
| 비밀번호 재설정 화면 | `GET /user/searchPassword` | 없음 |

중복 확인 요청 예시:

```bash
curl -i -X POST "http://localhost:11000/user/getUserIdExists" \
  --data-urlencode "userId=student01"
```

응답 DTO의 `existsYn`은 일반적으로 `Y`(존재) 또는 `N`(미존재)입니다.

### 이메일 인증 흐름

```text
[이메일 입력] → [중복 조회 및 6자리 인증번호 메일 발송]
              → [서버 세션에 인증정보 저장: 유효기간 10분]
              → [학생이 메일의 번호 입력]
              → [서버가 이메일·번호·만료·시도횟수 확인]
              → [회원정보 DB 저장]
```

인증번호를 **서버가 검사해야 하는 이유**: JavaScript에 검증용 정답을 전달하면 브라우저 개발자 도구에서 확인하거나 검증을 우회할 수 있기 때문입니다. 현재 예제는 서버 세션에서 인증번호를 관리하며 검증 시도 횟수를 제한합니다.

### 세션 예제

로그인 성공 시 컨트롤러는 `SS_USER_ID`, `SS_USER_NAME` 등을 세션에 저장합니다.

```java
// 개념 설명 예시. 이 코드만으로 안전한 로그인 기능이 완성되지는 않습니다.
session.setAttribute("SS_USER_ID", userId);
String currentUser = (String) session.getAttribute("SS_USER_ID");
```

서버의 세션에는 사용자 정보가 저장되고, 브라우저는 일반적으로 세션을 식별하는 쿠키를 보냅니다. **세션 값이 있는지 검사하는 것과 실제 권한을 검증하는 것은 다른 문제**입니다.

**확인 과제:** 쿠키를 지운 뒤 로그인 상태가 어떻게 바뀌는지 확인하고, `HttpSession`과 HTTP의 무상태(stateless) 특성을 연결해 설명해 보세요.

> **보안 한계:** 기존 예제는 비밀번호를 단순 SHA-256 방식으로 저장하고, 비밀번호 재설정도 별도 이메일 복구 토큰을 사용하지 않습니다. 운영 서비스에는 `BCrypt`/`Argon2`, 재설정 토큰, CSRF 방어 및 접근 제한이 필요합니다.

## 7. 예제 4 — Ehcache 캐시

**학습 목표:** 캐시 히트(hit), 미스(miss), TTL, 무효화(eviction)를 구분합니다.

관련 코드: [CacheConfig.java](src/main/java/kopo/poly/config/CacheConfig.java), [NoticeService.java](src/main/java/kopo/poly/service/impl/NoticeService.java), [WeatherService.java](src/main/java/kopo/poly/service/impl/WeatherService.java).

| 캐시 | 저장 데이터 | TTL | 캐시 키 |
| --- | --- | --- | --- |
| `notice:list` | 공지사항 목록 | 60초 | 동일한 목록 요청에 고정 키 |
| `weather` | 위경도별 날씨 DTO | 10분 | 소수점 3자리 반올림 위도·경도 |

현재 서비스의 어노테이션 예시:

```java
@Cacheable(cacheNames = "notice:list", keyGenerator = "noticeList", sync = true)
public List<NoticeDTO> getNoticeList() throws Exception {
    return noticeMapper.getNoticeList();
}
```

첫 요청은 DB에 접근하고 결과를 캐시에 보관합니다. 동일한 키로 재요청하면 만료 전에는 캐시 값을 활용할 수 있습니다. `@CacheEvict(cacheNames = "notice:list", allEntries = true)`는 목록 변경 후 기존 값을 제거합니다.

**실습:** 목록 페이지를 연속으로 두 번 요청하고 서비스 로그·DB 조회 여부를 비교하세요. 게시글 등록 후 새 목록이 보이는지 확인하세요. 캐시 시간이 있다고 해서 변경된 목록이 무조건 늦게 반영되는 것은 아닙니다. **쓰기 후 캐시 무효화**가 있기 때문입니다.

## 8. 예제 5 — 날씨 OpenAPI

**학습 목표:** 요청 파라미터 검증, HTTP GET, JSON 역직렬화, 외부 API 응답 처리를 이해합니다.

관련 코드: [WeatherController.java](src/main/java/kopo/poly/controller/WeatherController.java), [WeatherService.java](src/main/java/kopo/poly/service/impl/WeatherService.java), [NetworkUtil.java](src/main/java/kopo/poly/util/NetworkUtil.java).

API는 OpenWeatherMap **One Call 3.0**을 사용합니다. 해당 API 이용 권한·요금제는 각자 확인해야 합니다.

```bash
curl -i "http://localhost:11000/weather/getWeather?lat=37.5665&lon=126.9780"
```

- `lat`: 위도, -90 ~ 90
- `lon`: 경도, -180 ~ 180
- API 키: `WEATHER_API_KEY` 환경변수
- 결과 DTO: 위경도, 현재 기온, 일별 날씨 목록

응답 구조를 이해하기 위한 **가상 데이터 예시**:

```json
{
  "lat": "37.5665",
  "lon": "126.9780",
  "currentTemp": 22.5,
  "dailyList": [
    {"day": "2026-10-18", "dayTemp": "21.0", "dayTempMax": "25.0", "dayTempMin": "17.0"}
  ]
}
```

실제 기온·날짜·목록 개수는 호출 시점과 API 데이터에 따라 달라집니다.

**확인 과제:** `lat=999`로 요청해 왜 HTTP 400이 나오는지 확인하세요. 같은 위치를 반복 요청해 `weather` 캐시를 관찰하세요. `DateUtil`이 Unix 초 단위 시간을 어떻게 사람이 읽는 날짜로 변환하는지도 살펴보세요.

## 9. 예제 6 — Papago와 OpenFeign

**학습 목표:** 외부 REST API 인터페이스 선언, 요청 헤더, 폼 데이터, JSON 응답을 이해합니다.

관련 코드: [PapagoController.java](src/main/java/kopo/poly/controller/PapagoController.java), [PapagoService.java](src/main/java/kopo/poly/service/impl/PapagoService.java), [INaverAPIService.java](src/main/java/kopo/poly/service/INaverAPIService.java), [NaverApiConfig.java](src/main/java/kopo/poly/config/NaverApiConfig.java).

언어 감지:

```bash
curl -i -X POST "http://localhost:11000/papago/detectLangs" \
  --data-urlencode "text=안녕하세요"
```

자동 번역:

```bash
curl -i -X POST "http://localhost:11000/papago/translate" \
  --data-urlencode "text=안녕하세요"
```

**현재 구현은 요청의 `text`를 먼저 언어 감지하고, 한국어는 영어로·영어는 한국어로 번역합니다.** `source`, `target`을 컨트롤러 요청 파라미터로 직접 받는 API가 아닙니다. 다른 언어 입력은 예외 처리 대상입니다.

Feign 인터페이스에서 확인할 코드:

```java
@RequestLine("POST /v1/papago/detectLangs")
@Headers("Content-Type: application/x-www-form-urlencoded")
@Body("query={query}")
PapagoDTO detectLangs(@Param("query") String query);
```

- `@FeignClient`: 외부 서버를 호출할 Java 인터페이스를 정의합니다.
- `@RequestLine`: HTTP 메서드·API 경로를 정합니다 (이 예제는 Feign의 기본 Contract 사용).
- `@Headers`와 `@Body`: 폼 요청의 헤더·본문을 구성합니다.
- `RequestInterceptor`: 네이버 API 인증 헤더를 추가합니다.

**확인 과제:** 네이버 API에 직접 HTTP 연결 코드를 작성하는 방식과 Feign 인터페이스 방식을 비교하세요. 한국어·영어 각각으로 테스트해 보세요. 실제 Papago API 사용 가능 여부 및 인코딩 처리는 외부 서비스 응답으로 추가 검증해야 합니다.

## 10. 예제 7 — 이미지 OCR

**학습 목표:** `multipart/form-data` 파일 업로드, 파일 검증, OCR, 임시파일 정리 과정을 이해합니다.

관련 코드: [OcrController.java](src/main/java/kopo/poly/controller/OcrController.java), [OcrService.java](src/main/java/kopo/poly/service/impl/OcrService.java), [uploadImage.jsp](src/main/webapp/WEB-INF/views/ocr/uploadImage.jsp).

1. `OCR_MODEL_DATA`에 Tesseract의 `kor.traineddata`가 있는 디렉터리를 지정합니다.
2. `http://localhost:11000/ocr/uploadImage`로 접속합니다.
3. 한글이 들어 있는 JPG·PNG·GIF 파일을 선택해 전송합니다.
4. 서버가 파일 형식과 이미지 내용을 확인하고 OCR을 수행합니다.
5. 결과를 `ocr/readImage.jsp`에 표시하고 임시 파일을 삭제합니다.

터미널 요청 예시:

```bash
curl -i -X POST "http://localhost:11000/ocr/readImage" \
  -F "fileUpload=@./sample.png"
```

이 엔드포인트는 **JSON이 아닌 JSP/HTML 화면**을 반환합니다. 업로드는 5MB 제한이 있으며, 모델 파일이 없으면 OCR은 실패할 수 있습니다.

**확인 과제:** 같은 이미지의 해상도·문자 크기를 바꾸어 인식 결과를 비교하세요. 확장자를 `.png`로 바꾼 텍스트 파일이 왜 이미지 검증을 통과하지 못하는지도 설명해 보세요.

## 11. 예제 8 — WebSocket 채팅

**학습 목표:** HTTP 요청/응답과 달리 연결을 유지하는 양방향 통신을 이해합니다.

관련 코드: [WebSoketConfig.java](src/main/java/kopo/poly/config/WebSoketConfig.java), [ChatHandler.java](src/main/java/kopo/poly/chat/ChatHandler.java), [ChatController.java](src/main/java/kopo/poly/controller/ChatController.java).

- 일반 채팅 화면: `GET /chat/intro`
- 다국어 채팅 화면: `GET /global/intro`
- WebSocket 접속 패턴: `/ws/{room}/{user}/{language}`

브라우저 JavaScript **독립 학습 예시**:

```javascript
const ws = new WebSocket("ws://localhost:11000/ws/studyroom/student01/ko");
ws.onopen = () => ws.send(JSON.stringify({ name: "student01", msg: "안녕하세요" }));
ws.onmessage = event => console.log("받은 메시지:", event.data);
ws.onerror = () => console.error("채팅 연결 오류");
```

채팅방 이름은 서버에서 해시로 변환되어 방 식별에 사용됩니다. `ChatHandler`는 접속 사용자 목록을 관리하고, 같은 방에 있는 세션에 메시지를 전달합니다. 메시지 번역을 수행하므로 **Papago API 설정도 필요**합니다.

**확인 과제:** 브라우저 두 개로 같은 방에 접속해 메시지 수신을 관찰하세요. 방 이름을 다르게 했을 때 메시지 전송 범위가 어떻게 달라지는지 설명하세요. 실제 인터넷 서비스에서는 채팅방 권한·연결 인증도 필요합니다.

## 12. 예제 9 — SMTP 메일

**학습 목표:** 웹 폼에서 메일 DTO를 생성하고 SMTP 서버로 전송하는 흐름을 이해합니다.

관련 코드: [MailController.java](src/main/java/kopo/poly/controller/MailController.java), [MailService.java](src/main/java/kopo/poly/service/impl/MailService.java), [mailForm.jsp](src/main/webapp/WEB-INF/views/mail/mailForm.jsp).

메일 발송 경로는 `POST /mail/sendMail`이며, 폼 값은 `toMail`, `title`, `contents`입니다. 로그인 세션 `SS_USER_ID`가 없는 경우 **403 Forbidden**이 발생합니다.

요청 형태 예시 (먼저 브라우저에서 로그인을 완료한 경우에만 정상 전송 가능):

```text
POST /mail/sendMail
Content-Type: application/x-www-form-urlencoded

toMail=student@example.com&title=수업안내&contents=실습메일입니다
```

메일 발송 여부는 `MsgDTO`의 `result`와 `msg`로 확인합니다. SMTP 인증, 수신자, 서버 보안 정책에 따라 실패할 수 있습니다.

**확인 과제:** 로그인하지 않은 상태와 로그인한 상태를 비교하세요. 실제 수업에서는 본인이 관리하는 수신 주소로만 테스트하고, 외부 대량 발송은 금지합니다.

## 13. 예제 10 — Jsoup 웹 크롤링

**학습 목표:** HTML을 가져와 CSS 선택자로 필요한 데이터를 추출합니다.

관련 코드: [FoodService.java](src/main/java/kopo/poly/service/impl/FoodService.java), [MovieService.java](src/main/java/kopo/poly/service/impl/MovieService.java).

| 기능 | 실행 URL | 결과 |
| --- | --- | --- |
| 식단 수집·표시 | `GET /food/toDayFood` | JSP 식단 목록 |
| 영화 순위 수집 | `GET /movie/collectMovieRank` | DB 저장 후 수집 결과 화면 |
| 영화 순위 목록 | `GET /movie/getMovieRank` | DB에 저장된 영화 정보 JSP |

Jsoup의 핵심 원리:

```java
// 문법 학습용 예시: 실제 외부 사이트와 무관한 HTML
Document document = Jsoup.parse(
    "<ul class='items'><li>Java</li><li>Spring</li></ul>"
);
String first = document.select("ul.items li").first().text();
// 결과: Java
```

영화 데이터를 저장하는 [MovieMapper.xml](src/main/resources/mapper/MovieMapper.xml)은 수집일을 `=`로 조회하고, 순위 문자열을 `CAST(MOVIE_RANK AS UNSIGNED)`로 변환하여 1·2·10 순서대로 정렬합니다. 영화의 `SEQ`는 `AUTO_INCREMENT`로 발급됩니다. 현재 식단 예제는 `table.tbl_table tbody` 등 특정 HTML 구조를 가정하고, 영화 예제도 CGV의 과거 CSS 선택자에 의존합니다. **웹사이트가 개편되면 실제 수집 결과가 없거나 예외가 발생할 수 있습니다.** 대상 사이트의 이용 정책을 확인하고, 요청 빈도를 제한하세요.

**확인 과제:** 브라우저 개발자 도구에서 HTML 태그 구조를 살펴보고 `select("...")` 선택자가 어떤 요소를 가져오는지 설명하세요. 실습용 로컬 HTML부터 연습하면 외부 사이트 변경에 영향을 받지 않습니다.

## 14. Spring Boot 4 핵심 변경 사항

| 영역 | 이 프로젝트의 변경 |
| --- | --- |
| Spring Boot | `3.4.5 → 4.1.1` |
| Spring MVC | `spring-boot-starter-webmvc` 사용 |
| Spring Cloud | `2025.1.3` |
| MyBatis | Spring Boot 4 호환 스타터 4.x |
| JSON | 서비스/채팅에서 Jackson 3 `tools.jackson.databind.ObjectMapper` 사용 |
| 의존성 | Boot 4에 맞게 Maven 의존성 정리 |
| 화면 | JSP 유지, 패키징 `war` 유지 |
| 설정 | DB·메일·외부 API 값 환경변수로 분리 |
| 검사 | JUnit·Mockito 테스트, Maven GitHub Actions |

이 프로젝트는 **Spring MVC + JDBC/MyBatis(일반적으로 blocking)** 예제입니다. **Spring WebFlux + R2DBC(non-blocking)**를 배우려면 별도 [SpringWebFlux](https://github.com/Hyeopgeon-Lee/SpringWebFlux) 저장소와 비교하세요.

## 15. 테스트, 실습 과제, 문제 해결

### 15-1. 자동 테스트 실행

```bash
# Linux/macOS
./mvnw -B -ntp clean verify

# Windows
.\mvnw.cmd -B -ntp clean verify
```

GitHub Actions: [.github/workflows/maven.yml](.github/workflows/maven.yml)

기존 단위 테스트 10개에 **MariaDB 연동 테스트 4개**를 추가했습니다. GitHub Actions는 MariaDB 11.4 서비스를 실행하여 `USER_INFO`, `NOTICE`, `MOVIE_INFO`, `OCR_INFO`의 실제 MyBatis SQL을 확인하도록 구성되어 있습니다.

```text
USER_INFO : INSERT → 아이디/이메일 조회 → LOGIN → 비밀번호 UPDATE
NOTICE    : AUTO_INCREMENT INSERT → JOIN/DATE_FORMAT SELECT → 조회수 증가 → UPDATE → DELETE
MOVIE_INFO: AUTO_INCREMENT INSERT → 숫자 순위 정렬 SELECT → DELETE
OCR_INFO  : AUTO_INCREMENT INSERT → 저장 데이터 SELECT
```

로컬 컴퓨터에서 `INTEGRATION_DB_URL`이 없으면 DB 테스트 4개는 **건너뛰고**, 나머지 단위 테스트만 실행됩니다. 실제 DB 연동까지 검사하려면 **테스트 전용 빈 데이터베이스에서만** 환경변수를 설정해야 합니다. 통합 테스트는 스키마 생성 및 SQL 실행을 수행하므로 **운영 DB에 연결하지 마세요.** 테스트 통과와 별개로 SMTP·OCR 모델·외부 API·JSP 화면의 실제 동작은 추가 검증이 필요합니다.

### 15-2. 학생 실습 과제

| 주제 | 과제 | 확인할 결과 |
| --- | --- | --- |
| MVC | 목록 Controller에 전달하는 Model 이름 찾기 | JSP에서 출력되는 속성과 연결 |
| MyBatis | `NOTICE` 조회 SQL의 컬럼과 생성 번호 확인 | Mapper → DTO 매핑 및 `AUTO_INCREMENT` 설명 |
| 캐시 | 동일 목록 2회 요청 후 게시글 등록 | 캐시 재사용과 무효화 구분 |
| 회원 | 이메일 인증번호를 틀리게 입력 | 서버의 가입 거부 확인 |
| 날씨 | 정상 좌표와 `lat=999` 비교 | 정상 흐름과 HTTP 400 구별 |
| 번역 | 한국어·영어 문장 각각 번역 | 감지 언어에 따른 번역 방향 |
| OCR | 서로 다른 품질의 이미지 비교 | 인식 결과의 차이 분석 |
| WebSocket | 같은 방·다른 방의 메시지 비교 | 채팅방별 분리 확인 |
| 크롤링 | CSS 선택자 하나 변경 | 파싱 결과 변화를 설명 |
| 테스트 | 새로운 실패 입력 테스트 하나 작성 | `mvnw test`로 재현·검증 |

과제 제출 시 권장 항목: **실습 목표, 실행한 명령 또는 URL, 핵심 코드 5~15줄, 실행 화면, 확인한 결과, 오류 및 해결 과정**.

### 15-3. 오류 발생 시 확인 순서

| 증상 | 우선 확인 |
| --- | --- |
| DB 연결 실패 | MariaDB 실행, DB 이름, `DB_URL`, 계정·권한 |
| SQL 문법 오류 | 4개 매퍼의 MariaDB 문법, `SHOW CREATE TABLE`, `AUTO_INCREMENT` 설정 확인 |
| JSP 화면 오류 | 컨트롤러의 반환 View 이름, `WEB-INF/views` 경로, WAR/Jasper |
| 메일 403 | `SS_USER_ID` 로그인 세션 존재 여부 |
| SMTP 발송 실패 | SMTP 인증정보, 포트 465, 보안 연결 |
| 날씨 API 실패 | API 키와 One Call 3.0 사용 권한, lat/lon |
| OCR 오류 | `OCR_MODEL_DATA` 위치, `kor.traineddata`, 파일 크기 |
| Papago 오류 | 유효한 API 인증, 요청 데이터, 서비스 지원 상태 |
| 채팅 오류 | WebSocket URL·Origin, Papago 번역 설정 |
| 캐시 이상 | TTL, 캐시 키, `@CacheEvict` 실행 여부 |

**디버깅 순서:** ① 요청 URL/메서드 확인 → ② HTTP 상태 코드 확인 → ③ Controller 진입 여부 → ④ Service/Mapper 호출 → ⑤ DB 또는 외부 API 응답 확인. 로그에 비밀번호, 인증번호, 원문 메일, 외부 API 키를 기록해서는 안 됩니다.

## 16. 보안 및 실행 한계

- 과거 Git 이력에 노출되었을 가능성이 있는 **DB/SMTP 비밀번호와 API 키는 재발급·폐기**해야 합니다. 현재 파일에서 지워도 Git 이력은 자동으로 삭제되지 않습니다.
- 기존 이메일 암호화 방식(AES-CBC 고정 IV)과 비밀번호 저장 방식(SHA-256 단독)은 운영 환경에 적합하지 않습니다. 기존 데이터 호환성을 고려해 인증된 암호화 및 BCrypt/Argon2로 이전해야 합니다.
- 비밀번호 재설정의 사용자 확인만으로는 안전한 계정 복구를 보장할 수 없습니다. 검증된 별도 이메일 복구 토큰, 만료·1회용 정책, 요청 제한이 필요합니다.
- 게시판의 가상 사용자 `USER01`은 인증된 사용자와 다르며, WebSocket 채팅·크롤링·메일 기능에도 운영용 권한 관리가 필요합니다.
- MariaDB SQL은 GitHub Actions의 테스트용 DB에서 검증하지만 **기존 DB 스키마를 자동 변경하지 않습니다**. 새 실습 DB는 `docs/sql/mariadb_schema.sql`을 사용하세요.
- JSP와 외부 API 동작은 **빌드·DB SQL 검증과 별개로 실습 환경에서 확인**해야 합니다.
- 실습 데이터에는 실명·개인 계정·민감한 개인정보를 사용하지 마세요.

---

**수업 활용 제안:** 각 장의 코드 위치를 먼저 읽고, 요청을 실행한 다음, 호출 흐름을 직접 그려 보세요. 단순한 정답 실행보다 **왜 이 어노테이션·SQL·DTO가 필요한지 설명하는 것**을 평가 기준으로 삼으면 좋습니다.

라이선스: [Apache License 2.0](LICENSE).  
프로젝트: [Hyeopgeon-Lee/SpringBootBasic](https://github.com/Hyeopgeon-Lee/SpringBootBasic)
