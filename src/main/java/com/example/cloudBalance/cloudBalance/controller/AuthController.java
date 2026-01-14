package com.example.cloudBalance.cloudBalance.controller;

import com.example.cloudBalance.cloudBalance.DTO.ApiResponse;
import com.example.cloudBalance.cloudBalance.DTO.LoginRequest;
import com.example.cloudBalance.cloudBalance.DTO.LoginResponse;
import com.example.cloudBalance.cloudBalance.DTO.RefreshRequest;
import com.example.cloudBalance.cloudBalance.exception.ErrorCode;
import com.example.cloudBalance.cloudBalance.security.AuthUtils;
import com.example.cloudBalance.cloudBalance.service.AuthService;
import com.example.cloudBalance.cloudBalance.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    public final AuthService authService;
    public final RefreshTokenService refreshTokenService;
    public final AuthUtils authUtils;

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody @Valid LoginRequest req, HttpServletResponse response) {
        LoginResponse loginResponse= authService.login(req);

        Cookie refreshCookie = new Cookie("refreshToken", loginResponse.refreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true); // false only for local http
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(refreshCookie);

        // Remove refresh token from body
        LoginResponse sanitized = new LoginResponse(
                loginResponse.accessToken(),
                null,
                loginResponse.firstName(),
                loginResponse.lastName(),
                loginResponse.role()
        );
        return ApiResponse.success("User Logged In Successfully", sanitized,200);
    }

    @PostMapping("/refresh")
    public ApiResponse<?> refresh(HttpServletRequest request) {

        String refreshToken = authUtils.getRefreshToken(request);

        if (refreshToken == null) {
            return ApiResponse.error(
                    "Refresh token not found",
                    ErrorCode.REFRESH_TOKEN_MISSING,
                    401
            );
        }
        return refreshTokenService.refresh(refreshToken);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'READONLY', 'CUSTOMER')")
    @PostMapping("/logout")
    public ApiResponse<?> logout(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = authUtils.getRefreshToken(request);

        if (refreshToken != null) {
            refreshTokenService.deleteRefreshToken(refreshToken);
        }

        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // false only for local http
        cookie.setPath("/");
        cookie.setMaxAge(0); // delete cookie
        response.addCookie(cookie);

        return ApiResponse.success("Logged out successfully", null, 200);
    }


}
