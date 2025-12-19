package com.example.cloudBalance.cloudBalance.DTO;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}

