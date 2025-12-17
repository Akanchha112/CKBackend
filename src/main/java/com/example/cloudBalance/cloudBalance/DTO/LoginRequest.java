package com.example.cloudBalance.cloudBalance.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;


public record LoginRequest(

        @Email
        @NotBlank
        String emailId,

        @NotBlank
        String password
) {}
