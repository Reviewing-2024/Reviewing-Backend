package com.reviewing.review.config.jwt;

import static io.jsonwebtoken.Jwts.parserBuilder;

import com.reviewing.review.member.domain.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    public String createAccessToken(Member member) {

        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam("type", "jwt")
                .claim("id", member.getMemberId())
                .claim("nickname", member.getNickname())
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)))
//                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 2)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    // token에 대한 사용자 속성정보 조회
    public Member getMemberByAccessToken(String token) {
        Claims claims = parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        Long memberId = (Long) claims.get("id");
        String nickname = (String) claims.get("nickname");
        return new Member(memberId, nickname);
    }

    // 토큰에서 memberId 조회
    public Long getMemberIdByRefreshToken(String token) {
        try {
            Claims claims = parserBuilder()
                    .setSigningKey(jwtSecretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return (Long) claims.get("id");
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String token) {
        if (token == null) {
            log.info("Token is null");
            return false;
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

}
