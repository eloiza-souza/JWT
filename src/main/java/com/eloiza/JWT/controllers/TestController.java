package com.eloiza.JWT.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public Map<String, Date> returnDate(){
        return Map.of("dateNow", new Date());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/cadastrar")
    public Map<String, String> adminRoute(){
        return Map.of("message", "Bem-vindo, Admin!");
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/comprar")
    public Map<String, String> userRoute(){
        return Map.of("message", "Bem-vindo, User!");
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/")
    public Map<String, String> managerRoute(){
        return Map.of("message", "Bem-vindo, Manager!");
    }

}

