package com.eloiza.JWT.controllers;

import com.eloiza.JWT.controllers.dtos.AuthResponseDto;
import com.eloiza.JWT.controllers.dtos.LoginDto;
import com.eloiza.JWT.controllers.dtos.RefreshTokenRequestDto;
import com.eloiza.JWT.services.AuthService;
import com.eloiza.JWT.services.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto) {
        AuthResponseDto authResponseDto = authService.login(loginDto);
        showToken(authResponseDto);
        return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
    }


    @PostMapping("/refreshToken")
    public ResponseEntity<AuthResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDTO) {
        AuthResponseDto authResponseDto = authService.refreshAccessToken(refreshTokenRequestDTO);
        showToken(authResponseDto);
        return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
    }

    private void showToken(AuthResponseDto authResponseDto) {
        System.out.println("Token gerado: " + authResponseDto.getAccessToken());
        System.out.println("Refresh token: " + authResponseDto.getToken());
    }
}

