package codebadger.virtual_launch.common.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    private final Key signingKey;
    private final long accessTokenValidityInMilliseconds;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-validity}") long accessTokenValidityInMilliseconds) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
    }

    // 1. Access Token 생성
    public String createAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    // 2. 토큰으로부터 인증 정보 추출
    public Authentication getAuthentication(String token) {
        // 토큰 복호화
        Claims claims = parseClaims(token);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        // 비밀번호는 이미 인증된 상태이므로 빈 문자열로 처리
        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // 3. 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    // 내부적으로 토큰을 파싱하여 Claims를 반환하는 헬퍼 메서드
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}