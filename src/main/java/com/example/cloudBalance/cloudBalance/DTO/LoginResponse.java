package com.example.cloudBalance.cloudBalance.DTO;

import com.example.cloudBalance.cloudBalance.entity.RoleType;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String firstName,
        String lastName,
        RoleType role
) {}

