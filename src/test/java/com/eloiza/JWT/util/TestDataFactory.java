package com.eloiza.JWT.util;

import com.eloiza.JWT.models.Department;
import com.eloiza.JWT.models.RefreshToken;
import com.eloiza.JWT.models.Role;
import com.eloiza.JWT.models.User;

import java.util.Set;

public class TestDataFactory {

    public static User createDefaultUser() {
        User user = new User();
        user.setName("Test User");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRoles(Set.of(new Role("ROLE_ADMIN"), new Role("ROLE_USER")));
        user.setDepartment(new Department("IT"));
        return user;
    }

    public static RefreshToken createRefreshToken(){
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");
        return refreshToken;
    }


}
