package com.ssafy.yogiattacku.security.service;

import com.ssafy.yogiattacku.global.exception.ErrorCode;
import com.ssafy.yogiattacku.global.exception.GlobalException;
import com.ssafy.yogiattacku.security.util.RefreshTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenUtil refreshTokenUtil;
    @Value("${spring.jwt.refresh-token-ttl}")
    private Duration REFRESH_TOKEN_TTL;
    private static final String ALGORITHM_NAME = "SHA-256";

    public String issue(Long userId) {
        String token = UUID.randomUUID().toString();
        String refreshToken = sha256(token);
        refreshTokenUtil.save(userId, refreshToken, REFRESH_TOKEN_TTL);
        return token;
    }

    public Long getUserIdByRefreshToken(String rawRefreshToken) {
        String tokenHash = sha256(rawRefreshToken);
        return refreshTokenUtil.findUserIdByTokenHash(tokenHash)
                .orElseThrow(() -> new GlobalException(ErrorCode.REFRESH_TOKEN_INVALID));
    }

    public String rotate(String oldRawRefreshToken) {
        String oldHash = sha256(oldRawRefreshToken);

        Long userId = refreshTokenUtil.findUserIdByTokenHash(oldHash)
                .orElseThrow(() -> new GlobalException(ErrorCode.REFRESH_TOKEN_INVALID));

        refreshTokenUtil.delete(oldHash);

        String newRawToken = UUID.randomUUID().toString();
        String newHash = sha256(newRawToken);
        refreshTokenUtil.save(userId, newHash, REFRESH_TOKEN_TTL);

        return newRawToken;
    }

    public void delete(String rawRefreshToken) {
        String hash = sha256(rawRefreshToken);
        refreshTokenUtil.delete(hash);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM_NAME);
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No such algorithm", e);
        }
    }
}
