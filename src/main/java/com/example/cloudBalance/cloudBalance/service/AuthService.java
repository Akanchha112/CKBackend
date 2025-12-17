package com.example.cloudBalance.cloudBalance.service;

import com.example.cloudBalance.cloudBalance.DTO.ApiResponse;
import com.example.cloudBalance.cloudBalance.DTO.LoginRequest;
import com.example.cloudBalance.cloudBalance.exception.ApiException;
import com.example.cloudBalance.cloudBalance.exception.ErrorCode;
import com.example.cloudBalance.cloudBalance.model.User;
import com.example.cloudBalance.cloudBalance.security.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final AuthUtils authUtils;

    public ApiResponse<?> login(LoginRequest req) {
        String token;
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.emailId(), req.password()
                    )
            );

            UserDetails userDetails =
                    (UserDetails) authentication.getPrincipal();

            System.out.println("userDetails.getAuthorities(); " + userDetails.getAuthorities());
            token = authUtils.generateAcessToken(userDetails);
        }catch (Exception e){
            throw new ApiException(
                    "Invalid email or password",
                    HttpStatus.UNAUTHORIZED,
                    ErrorCode.INVALID_CREDENTIALS
            );
        }

        return ApiResponse.success(
                "Login successful",
                token,
                200
        );
    }
}
