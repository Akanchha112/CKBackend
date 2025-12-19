package com.example.cloudBalance.cloudBalance.repository;

import com.example.cloudBalance.cloudBalance.model.RefreshToken;
import com.example.cloudBalance.cloudBalance.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {



    @Query("DELETE FROM RefreshToken r WHERE r.user.id =:userId")
    void deleteByUserId(@Param("userId") Long userId );

    @Modifying
    void deleteByUser(User user);

    void deleteByToken(String refreshToken);
    Optional<RefreshToken> findByToken(String refreshTokenValue);

    Optional<RefreshToken> findByUser(User user);
}
