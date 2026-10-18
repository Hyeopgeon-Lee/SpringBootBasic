-- 수업용 선택 데이터: NoticeController의 가상 작성자 USER01.
-- 실제 로그인용 계정이 아님: PASSWORD 값은 해시가 아니므로 로그인되지 않습니다.
-- 민감한 개인정보를 사용하지 않는 샘플 데이터입니다.
-- 실행: mariadb -u poly -p myDB < docs/sql/mariadb_seed_demo.sql

INSERT IGNORE INTO USER_INFO (
    USER_ID, USER_NAME, PASSWORD, EMAIL, REG_ID, CHG_ID
)
VALUES (
    'USER01', '수업 예제 사용자', 'LOGIN_DISABLED',
    'user01-demo@example.invalid', 'SYSTEM', 'SYSTEM'
);
