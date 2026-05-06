package co.books.api.security.jwt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtAuthenticationFilter 단위 테스트")
class JwtAuthenticationFilterTest {

    private JwtTokenProvider jwtTokenProvider;
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenValidity", 1_800_000L);
        ReflectionTestUtils.setField(jwtTokenProvider, "issuer", "test-issuer");
        jwtTokenProvider.init();
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("유효한 Bearer 토큰이 있으면 SecurityContext 에 Authentication 이 주입된다")
    void 유효한_토큰_SecurityContext_주입() throws Exception {
        String token = jwtTokenProvider.createToken("1");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);

        jwtAuthenticationFilter.doFilter(
                request, new MockHttpServletResponse(), new MockFilterChain());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo("1");
    }

    @Test
    @DisplayName("Authorization 헤더가 없으면 SecurityContext 가 비어있다")
    void 헤더_없음_SecurityContext_비어있음() throws Exception {
        jwtAuthenticationFilter.doFilter(
                new MockHttpServletRequest(),
                new MockHttpServletResponse(),
                new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("잘못된 토큰이 있으면 SecurityContext 가 비어있다")
    void 잘못된_토큰_SecurityContext_비어있음() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid.token.value");

        jwtAuthenticationFilter.doFilter(
                request, new MockHttpServletResponse(), new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Bearer 접두사 없이 오면 SecurityContext 가 비어있다")
    void Bearer_접두사_없음_SecurityContext_비어있음() throws Exception {
        String token = jwtTokenProvider.createToken("1");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", token);   // "Bearer " 없음

        jwtAuthenticationFilter.doFilter(
                request, new MockHttpServletResponse(), new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}