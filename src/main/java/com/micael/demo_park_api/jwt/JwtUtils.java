package com.micael.demo_park_api.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;


@Slf4j
@Component
public class JwtUtils {

    public static final String JWT_BEARER = "Bearer ";
    public static final String JWT_AUTHORIZATION = "Authorization";
    public static final long EXPIRE_DAYS = 0;
    public static final long EXPIRE_HOURS = 0;
    public static final long EXPIRE_MINUTES = 30;

    private final SecretKey secretKey;

    // A chave vem da propriedade jwt.secret (variavel de ambiente JWT_SECRET), nunca do codigo.
    // Keys.hmacShaKeyFor exige no minimo 32 bytes e falha na subida da aplicacao se for menor.
    public JwtUtils(@Value("${jwt.secret}") String secret){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }


    private static Date toExpireDtae(Date start){
        LocalDateTime dateTime = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime end = dateTime.plusDays(EXPIRE_DAYS).plusHours(EXPIRE_HOURS).plusMinutes(EXPIRE_MINUTES);
        return Date.from(end.atZone(ZoneId.systemDefault()).toInstant());
    }

    public JwtToken createToken(Long id, String username, String role){
        Date issuedAt = new Date();
        Date limit = toExpireDtae(issuedAt);

        String token = Jwts.builder()
            .header().add("typ", "jwt").and()
            .subject(username)
            .issuedAt(issuedAt)
            .expiration(limit)
            .claims(Map.of("id",id,"role", role))
            .signWith(secretKey)
            .compact();

        return new JwtToken(token);
    }

    private Claims getClaimsFromToken(String token){
        try{
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(refactorToken(token))
                .getPayload();
        }
        catch (JwtException ex){
            log.error("Token inválido: {}", ex.getMessage());
        }
        return null;
    }

    public String getUsernameFromToken(String token){
       Claims claims = getClaimsFromToken(token);
       return claims != null ? claims.getSubject() : null;
    }

    public Boolean isTokenValid(String token){
        try{
            Jwts.parser()
                .verifyWith(secretKey)
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
