package co.books.api.security;

import co.books.api.user.entity.UserEntity;
import co.books.api.user.repo.UserRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * H2 인메모리 DB 를 사용한 JWT 인증 통합 테스트.
 * 실제 PostgreSQL 없이도 실행된다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        // schema.sql 은 PostgreSQL 전용 타입을 사용하므로 H2 에서는 비활성화
        "spring.sql.init.mode=never"
})
@DisplayName("JWT 인증 통합 테스트")
class AuthIntegrationTest {

    private static final String TEST_USER_ID = "test-user-001";
    private static final String TEST_EMAIL    = "tester@books.co";
    private static final String TEST_PASSWORD = "password1234";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        UserEntity user = new UserEntity();
        user.setUserId("test-user-001");
        user.setEmail(TEST_EMAIL);
        user.setPasswd(passwordEncoder.encode(TEST_PASSWORD));
        user.setName("테스트유저");
        userRepository.save(user);
    }

    @Test
    @DisplayName("올바른 이메일·비밀번호로 로그인하면 accessToken 과 name 을 반환한다")
    void 로그인_성공_토큰_반환() throws Exception {
        mockMvc.perform(post("/api/v1/login")
                        .param("userId", TEST_USER_ID)
                        .param("passwd", TEST_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").isNumber())
                .andExpect(jsonPath("$.userId").value(TEST_USER_ID))
                .andExpect(jsonPath("$.name").value("테스트유저"));
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인하면 401 을 반환한다")
    void 로그인_실패_잘못된_비밀번호() throws Exception {
        mockMvc.perform(post("/api/v1/login")
                        .param("userId", TEST_USER_ID)
                        .param("passwd", "wrongpassword"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 로그인하면 401 을 반환한다")
    void 로그인_실패_존재하지않는_아이디() throws Exception {
        mockMvc.perform(post("/api/v1/login")
                        .param("userId", "notexist-user")
                        .param("passwd", TEST_PASSWORD))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("토큰 없이 보호 엔드포인트에 접근하면 401 을 반환한다")
    void 미인증_보호_엔드포인트_접근_401() throws Exception {
        mockMvc.perform(get("/test"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("유효한 토큰으로 보호 엔드포인트에 접근하면 200 을 반환한다")
    void 유효한_토큰으로_보호_엔드포인트_접근_200() throws Exception {
        // 로그인하여 토큰 발급
        MvcResult loginResult = mockMvc.perform(post("/api/v1/login")
                        .param("userId", TEST_USER_ID)
                        .param("passwd", TEST_PASSWORD))
                .andExpect(status().isOk())
                .andReturn();

        String accessToken = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.accessToken");

        // 토큰으로 보호 엔드포인트 접근
        mockMvc.perform(get("/test")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("조작된 토큰으로 접근하면 401 을 반환한다")
    void 조작된_토큰_접근_401() throws Exception {
        mockMvc.perform(get("/test")
                        .header("Authorization", "Bearer tampered.invalid.token"))
                .andExpect(status().isUnauthorized());
    }
}