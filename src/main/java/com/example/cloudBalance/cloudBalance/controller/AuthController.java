package com.example.cloudBalance.cloudBalance.controller;

import com.example.cloudBalance.cloudBalance.DTO.ApiResponse;
import com.example.cloudBalance.cloudBalance.DTO.LoginRequest;
import com.example.cloudBalance.cloudBalance.model.User;
import com.example.cloudBalance.cloudBalance.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {


    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody @Valid LoginRequest req) {
        return authService.login(req);
    }
}
