package com.example.cloudBalance.cloudBalance.service;

import com.example.cloudBalance.cloudBalance.model.User;
import com.example.cloudBalance.cloudBalance.security.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final AuthUtils authUtils;

    public ResponseEntity<Object> login(User req) {
        Authentication authentication=authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmailId(), req.getPassword()
                )
        );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        System.out.println("userDetails.getAuthorities(); "+userDetails.getAuthorities());
        String token = authUtils.generateAcessToken(userDetails.getUsername());

        return ResponseEntity.accepted().body(token);
    }
}
