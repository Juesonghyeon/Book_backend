package co.books.api.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * JWT 토큰 생성·파싱·검증을 담당한다.
 * 서명 키는 애플리케이션 시작 시 임의 생성 — 재시작하면 기존 토큰이 모두 무효화된다.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;

    @Value("${jwt.issuer}")
    private String issuer;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        this.signingKey = Jwts.SIG.HS256.key().build();
    }

    /** userId 를 subject 로 하는 액세스 토큰을 발급한다. */
    public String createToken(String userId) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userId)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenValidity))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    /** 토큰 서명·만료 여부를 검증한다. */
    public boolean validate(String token) {
        try {
            Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("유효하지 않은 JWT 토큰: {}", e.getMessage());
            return false;
        }
    }

    /** 액세스 토큰 유효시간을 초 단위로 반환한다. */
    public long getAccessTokenValiditySeconds() {
        return accessTokenValidity / 1000;
    }

    /** 토큰에서 userId (subject) 를 추출한다. */
    public String getUserId(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /** 토큰으로부터 Spring Security Authentication 객체를 생성한다. */
    public Authentication getAuthentication(String token) {
        String userId = getUserId(token);
        return new UsernamePasswordAuthenticationToken(
                userId,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}