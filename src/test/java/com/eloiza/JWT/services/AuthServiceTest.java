package com.eloiza.JWT.services;

import com.eloiza.JWT.controllers.dtos.AuthResponseDto;
import com.eloiza.JWT.controllers.dtos.LoginDto;
import com.eloiza.JWT.infra.jwt.JwtTokenProvider;
import com.eloiza.JWT.models.CustomUserDetails;
import com.eloiza.JWT.models.Department;
import com.eloiza.JWT.models.RefreshToken;
import com.eloiza.JWT.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void login_Success() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");

        User user = new User();
        user.setName("Test User");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRoles(Set.of());
        user.setDepartment(new Department("IT"));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(refreshTokenService.createRefreshToken("testuser")).thenReturn(refreshToken);
        when(jwtTokenProvider.generateAccessToken(any(Authentication.class))).thenReturn("access-token");

        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(customUserDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);

        AuthResponseDto response = authService.login(loginDto);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(refreshTokenService).createRefreshToken("testuser");


    }

}
