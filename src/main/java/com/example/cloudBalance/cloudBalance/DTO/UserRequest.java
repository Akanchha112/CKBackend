package com.example.cloudBalance.cloudBalance.DTO;

import com.example.cloudBalance.cloudBalance.model.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

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

        RoleType role
) {}