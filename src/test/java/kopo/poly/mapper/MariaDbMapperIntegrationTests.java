package kopo.poly.mapper;

import kopo.poly.dto.MovieDTO;
import kopo.poly.dto.NoticeDTO;
import kopo.poly.dto.OcrDTO;
import kopo.poly.dto.UserInfoDTO;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * MyBatis XML의 SQL을 실제 MariaDB에서 검증한다.
 * INTEGRATION_DB_URL이 없으면 로컬 기본 빌드에서는 건너뛴다.
 * GitHub Actions에서는 MariaDB 서비스를 실행하여 테스트한다.
 */
class MariaDbMapperIntegrationTests {

    private static SqlSessionFactory sessionFactory;
    private static String url;
    private static String username;
    private static String password;

    @BeforeAll
    static void prepare() throws Exception {
        url = System.getenv("INTEGRATION_DB_URL");
        assumeTrue(url != null && !url.isBlank(),
                "Set INTEGRATION_DB_URL to run MariaDB integration tests");

        username = System.getenv().getOrDefault("INTEGRATION_DB_USERNAME", "poly");
        password = System.getenv().getOrDefault("INTEGRATION_DB_PASSWORD", "");

        // 테스트 전용 빈 DB에서만 실행: 기존 DB에는 절대 사용하지 않는다.
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {
            String script = Files.readString(Path.of("docs/sql/mariadb_schema.sql"),
                    StandardCharsets.UTF_8);
            String sql = script.lines()
                    .filter(line -> !line.stripLeading().startsWith("--"))
                    .reduce("", (left, right) -> left + "\n" + right);
            for (String command : sql.split(";")) {
                if (!command.isBlank()) {
                    statement.execute(command.strip());
                }
            }
        }

        Configuration config = new Configuration();
        config.getTypeAliasRegistry().registerAliases("kopo.poly.dto");
        config.setMapUnderscoreToCamelCase(true);
        config.setEnvironment(new Environment("mariadb", new JdbcTransactionFactory(),
                new UnpooledDataSource("org.mariadb.jdbc.Driver", url, username, password)));

        for (String file : List.of("NoticeMapper.xml", "UserInfoMapper.xml",
                "MovieMapper.xml", "OcrMapper.xml")) {
            String resource = "mapper/" + file;
            try (InputStream input = Resources.getResourceAsStream(resource)) {
                XMLMapperBuilder parser = new XMLMapperBuilder(
                        input, config, resource, config.getSqlFragments());
                parser.parse();
            }
        }
        sessionFactory = new SqlSessionFactoryBuilder().build(config);
    }

    private UserInfoDTO newUser(SqlSession session) throws Exception {
        String userId = "student_" + UUID.randomUUID().toString().substring(0, 8);
        UserInfoDTO dto = new UserInfoDTO();
        dto.setUserId(userId);
        dto.setUserName("테스트 학생");
        dto.setPassword("TEST_ONLY_DISABLED_PASSWORD");
        dto.setEmail(userId + "@example.invalid");
        session.getMapper(IUserInfoMapper.class).insertUserInfo(dto);
        return dto;
    }

    @Test
    void userMapperExecutesInsertFindLoginAndUpdate() throws Exception {
        try (SqlSession session = sessionFactory.openSession(false)) {
            IUserInfoMapper mapper = session.getMapper(IUserInfoMapper.class);
            UserInfoDTO user = newUser(session);

            assertEquals("Y", mapper.getUserIdExists(user).getExistsYn());
            assertEquals("Y", mapper.getEmailExists(user).getExistsYn());
            assertEquals(user.getUserId(), mapper.getLogin(user).getUserId());
            assertEquals(user.getUserId(), mapper.getUserId(user).getUserId());

            user.setPassword("TEST_ONLY_NEW_PASSWORD");
            assertEquals(1, mapper.updatePassword(user));
            assertNotNull(mapper.getLogin(user));
            session.rollback();
        }
    }

    @Test
    void noticeMapperSupportsGeneratedIdsDateFormatAndCrud() throws Exception {
        try (SqlSession session = sessionFactory.openSession(false)) {
            newUser(session);
            UserInfoDTO user = newUser(session);
            INoticeMapper mapper = session.getMapper(INoticeMapper.class);

            NoticeDTO dto = new NoticeDTO();
            dto.setTitle("MariaDB 공지 실습");
            dto.setNoticeYn("N");
            dto.setContents("생성 및 수정 실습");
            dto.setUserId(user.getUserId());

            mapper.insertNoticeInfo(dto);
            assertNotNull(dto.getNoticeSeq());
            assertFalse(dto.getNoticeSeq().isBlank());

            List<NoticeDTO> notices = mapper.getNoticeList();
            assertEquals(1, notices.size());
            assertEquals(dto.getTitle(), notices.get(0).getTitle());
            assertTrue(notices.get(0).getRegDt().matches("\\d{4}-\\d{2}-\\d{2}"));

            mapper.updateNoticeReadCnt(dto);
            assertEquals("1", mapper.getNoticeInfo(dto).getReadCnt());

            dto.setTitle("수정된 공지");
            mapper.updateNoticeInfo(dto);
            assertEquals("수정된 공지", mapper.getNoticeInfo(dto).getTitle());

            mapper.deleteNoticeInfo(dto);
            assertNull(mapper.getNoticeInfo(dto));
            session.rollback();
        }
    }

    @Test
    void movieMapperGeneratesIdsAndSortsRankNumerically() throws Exception {
        try (SqlSession session = sessionFactory.openSession(false)) {
            IMovieMapper mapper = session.getMapper(IMovieMapper.class);
            for (String rank : List.of("10", "2", "1")) {
                MovieDTO dto = new MovieDTO();
                dto.setCollectTime("20261018");
                dto.setMovieRank(rank);
                dto.setMovieNm("영화 순위 " + rank);
                dto.setRegId("test");
                assertEquals(1, mapper.insertMovieInfo(dto));
                assertNotNull(dto.getSeq());
            }
            MovieDTO query = new MovieDTO();
            query.setCollectTime("20261018");
            List<MovieDTO> result = mapper.getMovieInfo(query);
            assertEquals(List.of("1", "2", "10"),
                    result.stream().map(MovieDTO::getMovieRank).toList());
            assertEquals(3, mapper.deleteMovieInfo(query));
            assertTrue(mapper.getMovieInfo(query).isEmpty());
            session.rollback();
        }
    }

    @Test
    void ocrMapperUsesAutomaticSequence() throws Exception {
        try (SqlSession session = sessionFactory.openSession(false)) {
            OcrDTO dto = new OcrDTO();
            dto.setFileName("sample.png");
            dto.setFilePath("/tmp");
            dto.setOrgFileName("original.png");
            dto.setExt("png");
            dto.setTextFromImage("한글 OCR 테스트");
            dto.setRegId("test");

            assertEquals(1, session.getMapper(IOcrMapper.class).insertOcrInfo(dto));
            try (Statement statement = session.getConnection().createStatement();
                 ResultSet rows = statement.executeQuery(
                         "SELECT OCR_TEXT FROM OCR_INFO WHERE SAVE_FILE_NAME='sample.png'")) {
                assertTrue(rows.next());
                assertEquals("한글 OCR 테스트", rows.getString(1));
            }
            session.rollback();
        }
    }
}
