package com.eloiza.JWT.controllers.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterUserDto {
    private String name;
    private String username;
    private String email;
    private String password;
    private Set<String> roles;
    private String department;
}
