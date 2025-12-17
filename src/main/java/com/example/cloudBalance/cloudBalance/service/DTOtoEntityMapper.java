package com.example.cloudBalance.cloudBalance.service;

import com.example.cloudBalance.cloudBalance.DTO.UpdateUserRequest;
import com.example.cloudBalance.cloudBalance.DTO.UserRequest;
import com.example.cloudBalance.cloudBalance.DTO.UserResponse;
import com.example.cloudBalance.cloudBalance.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DTOtoEntityMapper {
    @Autowired
    public PasswordEncoder passwordEncoder;

    public UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmailId(),
                user.getRole(),
                user.getLastLogin(),
                user.getCreatedAt()
        );
    }
    public void updateEntity(User user, UpdateUserRequest dto, PasswordEncoder encoder) {

        if (dto.firstName() != null) {
            user.setFirstName(dto.firstName());
        }

        if (dto.lastName() != null) {
            user.setLastName(dto.lastName());
        }

        if (dto.emailId() != null) {
            user.setEmailId(dto.emailId());
        }

        if (dto.role() != null) {
            user.setRole(dto.role());
        }

        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPassword(encoder.encode(dto.password()));
        }
    }

    public User mapToEntity(UserRequest dto) {
        return User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .emailId(dto.emailId())
                .password(passwordEncoder.encode(dto.password()))
                .role(dto.role())
                .build();
    }
}
