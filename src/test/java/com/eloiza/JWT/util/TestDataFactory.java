package com.eloiza.JWT.util;

import com.eloiza.JWT.controllers.dtos.RefreshTokenRequestDto;
import com.eloiza.JWT.controllers.dtos.RegisterUserDto;
import com.eloiza.JWT.models.Department;
import com.eloiza.JWT.models.RefreshToken;
import com.eloiza.JWT.models.Role;
import com.eloiza.JWT.models.User;

import java.util.Set;

public class TestDataFactory {

    public static final String TEST_USER = "testuser";
    public static final String REFRESH_TOKEN = "refresh-token";

    public static User createDefaultUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername(TEST_USER);
        user.setPassword("password");
        user.setRoles(Set.of(new Role("ROLE_ADMIN"), new Role("ROLE_USER")));
        user.setDepartment(new Department("IT"));
        return user;
    }

    public static RefreshToken createDefaultRefreshToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(REFRESH_TOKEN);
        refreshToken.setUser(createDefaultUser());
        return refreshToken;

    }

    public static RefreshTokenRequestDto createDefaultRefreshTokenRequestDto() {
        RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto();
        refreshTokenRequestDto.setToken(REFRESH_TOKEN);
        return refreshTokenRequestDto;
    }

    public static RegisterUserDto createDefaultRegisterUserDto() {
        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setName("Test User");
        registerUserDto.setUsername(TEST_USER);
        registerUserDto.setEmail("testuser@example.com");
        registerUserDto.setPassword("password");
        registerUserDto.setRoles(Set.of("ROLE_USER"));
        registerUserDto.setDepartment("IT");
        return registerUserDto;
    }

}
