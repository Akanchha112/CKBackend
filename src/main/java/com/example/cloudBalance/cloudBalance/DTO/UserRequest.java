package com.example.cloudBalance.cloudBalance.DTO;

import com.example.cloudBalance.cloudBalance.entity.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UserRequest(

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @Email
        @NotBlank
        String emailId,

        @NotBlank
        String password,

        RoleType role,

        List<Long> accountIds
) {}