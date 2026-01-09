package com.example.cloudBalance.cloudBalance.controller;

import com.example.cloudBalance.cloudBalance.DTO.UserRequest;
import com.example.cloudBalance.cloudBalance.DTO.ApiResponse;
import com.example.cloudBalance.cloudBalance.DTO.UpdateUserRequest;
import com.example.cloudBalance.cloudBalance.DTO.UserResponse;
import com.example.cloudBalance.cloudBalance.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
@RequestMapping("/api/users")
public class UserController {

    public final UserService userService;

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
        return userService.addUser(user);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'READONLY')")
    @GetMapping
    public ApiResponse<?> getAllUser(){
        return userService.getAllUser();
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<?> editUser(@PathVariable Long id,@RequestBody @Valid UpdateUserRequest user){
        return userService.editUser(id,user);
    }
}
