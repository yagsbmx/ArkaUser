package com.example.arkauser.application.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    private static final String SECRET = "short_secret_key_32_chars_len!!!";
    private static final long EXPIRATION_TIME = 3600000;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + EXPIRATION_TIME);
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(String subject, Long userId, Object roles) {
        Map<String, Object> claims = Map.of(
                "uid", userId,
                "roles", roles
        );
        return generateToken(subject, claims);
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        final String username = getSubject(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String getSubject(String token) {
        return getAllClaims(token).getSubject();
    }

    public Long getUserId(String token) {
        Object v = getAllClaims(token).get("uid");
        return v == null ? null : Long.parseLong(v.toString());
    }

    public List<String> getRoles(String token) {
        Object r = getAllClaims(token).get("roles");
        if (r == null) return List.of();
        if (r instanceof String s) return List.of(s);
        if (r instanceof Collection<?> c) return c.stream().map(Object::toString).toList();
        return List.of(r.toString());
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return getAllClaims(token).getExpiration().before(new Date());
    }
}
