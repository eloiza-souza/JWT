package com.eloiza.JWT.infra.jwt;

import com.eloiza.JWT.controllers.dtos.Departments;
import com.eloiza.JWT.models.User;
import com.eloiza.JWT.services.CustomUserDetailsService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey = "";

    @Value("${jwt.expiration}")
    private long jwtExpirationDate;

    public String generateToken(Authentication authentication) {

        String username = authentication.getName();

        // Extrai as roles (authorities) do usuário
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String department = getDepartmentFromUser(authentication);
        // Adiciona claims personalizados
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", roles);
        claims.put("department", department);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationDate))
                .signWith(SignatureAlgorithm.HS256, key())
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String getUsername(String token) {
        // Atualizado para usar o método recomendado
        return Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private String getDepartmentFromUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            // Se você tiver uma classe CustomUserDetails que contém informações do usuário
            User user = (User) principal;
            return user.getDepartment().getName();
        }

        // Caso o departamento não esteja disponível, você pode retornar um valor padrão ou lançar uma exceção
        return "UNKNOWN";
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // Log de erro ou tratamento adicional
            return false;
        }
    }


}