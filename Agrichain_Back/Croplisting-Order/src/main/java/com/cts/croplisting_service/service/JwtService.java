//package com.cts.croplisting_service.service;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//import java.security.Key;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//
//@Service
//public class JwtService {
//
//    @Value("${jwt.secret}")
//    private String secretKey;
//
//    // 1. Extract Username
//    public String extractUserName(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    // 2. NEW: Extract Roles from the Claims
//    public List<String> extractRoles(String token) {
//        final Claims claims = extractAllClaims(token);
//
//        // DEBUG: This will print ALL claims to your console
//        // so you can see the correct key name
//        System.out.println("All JWT Claims: " + claims);
//
//        // Try "role" first, then "roles" as a backup
//        Object roleClaim = claims.get("role");
//        if (roleClaim == null) {
//            roleClaim = claims.get("roles");
//        }
//
//        if (roleClaim != null) {
//            return List.of(roleClaim.toString());
//        }
//
//        return List.of();
//    }
//
//    // 3. Validate Token
//    public boolean validateToken(String token, UserDetails userDetails) {
//        final String userName = extractUserName(token);
//        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//
//    // --- Helper Methods ---
//
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    private Claims extractAllClaims(String token) {
//        return Jwts.parser()
//                .verifyWith((javax.crypto.SecretKey) getSignInKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }
//
//    private Key getSignInKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//
//    private boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    private Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    // Updated to include Role in the token for testing
//    public String generateToken(String userName, String role) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("role", role); // Adding the role to the payload
//        return Jwts.builder()
//                .claims(claims)
//                .subject(userName)
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
//                .signWith(getSignInKey())
//                .compact();
//    }
//}