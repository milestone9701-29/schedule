package com.tr.schedule.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import java.util.Date;

import java.util.List;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;
    // 1시간
    private final long ACCESS_TOKEN_VALIDITY_MS = 60*60*1000L; // 1ms 0.001초 : 분리 필요.
    // token 생성
    public String generateToken(CustomUserDetails userDetails){
        Date now=new Date();
        Date expiry=new Date(now.getTime()+ACCESS_TOKEN_VALIDITY_MS);

        List<String> roles=userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList(); // ROLE_USER

        return Jwts.builder() // set
            .subject(userDetails.getId().toString()) // set
            .claim("email", userDetails.getUsername()) // 클레임.
            .claim("roles", roles)
            .issuedAt(now) // set
            .expiration(expiry) // set
            .signWith(getSigningKey()) // Key에서 알고리즘 유추 : 구버전은 따로 설정해줘야 함.
            .compact();
    }
    private SecretKey getSigningKey(){
        byte[] keyBytes=secretKey.getBytes(StandardCharsets.UTF_8); // kor
        return Keys.hmacShaKeyFor(keyBytes); // HS256 : 32자 이상 : Hash Based Message Authentication Code
    }

    // token에서 userId 추출
    public Long getUserId(String token){
        Claims claims=parseClaims(token); // 파싱
        return Long.parseLong(claims.getSubject()); // Long으로 파싱
    }

    public boolean validateToken(String token){ // 검증
        try{
            parseClaims(token);
            return true;
        }catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }

    public Claims parseClaims(String token){ // 파싱
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

}
