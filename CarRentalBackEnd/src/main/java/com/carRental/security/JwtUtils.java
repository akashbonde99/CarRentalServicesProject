package com.carRental.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    // Change 1: Return SecretKey instead of generic Key
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    // Change 2: Updated parser syntax for JJWT 0.12+
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Use verifyWith instead of setSigningKey
                .build()
                .parseSignedClaims(token) // parseSignedClaims instead of parseClaimsJws
                .getPayload(); // getPayload instead of getBody
    }

    public String generateToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role",
                userDetails.getAuthorities().stream()
                        .findFirst()
                        .map(GrantedAuthority::getAuthority)
                        .orElse("ROLE_USER"));
        claims.put("userId", userDetails.getId());
        claims.put("email", userDetails.getUsername());

        // Change 3: Updated builder syntax
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 10 * 60 * 60 * 1000))
                .signWith(getSigningKey(), Jwts.SIG.HS256) // Explicitly use Jwts.SIG
                .compact();
    }

    public Claims validateJwtToken(String token) {
        return extractAllClaims(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> (String) claims.get("role"));
    }
}