package com.example.cloudBalance.cloudBalance.controller;

import com.example.cloudBalance.cloudBalance.model.User;
import com.example.cloudBalance.cloudBalance.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
//@RequestMapping("/api/users")
public class UserController {
    @Autowired
    public final UserService userService;

    @PostMapping("/adduser")
    public ResponseEntity<Object> addUser(@RequestBody User user){
        return userService.addUser(user);
    }

    @GetMapping("/users")
    public ResponseEntity<Object> getAllUser(){
        return userService.getAllUser();
    }

    @PutMapping("/editUser/{id}")
    public ResponseEntity<Object> editUser(@PathVariable Long id,@RequestBody User user){
        return userService.editUser(id,user);
    }
}
