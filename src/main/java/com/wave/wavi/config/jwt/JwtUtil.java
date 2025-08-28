package com.wave.wavi.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String BEARER_PREFIX = "Bearer ";
    private final long TOKEN_TIME = 30 * 60 * 1000L;

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

   @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
   }

   public String createToken(String email) {
       Date date = new Date();
       return BEARER_PREFIX +
               Jwts.builder()
                       .setSubject(email)
                       .setExpiration(new Date(date.getTime() + TOKEN_TIME))
                       .setIssuedAt(date)
                       .signWith(key, signatureAlgorithm)
                       .compact();
   }

   public String getTokenFromHeader(HttpServletRequest request) {
       String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
       if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
           return bearerToken.substring(BEARER_PREFIX.length());
       }
       return null;
   }

   public boolean validateToken(String token) {
       try {
           Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
           return true;
       } catch (SecurityException | MalformedJwtException e) {
           log.error("Invalid JWT token: {}", e.getMessage());
       } catch (ExpiredJwtException e) {
           log.error("Expired JWT token: {}", e.getMessage());
       } catch (UnsupportedJwtException e) {
           log.error("Unsupported JWT token: {}", e.getMessage());
       } catch (IllegalArgumentException e) {
           log.error("JWT claims is empty: {}", e.getMessage());
       }
       return false;
   }

   public Claims getUserInfoFromToken(String token) {
       return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
   }
}
