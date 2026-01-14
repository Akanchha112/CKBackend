package com.example.cloudBalance.cloudBalance.repository;

import com.example.cloudBalance.cloudBalance.entity.RefreshToken;
import com.example.cloudBalance.cloudBalance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

//    @Modifying
//    @Query("DELETE FROM RefreshToken r WHERE r.user.id =:userId")
//    void deleteByUserId(@Param("userId") Long userId );

    @Modifying
    void deleteByUser(User user);

    void deleteByToken(String refreshToken);
    Optional<RefreshToken> findByToken(String refreshTokenValue);

    Optional<RefreshToken> findByUser(User user);
}
