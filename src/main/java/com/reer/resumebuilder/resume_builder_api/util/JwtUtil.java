package com.reer.resumebuilder.resume_builder_api.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private SecretKey secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    public JwtUtil(@Value("${jwt.secret}") String localJwtsecret) {
        this.secretKey = Keys.hmacShaKeyFor(localJwtsecret.getBytes());
    }

    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        Map<String, Object> claims = new HashMap<>();
        claims.put("localName", email);

        return Jwts.builder()
                .header().add("typ", "JWT").and()
                .subject(email)
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String username, UserDetails userDetails, String token) {
        String tokenUsername = extractUsername(token);
        return tokenUsername.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        Date expiry = extractAllClaims(token).getExpiration();
        return expiry.before(new Date());
    }


    public Claims extractAllClaims(String token) {
        return getParser().parseSignedClaims(token).getPayload();
    }

    public String extractUsername(String token) {
        return getParser().parseSignedClaims(token).getPayload().getSubject();
    }


    private JwtParser getParser() {
        return Jwts.parser().verifyWith(secretKey).build();
    }


}
