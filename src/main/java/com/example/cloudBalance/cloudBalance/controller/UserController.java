package com.example.cloudBalance.cloudBalance.controller;

import com.example.cloudBalance.cloudBalance.DTO.UserRequest;
import com.example.cloudBalance.cloudBalance.DTO.ApiResponse;
import com.example.cloudBalance.cloudBalance.DTO.UpdateUserRequest;
import com.example.cloudBalance.cloudBalance.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
//@RequestMapping("/api/users")
public class UserController {
    @Autowired
    public final UserService userService;

    @PostMapping("/adduser")
    public ApiResponse<?> addUser(@RequestBody @Valid UserRequest user){
        return userService.addUser(user);
    }

    @GetMapping("/users")
    public ApiResponse<?> getAllUser(){
        return userService.getAllUser();
    }

    @PutMapping("/editUser/{id}")
    public ApiResponse<?> editUser(@PathVariable Long id,@RequestBody @Valid UpdateUserRequest user){
        return userService.editUser(id,user);
    }
}
