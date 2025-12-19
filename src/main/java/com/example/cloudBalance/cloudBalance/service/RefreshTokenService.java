package com.example.cloudBalance.cloudBalance.service;

import com.example.cloudBalance.cloudBalance.DTO.ApiResponse;
import com.example.cloudBalance.cloudBalance.DTO.LoginResponse;
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final AuthUtils authUtils;

    @Transactional
    public RefreshToken create(User user) {
       // repo.deleteByUser(user);
       Optional<RefreshToken> existing = repo.findByUser(user);
        existing.ifPresent(repo::delete);
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        token.setLastActivityAt(Instant.now());

        return repo.save(token);
    }

    public ApiResponse<?> refresh(String refreshTokenValue) {

        RefreshToken token = repo.findByToken(refreshTokenValue)
                .orElseThrow(() -> new ApiException(
                        "Invalid session",
                        HttpStatus.UNAUTHORIZED,
                        ErrorCode.INVALID_SESSION));

        // Check absolute expiry (30 days)
        if (token.getExpiresAt().isBefore(Instant.now())) {
            repo.delete(token);
            throw new ApiException(
                    "Session expired",
                    HttpStatus.UNAUTHORIZED,
                    ErrorCode.SESSION_EXPIRED);
        }

        if (token.getLastActivityAt()
                .isBefore(Instant.now().minus(15, ChronoUnit.MINUTES))) {

            repo.delete(token);
            throw new ApiException(
                    "Session Expired",
                    HttpStatus.UNAUTHORIZED,
                    ErrorCode.SESSION_EXPIRED);
        }

        token.setLastActivityAt(Instant.now());

        String newAccessToken =
                authUtils.generateAcessToken(token.getUser());

        return ApiResponse.success(
                "Login successful",
                new LoginResponse(newAccessToken, refreshTokenValue),
                200
        );
    }

    public void logout(String refreshToken) {
        repo.deleteByToken(refreshToken);
    }


    public RefreshToken validateAndUpdateActivity(String tokenValue) {

        RefreshToken token = repo.findByToken(tokenValue)
                .orElseThrow(() -> new ApiException(
                        "Invalid session",
                        HttpStatus.UNAUTHORIZED,
                        ErrorCode.INVALID_SESSION));

        // Check absolute expiry (30 days)
        if (token.getExpiresAt().isBefore(Instant.now())) {
            repo.delete(token);
            throw new ApiException(
                    "Session expired",
                    HttpStatus.UNAUTHORIZED,
                    ErrorCode.SESSION_EXPIRED);
        }

        // Inactivity check (15 minutes)
        if (token.getLastActivityAt()
                .isBefore(Instant.now().minus(15, ChronoUnit.MINUTES))) {

            repo.delete(token);
            throw new ApiException(
                    "Session expired due to inactivity",
                    HttpStatus.UNAUTHORIZED,
                    ErrorCode.SESSION_EXPIRED);
        }

        // Throttled DB update (every 5 minutes max)
        if (token.getLastActivityAt()
                .isBefore(Instant.now().minus(2, ChronoUnit.MINUTES))) {

            token.setLastActivityAt(Instant.now());
            repo.save(token);
        }

        return token;
    }




}

