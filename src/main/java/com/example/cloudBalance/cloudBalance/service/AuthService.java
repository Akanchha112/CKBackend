package com.example.cloudBalance.cloudBalance.service;

import com.example.cloudBalance.cloudBalance.DTO.ApiResponse;
import com.example.cloudBalance.cloudBalance.DTO.LoginRequest;
import com.example.cloudBalance.cloudBalance.DTO.LoginResponse;
import com.example.cloudBalance.cloudBalance.exception.ApiException;
import com.example.cloudBalance.cloudBalance.exception.ErrorCode;
import com.example.cloudBalance.cloudBalance.model.RefreshToken;
import com.example.cloudBalance.cloudBalance.model.User;
import com.example.cloudBalance.cloudBalance.repository.UserRepository;
import com.example.cloudBalance.cloudBalance.security.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    public UserDetails authenticate(LoginRequest req){
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.emailId(), req.password()
                    )
            );
            return (UserDetails) authentication.getPrincipal();
        }catch (Exception e){
            throw new ApiException(
                    "Invalid email or password",
                    HttpStatus.UNAUTHORIZED,
                    ErrorCode.INVALID_CREDENTIALS
            );
        }
    }

    public ApiResponse<?> login(LoginRequest req) {
        UserDetails userDetails=authenticate(req);

        User user = userRepository.findByEmailId(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = authUtils.generateAcessToken(user);

        RefreshToken refreshToken = refreshTokenService.create(user);

        return ApiResponse.success(
                "Login successful",
                new LoginResponse(accessToken,refreshToken.getToken()),
                200
        );
    }


}
