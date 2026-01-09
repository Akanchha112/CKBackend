package com.example.cloudBalance.cloudBalance.DTO;

import com.example.cloudBalance.cloudBalance.model.Account;
import com.example.cloudBalance.cloudBalance.model.RoleType;
import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String emailId,
        RoleType role,
        LocalDateTime lastLogin,
        LocalDateTime createdAt,
        List<AccountResponse> accounts
) {}
