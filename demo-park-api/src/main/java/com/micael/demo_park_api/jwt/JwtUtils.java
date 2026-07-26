package com.micael.demo_park_api.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Slf4j
public class JwtUtils {

    private static final String JWT_BEARER = "Bearer ";
    private static final String JWT_AUTHORIZATION = "Authorization";
    private static final String SECRET_KEY = "h25@1jhd#2h1Adh&em-lA*sh--20ha-2";
    private static final long EXPIRE_DAYS = 0;
    private static final long EXPIRE_HOURS = 0;
    private static final long EXPIRE_MINUTES = 2;

    private JwtUtils(){
    }

    private static SecretKey generatedKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }


    private static Date toExpireDtae(Date start){
        LocalDateTime dateTime = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime end = dateTime.plusDays(EXPIRE_DAYS).plusHours(EXPIRE_HOURS).plusMinutes(EXPIRE_MINUTES);
        return Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static JwtToken createToken(String username, String role){
        Date issuedAt = new Date();
        Date limit = toExpireDtae(issuedAt);

        String token = Jwts.builder()
            .header().add("typ", "jwt").and()
            .subject(username)
            .issuedAt(issuedAt)
            .expiration(limit)
            .claim("role", role)
            .signWith(generatedKey())
            .compact();

        return new JwtToken(token);
    }

    private static Claims getClaimsFromToken(String token){
        try{
            return Jwts.parser()
                .verifyWith(generatedKey())
                .build()
                .parseSignedClaims(refactorToken(token))
                .getPayload();
        }
        catch (JwtException ex){
            log.error("Token inválido: {}", ex.getMessage());
        }
        return null;
    }

    public static String getUsernameFromToken(String token){
       Claims claims = getClaimsFromToken(token);
       return claims != null ? claims.getSubject() : null;
    }

    public static Boolean isTokenValid(String token){
        try{
            Jwts.parser()
                .verifyWith(generatedKey())
                .build()
                .parseSignedClaims(refactorToken(token));
            return true;
        }
        catch (JwtException ex){
            log.error("Token inválido: {}", ex.getMessage());
        }
        return false;
    }

    private static String refactorToken(String token){
        if(token.contains(JWT_BEARER))
            return token.substring(JWT_BEARER.length());
        return token;
    }

}
