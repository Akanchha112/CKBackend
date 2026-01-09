package com.example.cloudBalance.cloudBalance.DTO;

public record AccountResponse(
        Long id,
        String accountId,
        String accountName,
        String arn
) {}
