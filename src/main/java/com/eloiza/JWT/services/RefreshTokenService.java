package com.eloiza.JWT.services;

import com.eloiza.JWT.infra.jwt.JwtTokenProvider;
import com.eloiza.JWT.models.RefreshToken;
import com.eloiza.JWT.models.User;
import com.eloiza.JWT.repositories.RefreshTokenRepository;
import com.eloiza.JWT.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RefreshTokenService {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refresh-expiration}")
    private long jwtRefreshExpiration;

    @Transactional
    public RefreshToken createRefreshToken(String username) {

        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Usuário não encontrado"));
        refreshTokenRepository.deleteByUserId(user.getId());
        RefreshToken refreshToken = new RefreshToken();
        String token = jwtTokenProvider.generateRefreshToken(username);
        refreshToken.setToken(token);
        refreshToken.setUser(user);

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        String token = refreshToken.getToken();
        if (jwtTokenProvider.isTokenExpired(token)) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException(" Refresh token is expired. Please make a new login..!");
        }
        return refreshToken;
    }

}
