package co.books.api.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtTokenProvider 단위 테스트")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenValidity", 1_800_000L);
        ReflectionTestUtils.setField(jwtTokenProvider, "issuer", "test-issuer");
        jwtTokenProvider.init();
    }

    @Test
    @DisplayName("userId 로 토큰 생성에 성공한다")
    void 토큰_생성_성공() {
        String token = jwtTokenProvider.createToken("1");
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("유효한 토큰은 검증에 통과한다")
    void 유효한_토큰_검증_성공() {
        String token = jwtTokenProvider.createToken("1");
        assertThat(jwtTokenProvider.validate(token)).isTrue();
    }

    @Test
    @DisplayName("잘못된 형식의 토큰은 검증에 실패한다")
    void 잘못된_토큰_검증_실패() {
        assertThat(jwtTokenProvider.validate("invalid.token.value")).isFalse();
    }

    @Test
    @DisplayName("빈 문자열 토큰은 검증에 실패한다")
    void 빈_토큰_검증_실패() {
        assertThat(jwtTokenProvider.validate("")).isFalse();
    }

    @Test
    @DisplayName("토큰에서 userId 를 정확하게 추출한다")
    void userId_추출_성공() {
        String userId = "42";
        String token = jwtTokenProvider.createToken(userId);
        assertThat(jwtTokenProvider.getUserId(token)).isEqualTo(userId);
    }

    @Test
    @DisplayName("만료된 토큰은 검증에 실패한다")
    void 만료된_토큰_검증_실패() {
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenValidity", -1_000L);
        String token = jwtTokenProvider.createToken("1");
        assertThat(jwtTokenProvider.validate(token)).isFalse();
    }

    @Test
    @DisplayName("토큰으로부터 Authentication 객체를 생성한다")
    void Authentication_생성_성공() {
        String token = jwtTokenProvider.createToken("7");
        var authentication = jwtTokenProvider.getAuthentication(token);

        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo("7");
        assertThat(authentication.getAuthorities()).isNotEmpty();
    }
}