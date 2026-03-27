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

    // 1. Access Token 생성 (인증 성공 시 호출)
    public String createAccessToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(userDetails.getId().toString()) // sub에 ID 저장
                .claim("email", userDetails.getUsername())   // 편리함을 위해 이메일도 포함
                .claim("auth", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))   // 권한 정보 저장 🎟
                .setIssuedAt(new Date(now))
                .setExpiration(validity)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // 2. 토큰으로부터 인증 정보 추출 (필터에서 요청마다 호출)
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        // 1. 토큰에서 필요한 정보들 추출
        Long memberId = Long.valueOf(claims.getSubject());
        String email = claims.get("email", String.class);
        String roleWithPrefix = claims.get("auth", String.class); // "ROLE_USER" 형태

        // 2. Enum 변환을 위해 "ROLE_" 접두사 제거 (예: "ROLE_USER" -> "USER")
        String role = roleWithPrefix.replace("ROLE_", "");

        // 3. 재조립
        CustomUserDetails userDetails = CustomUserDetails.fromValues(memberId, email, role);

        // 4. 인증 토큰 생성 및 반환
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    // 3. 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("유효하지 않은 JWT 토큰입니다: {}", e.getMessage());
        }
        return false;
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}