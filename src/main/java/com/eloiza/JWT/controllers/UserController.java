package com.eloiza.JWT.controllers;

import com.eloiza.JWT.controllers.dtos.AuthResponseDto;
import com.eloiza.JWT.controllers.dtos.AuthUserDto;
import com.eloiza.JWT.controllers.dtos.RegisterUserDto;
import com.eloiza.JWT.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public void registerUser(@RequestBody RegisterUserDto registerUserDto){
        userService.registerUser(registerUserDto);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<AuthUserDto> getUser() {
        return new ResponseEntity<>(userService.getAuthenticatedUser(), HttpStatus.OK);
    }
}

