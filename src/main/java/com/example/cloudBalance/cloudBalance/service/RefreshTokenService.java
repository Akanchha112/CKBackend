package com.example.cloudBalance.cloudBalance.service;

import com.example.cloudBalance.cloudBalance.DTO.ApiResponse;
import com.example.cloudBalance.cloudBalance.DTO.LoginResponse;
import com.example.cloudBalance.cloudBalance.DTO.RefreshTokenResponse;
import com.example.cloudBalance.cloudBalance.exception.ApiException;
import com.example.cloudBalance.cloudBalance.exception.ErrorCode;
import com.example.cloudBalance.cloudBalance.model.RefreshToken;
import com.example.cloudBalance.cloudBalance.model.User;
import com.example.cloudBalance.cloudBalance.repository.RefreshTokenRepository;
import com.example.cloudBalance.cloudBalance.security.AuthUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    String zoneId="Asia/Kolkata";
    private final RefreshTokenRepository repo;
    private final AuthUtils authUtils;



    @Transactional
    public RefreshToken create(User user) {

        Optional<RefreshToken> existing = repo.findByUser(user);

        RefreshToken token = existing.orElse(new RefreshToken());
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(LocalDateTime.now(ZoneId.of(zoneId)).plusDays(10));
        token.setLastActivityAt(LocalDateTime.now(ZoneId.of(zoneId)));

        return repo.save(token);
    }

    public ApiResponse<?> refresh(String refreshTokenValue) {

        RefreshToken token = getValidRefreshToken(refreshTokenValue);

        token.setLastActivityAt(LocalDateTime.now(ZoneId.of(zoneId)));
        repo.save(token);

        String newAccessToken =
                authUtils.generateAcessToken(token.getUser());

        return ApiResponse.success(
                "new token generated successfully successful",
                new RefreshTokenResponse(newAccessToken),
                200
        );
    }

    public void logout(String refreshToken) {
        repo.deleteByToken(refreshToken);
    }


    public void validateAndUpdateActivity(String tokenValue) {
        RefreshToken token = getValidRefreshToken(tokenValue);
//        System.out.println(token.getLastActivityAt());
        LocalDateTime now = LocalDateTime.now(ZoneId.of(zoneId));

        // Throttled DB update (every 2 minutes max)
        if (token.getLastActivityAt().isBefore(now.minusMinutes(2))) {
            token.setLastActivityAt(now);
            repo.save(token);
        }
    }


    private RefreshToken getValidRefreshToken(String tokenValue) {

        RefreshToken token = repo.findByToken(tokenValue)
                .orElseThrow(() -> new ApiException(
                        "Invalid session",
                        HttpStatus.UNAUTHORIZED,
                        ErrorCode.INVALID_SESSION));

        LocalDateTime now = LocalDateTime.now(ZoneId.of(zoneId));

        // Absolute expiry check
        if (token.getExpiresAt().isBefore(now)) {
            repo.delete(token);
            throw new ApiException(
                    "Session expired",
                    HttpStatus.UNAUTHORIZED,
                    ErrorCode.SESSION_EXPIRED);
        }

        // Inactivity expiry check
        if (token.getLastActivityAt().isBefore(now.minusMinutes(15))) {
            repo.delete(token);
            throw new ApiException(
                    "Session expired due to inactivity",
                    HttpStatus.UNAUTHORIZED,
                    ErrorCode.SESSION_EXPIRED);
        }

        return token;
    }



}

