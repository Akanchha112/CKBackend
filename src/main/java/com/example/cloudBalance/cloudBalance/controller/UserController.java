package com.example.cloudBalance.cloudBalance.controller;

import com.example.cloudBalance.cloudBalance.DTO.UserRequest;
import com.example.cloudBalance.cloudBalance.DTO.ApiResponse;
import com.example.cloudBalance.cloudBalance.DTO.UpdateUserRequest;
import com.example.cloudBalance.cloudBalance.DTO.UserResponse;
import com.example.cloudBalance.cloudBalance.exception.ApiException;
import com.example.cloudBalance.cloudBalance.exception.ErrorCode;
import com.example.cloudBalance.cloudBalance.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
@RequestMapping("/api/users")
public class UserController {

    public final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/test-error")
    public void testError() {
        throw new ApiException(
                "Forced error",
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_ARGUMENTS
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ApiResponse<?> getUserById(@PathVariable Long id){
        UserResponse res=userService.findUserById(id);
        return ApiResponse.success(
                "User fetched successfully",
                res,
                HttpStatus.OK.value()
        );
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<?> addUser(@RequestBody @Valid UserRequest user){
        UserResponse res=userService.addUser(user);
        return ApiResponse.success(
                "User created successfully",
                res,
                HttpStatus.CREATED.value()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'READONLY')")
    @GetMapping
    public ApiResponse<?> getAllUser(){
        List<UserResponse> users= userService.getAllUser();
        return ApiResponse.success(
                "Users fetched successfully",
                users,
                200
        );
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<?> editUser(@PathVariable Long id,@RequestBody @Valid UpdateUserRequest user){
        UserResponse res= userService.editUser(id,user);
        return ApiResponse.success(
                "User updated successfully",
                res,
                HttpStatus.OK.value()
        );
    }
}
