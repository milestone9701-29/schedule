package com.tr.schedule.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Signature;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;
    // 1시간
    private final long ACCESS_TOKEN_VALIDITY_MS = 60*60*1000L; // 1ms 0.001초
    // token 생성
    public String generateToken(CustomUserDetails userDetails){
        Date now=new Date();
        Date expiry=new Date(now.getTime()+ACCESS_TOKEN_VALIDITY_MS);

        return Jwts.builder()
            .setSubject(userDetails.getId().toString()) //지원중단?
            .claim("email", userDetails.getUsername())
            .setIssuedAt(now) // 지원중단ㅋㅋ
            .setExpiration(expiry)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 지원중단ㅋㅋㅋㅋㅋ
            .compact();
    }
    private Key getSigningKey(){
        byte[] keyBytes=secretKey.getBytes(StandardCharsets.UTF_8); // kor
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // token에서 userId 추출
    public Long getUserId(String token){
        Claims claims=parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }
    public boolean validateToken(String token){ // 검증
        try{
            parseClaims(token);
            return true;
        }catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }
    public Claims parseClaims(String token){
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

}
