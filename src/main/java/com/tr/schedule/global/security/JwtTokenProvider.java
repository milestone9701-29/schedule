package com.tr.schedule.global.security;


import com.tr.schedule.global.exception.ErrorCode;
import com.tr.schedule.global.exception.JwtAuthenticationException;
import io.jsonwebtoken.*;

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
    // application.yml - .properties
    // jwt:
    //  secret: "randoms3cretkey_example_aaaaaah1201931randoms3cretkey_example_aaaaaah1201931"
    private final SecretKey secretKey; // HMAC
    private final long accessTokenValidityMs;
    private final long refreshTokenValidityMs;

    public JwtTokenProvider(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.access-token-validity-ms:3600000}") long accessTokenValidityMs,
        @Value("${jwt.refresh-token-validity-ms:1209600000}") long refreshTokenValidityMs
    ) {
        this.secretKey=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenValidityMs=accessTokenValidityMs;
        this.refreshTokenValidityMs=refreshTokenValidityMs;
    }
    // @Value("${jwt.access-token-validity-ms}");

    public String generateAccessToken(CustomUserDetails userDetails){
        return generateToken(userDetails, accessTokenValidityMs, "access");
    }

    public String generateRefreshToken(CustomUserDetails userDetails){
        return generateToken(userDetails, refreshTokenValidityMs, "refresh");
    }

    // token 생성
    private String generateToken(CustomUserDetails userDetails, long accessTokenValidityMs, String type){
        Date now=new Date(); // 현재 시각.
        Date expiry=new Date(now.getTime()+accessTokenValidityMs); // 현재 + 1시간 뒤 만료.

        /*
        userDetails.getAuthorities() : Collection<? extends GrantedAuthority>
        .map(GrantedAuthority::getAuthority) ROLE_USER, ROLE_ADMIN : ("ROLE_" + name)
        */
        List<String> roles=userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList(); // ROLE_USER

        /* Builder Chain
        .subject(userDetails.getId().toString()) : subject : UserId(toString()) : getUserId 등등..
        .claim("email",userDetails.getUsername()) : CustomClaim : username(=이메일)
        .claim("roles",roles) : 예시 : "roles", ["ROLE_USER", "ROLE_ADMIN"]
        .claim("type", type) : access refresh 구분
        .issuedAt(now) : 발급된 시간 : 지금
        .expiration(expiry) : 만료일 : 1시간
        .signWith(getSigningKey()) : HS256 서명
        .compact() : header.payload.signature 형태의 문자열(JWS)로 직렬화.
         */

        return Jwts.builder()
            .subject(userDetails.getId().toString())
            .claim("email", userDetails.getUsername())
            .claim("roles", roles)
            .claim("type", type)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(getSigningKey(), Jwts.SIG.HS256)
            .compact();
    }


    /* secretKey 문자열 : UTF-8 : 한국어 유니코드.
    Keys.hmacShaKeyFor(keyBytes); : 길이 32byte 이상.
    내부적으로 SecretKey 만들어서 반환
    -> signWith, verifyWith 양쪽에 사용. */
    private SecretKey getSigningKey(){
        return secretKey;
    }

    /*
    parseClaims(token) : payload(claims)
    -> claims.getSubject() : Subject : userId 문자열
    -> Long.parseLong()으로 파싱 : userId(pk) 생성.
     */
    public Long getUserId(String token){
        Claims claims=parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /* validateToken
    -> 1. 파싱 -> 1
    -> 2. JwtException | IllegalArgumentException e : 0
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    } */

    public void validateTokenOrThrow(String token){
        try{
            parseClaims(token);
        }catch(ExpiredJwtException e){
            throw new JwtAuthenticationException(ErrorCode.JWT_EXPIRED, e);
        } catch(JwtException|IllegalArgumentException e){ // 서명 깨짐, 형식 오류 등..
            throw new JwtAuthenticationException(ErrorCode.JWT_INVALID, e);
        }
    }


    // JJWT ver 0.12
    /*
    .verifyWith(getSigningKey()) : 서명 검증
    .parseSignedClaims(token) : 서명된 JWS 파싱
    .getPayload(); : 적재 : payload -> Claims 객체 반환.
    */
    public Claims parseClaims(String token){ // 파싱
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
    /*
    예외 경우의 수
    1. 서명 위조, 불일치
    2. expired
    3. 형식 깨짐.
    */

    // test
    public String generateExpiredToken(Long userId){
        Date now=new Date();
        Date past=new Date(now.getTime()-1000L*60); // 1분 전.

        return Jwts.builder()
            .subject(String.valueOf(userId))
            .issuedAt(past)
            .expiration(past) // 이미 만료
            .signWith(getSigningKey(), Jwts.SIG.HS256)
            .compact();
    }
}
/*
JWT
1. header :
{
  "typ": "JWT", : 토큰 타입
  "alg": "HS256" : 해싱 알고리즘 지정.
}
2. Payload : registered, public, private claims(name, value의 한 쌍으로 구성)
1). registered : optional : 이름이 이미 정해져있다.
(1). iss : 토큰 발급자. issuer
(2). sub : 토큰 제목. Subject
(3). aud : 토큰 대상자. audience
(4). exp : 만료시간. expiration. 현재 시간보다 이후로 설정되어야 한다.
* NumericDate : 1480849147370 : (4),(5)
- 초(second) 단위 : Unix time.
- Java Date : ms 단위
- JJWT 등의 라이브러리 : ms <-> second 변환


(5). nbf : 토큰의 활성 날짜. Not Before. 이 날짜가 지나기 전까진 토큰이 처리되지 않는다.
(6). iat : 토큰이 발급된 시간. issued at. 토큰의 age가 얼마나 되었는지 판단.
(7). jti : JWT의 고유 식별자. 중복 처리 방지를 위함. 일회용 토큰에 사용하는 것이 대표 예시.

2). public : Collision resistant : Claim name을 URI 형식으로 짓는 편.
{
    "https://aaaaa.com/jwt_claims/is_admin": true
}
3). private : Server - Client 협의 하에 사용하는 Claim name
* 중복 충돌 주의.
{
    "iss": "aaaaa.com",
    "exp": "1485270000000",
    "https://aaaaa.com/jwt_claims/is_admin": true,
    "userId": "11028373725102",
    "username": "Jinsoo"
}
-> base64 : ICAgICJpc3MiOiAiYWFhYWEuY29tIiwKICAgICJleHAiOiAiMTQ4NTI3MDAwMDAwMCIsCiAgICAiaHR0cHM6Ly
9hYWFhYS5jb20vand0X2NsYWltcy9pc19hZG1pbiI6IHRydWUsCiAgICAidXNlcklkIjogIj
ExMDI4MzczNzI1MTAyIiwKICAgICJ1c2VybmFtZSI6ICJKaW5zb28i
* 사용 시 url-safe 유무 판단 : dA== : padding : 제거하는 것이 이롭다.
- dA== : 일반 Base64 예시
- JWT(Base64URL)토큰에서는 이미 라이브러리가 URL-safe 형식으로 만들기 때문에, 직접 패딩을 관리하지 않아도 괜찮다.
예시 : + -> - , / -> _ , padding = 생략 등..

3. 서명. Signature. : Header의 encode 값과 정보의 encode 값을 합친 후, 주어진 비밀키로 해싱하여 생성.
* 의사코드 예시.
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret)

헤더와 정보의 인코딩 값 사이에 . 을 넣어주고, 합치기 -> 비밀키의 값을 secret 으로 해싱을 하고 base64로 인코딩
-> . 을 중간자로 다 합쳐주면, 하나의 토큰이 완성

JWT.IO를 통한 검증.

4. 결론
Login 성공 -> CustomUserDetails : sub=id, email, roles, exp, iat 를 가진 HS256 토큰 발급.

출처 : https://velopert.com/2389
 */
