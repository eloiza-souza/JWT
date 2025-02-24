package com.eloiza.JWT.controllers.dtos;

import com.eloiza.JWT.models.Department;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterUserDto {
    private String name;
    private String username;
    private String email;
    private String password;
    private Set<Roles> roles;
    private Department department;
}
