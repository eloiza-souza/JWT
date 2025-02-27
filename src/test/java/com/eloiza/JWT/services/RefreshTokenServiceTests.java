package com.eloiza.JWT.services;

import com.eloiza.JWT.infra.jwt.JwtTokenProvider;
import com.eloiza.JWT.models.RefreshToken;
import com.eloiza.JWT.models.User;
import com.eloiza.JWT.repositories.RefreshTokenRepository;
import com.eloiza.JWT.repositories.UserRepository;
import com.eloiza.JWT.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static com.eloiza.JWT.util.TestDataFactory.REFRESH_TOKEN;
import static com.eloiza.JWT.util.TestDataFactory.TEST_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RefreshTokenServiceTests {

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createRefreshToken_Success() {
        User user = TestDataFactory.createDefaultUser();
        RefreshToken expectedRefreshToken = new RefreshToken();
        expectedRefreshToken.setToken(REFRESH_TOKEN);
        expectedRefreshToken.setUser(user);

        when(userRepository.findByUsername(TEST_USER)).thenReturn(Optional.of(user));
        doNothing().when(refreshTokenRepository).deleteByUserId(user.getId());
        when(jwtTokenProvider.generateRefreshToken(TEST_USER)).thenReturn(REFRESH_TOKEN);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(expectedRefreshToken);

        RefreshToken actualRefreshToken = refreshTokenService.createRefreshToken(TEST_USER);

        assertNotNull(actualRefreshToken);
        assertEquals(REFRESH_TOKEN, actualRefreshToken.getToken());
        assertEquals(user, actualRefreshToken.getUser());

        verify(userRepository).findByUsername(TEST_USER);
        verify(refreshTokenRepository).deleteByUserId(user.getId());
        verify(jwtTokenProvider).generateRefreshToken(TEST_USER);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void createRefreshToken_NonExistentUser() {

        when(userRepository.findByUsername(TEST_USER)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                refreshTokenService.createRefreshToken(TEST_USER));

        assertEquals("Usuário não encontrado", exception.getMessage());

        verify(userRepository).findByUsername(TEST_USER);
        verify(refreshTokenRepository, never()).deleteByUserId(any(Long.class));
        verify(jwtTokenProvider, never()).generateRefreshToken(TEST_USER);
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void findByToken_Success(){
        RefreshToken refreshToken = TestDataFactory.createDefaultRefreshToken();
        when(refreshTokenRepository.findByToken(REFRESH_TOKEN)).thenReturn(Optional.of(refreshToken));

        Optional<RefreshToken> result = refreshTokenService.findByToken(REFRESH_TOKEN);

        assertEquals(REFRESH_TOKEN, result.get().getToken());
    }

    @Test
    void findByToken_NonExistentToken(){
        RefreshToken refreshToken = TestDataFactory.createDefaultRefreshToken();
        when(refreshTokenRepository.findByToken(REFRESH_TOKEN)).thenReturn(Optional.empty());

        Optional<RefreshToken> result = refreshTokenService.findByToken(REFRESH_TOKEN);

        assertFalse(result.isPresent());
        verify(refreshTokenRepository).findByToken(REFRESH_TOKEN);
    }

    @Test
    void verifyExpiration_NonExpiredToken(){
        RefreshToken refreshToken = TestDataFactory.createDefaultRefreshToken();

        when(jwtTokenProvider.isTokenExpired(REFRESH_TOKEN)).thenReturn(false);

        RefreshToken result = refreshTokenService.verifyExpiration(refreshToken);

        assertNotNull(result);
        assertEquals(REFRESH_TOKEN, result.getToken());
        verify(jwtTokenProvider).isTokenExpired(REFRESH_TOKEN);
        verify(refreshTokenRepository, never()).delete(refreshToken);
    }

    @Test
    void verifyExpiration_expiredToken(){
        RefreshToken refreshToken = TestDataFactory.createDefaultRefreshToken();

        when(jwtTokenProvider.isTokenExpired(REFRESH_TOKEN)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> refreshTokenService.verifyExpiration(refreshToken));

        assertEquals(" Refresh token is expired. Please make a new login..!", exception.getMessage());
        verify(jwtTokenProvider).isTokenExpired(REFRESH_TOKEN);
        verify(refreshTokenRepository).delete(refreshToken);
    }

}
