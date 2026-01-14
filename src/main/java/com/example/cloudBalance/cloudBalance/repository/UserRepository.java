package com.example.cloudBalance.cloudBalance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.cloudBalance.cloudBalance.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmailId(String emailId);
}
