package com.eloiza.JWT.infra.jwt;

import com.eloiza.JWT.controllers.dtos.Departments;
import com.eloiza.JWT.models.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static io.jsonwebtoken.Jwts.builder;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}") // Alterado para usar uma variável de ambiente
    private String jwtSecret;

    @Value("${jwt.expiration}") // Alterado para usar uma variável de ambiente
    private long jwtExpiration;

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();

        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Departments department = extractDepartment(authentication);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", roles);
        claims.put("department", department.name());

        return builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key())
                .compact();
    }

    private Departments extractDepartment(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getDepartment();
        }
        throw new IllegalArgumentException("Unable to extract department from authentication");
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateToken(String token, String username) {
        final String tokenUsername = getUsername(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    public String getUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}