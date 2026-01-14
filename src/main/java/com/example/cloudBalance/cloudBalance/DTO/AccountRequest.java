package com.example.cloudBalance.cloudBalance.DTO;

import jakarta.validation.constraints.NotBlank;

public record AccountRequest(
        @NotBlank String accountId,
        @NotBlank String accountName,
        @NotBlank String arn
) {}
