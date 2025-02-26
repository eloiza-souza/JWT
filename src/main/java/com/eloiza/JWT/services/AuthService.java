package com.eloiza.JWT.services;

import com.eloiza.JWT.controllers.dtos.AuthResponseDto;
import com.eloiza.JWT.controllers.dtos.LoginDto;
import com.eloiza.JWT.controllers.dtos.RefreshTokenRequestDto;
import com.eloiza.JWT.infra.jwt.JwtTokenProvider;
import com.eloiza.JWT.models.CustomUserDetails;
import com.eloiza.JWT.models.RefreshToken;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    public AuthResponseDto login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(),
                loginDto.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (authentication.isAuthenticated()) {
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginDto.getUsername());
            return createAuthResponse(loginDto.getUsername(), refreshToken.getToken());
        } else {
            throw new UsernameNotFoundException("Invalid user request..!!");
        }
    }

    public AuthResponseDto refreshAccessToken(RefreshTokenRequestDto refreshTokenRequestDTO) {
        return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> createAuthResponse(user.getUsername(), refreshTokenRequestDTO.getToken()))
                .orElseThrow(() -> new RuntimeException("Refresh Token is not in DB..!!"));
    }

    private AuthResponseDto createAuthResponse(String username, String refreshToken) {
        CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);

        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setAccessToken(accessToken);
        authResponseDto.setToken(refreshToken);
        return authResponseDto;
    }
}
