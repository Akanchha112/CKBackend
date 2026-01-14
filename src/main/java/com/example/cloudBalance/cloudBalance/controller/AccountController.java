package com.example.cloudBalance.cloudBalance.controller;

import com.example.cloudBalance.cloudBalance.DTO.AccountRequest;
import com.example.cloudBalance.cloudBalance.DTO.AccountResponse;
import com.example.cloudBalance.cloudBalance.DTO.ApiResponse;
import com.example.cloudBalance.cloudBalance.entity.Account;
import com.example.cloudBalance.cloudBalance.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@EnableMethodSecurity(prePostEnabled = true)
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;


    @PreAuthorize("hasAnyRole('ADMIN', 'READONLY', 'CUSTOMER')")
    @GetMapping("/all")
    public ApiResponse<?> getAllAccounts() {
        List<AccountResponse> accounts = accountService.getAllAccounts();
        return ApiResponse.success(
                "User fetched successfully",
                accounts,
                HttpStatus.OK.value()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'READONLY', 'CUSTOMER')")
    @GetMapping
    public ApiResponse<List<AccountResponse>> getAccounts(Authentication authentication) {
        String userEmail = authentication.getName();
        List<AccountResponse> accounts = accountService.getAccountsForUser(userEmail);
        return ApiResponse.success("Accounts fetched successfully", accounts, 200);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'READONLY', 'CUSTOMER')")
    @GetMapping("/{id}")
    public ApiResponse<?> getAccountById(@PathVariable Long id) {
        AccountResponse res= accountService.getAccountById(id);
        return ApiResponse.success(
                "Account fetched Successfully",
                res,
                200
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<AccountResponse> createAccount(@RequestBody @Valid AccountRequest account) {
        AccountResponse createdAccount = accountService.createAccount(account);
        return ApiResponse.success("Account Created Successfully",createdAccount,HttpStatus.CREATED.value());
    }

}
