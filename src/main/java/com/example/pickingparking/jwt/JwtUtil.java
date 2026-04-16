package com.example.pickingparking.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {


    private final SecretKey secretKey = Keys.hmacShaKeyFor("pickingparking_secret_key_for_jwt_validation_purpose_only".getBytes());
    //토큰 암호화 비밀 키

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    //토큰 만료인지 확인


    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis())) // 토큰 발급 시간
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 토큰 만료 시간 10시간
                .signWith(secretKey) // 비밀 키로 서명
                .compact();
    }
    //이메일로 토큰설정


    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
        //토큰 유효성 검증
    }
}
