package com.eloiza.JWT.controllers.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@AllArgsConstructor
@Data
public class AuthUserDto {
    private String message;
    private String department;
}
