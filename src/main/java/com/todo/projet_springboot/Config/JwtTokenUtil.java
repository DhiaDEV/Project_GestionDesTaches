package com.todo.projet_springboot.Config;

import com.todo.projet_springboot.Entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String jwtSecret; // Secret key for signing the token

    @Value("${jwt.expiration}")
    private long jwtExpirationMs; // Token expiration time in milliseconds

    /**
     * Generates a JWT token for the given user.
     *
     * @param user the user entity
     * @return the generated token
     */
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail()) // User's email as the subject
                .claim("username", user.getUsername()) // Add username as a custom claim
                .claim("id", user.getId()) // Add user ID as a custom claim
                .claim("Role", user.getRole())
                .setIssuedAt(new Date()) // Set the issued date
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // Set expiration date
                .signWith(SignatureAlgorithm.HS512, jwtSecret) // Sign the token using HS512 algorithm
                .compact();
    }

    /**
     * Validates the JWT token.
     *
     * @param token the JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // Token is invalid
        }
    }

    /**
     * Extracts the user's email (subject) from the token.
     *
     * @param token the JWT token
     * @return the user's email
     */
    public String getUserEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * Extracts the username from the token.
     *
     * @param token the JWT token
     * @return the username
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("username", String.class);
    }

    /**
     * Extracts the user ID from the token.
     *
     * @param token the JWT token
     * @return the user ID
     */
    public Long getIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("id", Long.class);
    }
    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("Role", String.class);
    }

    /**
     * Helper method to extract claims from the token.
     *
     * @param token the JWT token
     * @return the claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }
}
