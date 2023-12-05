package com.hotel.api.booking.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JwtUtil {

    private static final String SECRET_KEY = "NnhYNlF1UUg1Z1BCcDFkRFBuYmVPdDA1YjFKek9DaFk=";

    private final Claims claims;

    public JwtUtil(String token) {
        claims = extractClaims(token);
    }

    public String extractUsername() {
        return extractClaim(Claims::getSubject);
    }

    public static String generateToken(UserDetails details) {
        return generateToken(details, new HashMap<>());
    }

    public boolean isTokenValid(UserDetails details) {
        return extractUsername().equals(details.getUsername()) && !isTokenExpired();
    }

    private boolean isTokenExpired() {
        return extractExpirationDate().before(new Date(System.currentTimeMillis()));
    }

    private Date extractExpirationDate() {
        return extractClaim(Claims::getExpiration);
    }

    public static String generateToken(UserDetails details, Map<String, Object> otherClaims) {
        long TWO_DAYS = 2 * 24 * 60 * 60 * 1000L;
        return Jwts
                .builder()
                .setClaims(otherClaims)
                .setSubject(details.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TWO_DAYS))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private static Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private <T> T extractClaim(Function<Claims, T> claimResolver) {
        return claimResolver.apply(claims);
    }

    private Claims extractClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignKey())
                .parseClaimsJws(token)
                .getBody();
    }
}
