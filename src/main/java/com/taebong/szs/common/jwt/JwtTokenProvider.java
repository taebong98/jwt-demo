package com.taebong.szs.common.jwt;

import com.taebong.szs.common.exception.JwtParsingException;
import com.taebong.szs.domain.vo.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.token-validity-in-minute}")
    private int tokenValidTimeMinute;

    public String createToken(User user) {
        String base64EncodedSecretKey = encodeBase64SecretKey(secretKey);
        Key key = getKeyFromBase64EncodedKey(base64EncodedSecretKey);

        Map<String, String> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("name", user.getName());
        claims.put("password", user.getPassword());
        claims.put("regNo", user.getRegNo());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUserId())
                .setIssuedAt(Calendar.getInstance().getTime())
                .setExpiration(getExpiration())
                .signWith(key)
                .compact();
    }

    public Claims getClaims(String token) {
        Key key = getKeyFromBase64EncodedKey(encodeBase64SecretKey(secretKey));
        String jws = token.replace("Bearer ", "");

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jws)
                    .getBody();
        } catch (SecurityException | MalformedJwtException e) {
            throw new JwtParsingException("잘못된 JWT 서명");
        } catch (ExpiredJwtException e) {
            throw new JwtParsingException("만료된 JWT 토큰");
        } catch (UnsupportedJwtException e) {
            throw new JwtParsingException("지원 되지 않는 JWT 토큰");
        } catch (IllegalArgumentException e) {
            throw new JwtParsingException("잘못된 토큰");
        }
    }

    private String encodeBase64SecretKey(String secretKey) {
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private Key getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
        byte[] keyBytes = Decoders.BASE64URL.decode(base64EncodedSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Date getExpiration() {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, tokenValidTimeMinute);
        return instance.getTime();
    }
}
