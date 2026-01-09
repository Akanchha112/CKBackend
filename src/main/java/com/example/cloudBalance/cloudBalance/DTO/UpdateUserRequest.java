package com.example.cloudBalance.cloudBalance.DTO;


import com.example.cloudBalance.cloudBalance.model.RoleType;

import java.util.List;

public record UpdateUserRequest(
        String firstName,
        String lastName,
        String emailId,
        String password,
        RoleType role,
        List<Long> accountIds
) {}
