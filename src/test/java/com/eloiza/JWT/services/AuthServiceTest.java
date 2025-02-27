package com.eloiza.JWT.services;

import com.eloiza.JWT.controllers.dtos.AuthResponseDto;
import com.eloiza.JWT.controllers.dtos.LoginDto;
import com.eloiza.JWT.controllers.dtos.RefreshTokenRequestDto;
import com.eloiza.JWT.infra.jwt.JwtTokenProvider;
import com.eloiza.JWT.models.CustomUserDetails;
import com.eloiza.JWT.models.RefreshToken;
import com.eloiza.JWT.models.User;
import com.eloiza.JWT.repositories.RefreshTokenRepository;
import com.eloiza.JWT.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static com.eloiza.JWT.util.TestDataFactory.REFRESH_TOKEN;
import static com.eloiza.JWT.util.TestDataFactory.TEST_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private Authentication authentication;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void login_Success() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(TEST_USER);
        loginDto.setPassword("password");

        RefreshToken refreshToken = TestDataFactory.createDefaultRefreshToken();

        User user = TestDataFactory.createDefaultUser();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(refreshTokenService.createRefreshToken(TEST_USER)).thenReturn(refreshToken);
        when(jwtTokenProvider.generateAccessToken(any(Authentication.class))).thenReturn("access-token");

        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(customUserDetailsService.loadUserByUsername(TEST_USER)).thenReturn(userDetails);

        AuthResponseDto response = authService.login(loginDto);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals(REFRESH_TOKEN, response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(refreshTokenService).createRefreshToken(TEST_USER);
    }
    
    @Test
    public void login_InvalidAuthentcation() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(TEST_USER);
        loginDto.setPassword("password");

        User user = TestDataFactory.createDefaultUser();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> authService.login(loginDto));

        assertEquals("Invalid user request..!!", exception.getMessage());
    }

    @Test
    public void refreshAccessToken_Success(){
        RefreshTokenRequestDto refreshTokenRequestDto = TestDataFactory.createDefaultRefreshTokenRequestDto();
        User user = TestDataFactory.createDefaultUser();
        RefreshToken refreshToken = TestDataFactory.createDefaultRefreshToken();

        when(refreshTokenService.findByToken(REFRESH_TOKEN)).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(customUserDetailsService.loadUserByUsername(TEST_USER)).thenReturn(userDetails);
        when(jwtTokenProvider.generateAccessToken(any(Authentication.class))).thenReturn("access-token");

        AuthResponseDto response = authService.refreshAccessToken(refreshTokenRequestDto);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals(REFRESH_TOKEN, response.getToken());
        verify(refreshTokenService).findByToken(REFRESH_TOKEN);
        verify(refreshTokenService).verifyExpiration(refreshToken);
    }

    @Test
    public void refreshAccessToken_UnvalidToken(){
        RefreshTokenRequestDto refreshTokenRequestDto = TestDataFactory.createDefaultRefreshTokenRequestDto();

        when(refreshTokenService.findByToken(REFRESH_TOKEN)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->authService.refreshAccessToken(refreshTokenRequestDto));

        assertEquals("Refresh Token is not in DB..!!", exception.getMessage());
        verify(refreshTokenService).findByToken(REFRESH_TOKEN);
        verify(refreshTokenService, never()).verifyExpiration(any(RefreshToken.class));
    }

    @Test
    public void refreshAccessToken_ExpiratedToken(){
        RefreshTokenRequestDto refreshTokenRequestDto = TestDataFactory.createDefaultRefreshTokenRequestDto();
        RefreshToken refreshToken = TestDataFactory.createDefaultRefreshToken();

        when(refreshTokenService.findByToken(REFRESH_TOKEN)).thenReturn(Optional.of(refreshToken));

        when(refreshTokenService.verifyExpiration(refreshToken)).thenThrow(new RuntimeException(" Refresh token is expired. Please make a new login..!"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.refreshAccessToken(refreshTokenRequestDto));

        assertEquals(" Refresh token is expired. Please make a new login..!", exception.getMessage());
        verify(refreshTokenService).findByToken(any(String.class));
    }
}
