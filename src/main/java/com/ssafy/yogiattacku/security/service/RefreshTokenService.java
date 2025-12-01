package com.ssafy.yogiattacku.security.service;

import com.ssafy.yogiattacku.global.exception.ErrorCode;
import com.ssafy.yogiattacku.global.exception.GlobalException;
import com.ssafy.yogiattacku.security.util.RefreshTokenUtil;
import lombok.RequiredArgsConstructor;
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
    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);
    private static final String ALGORITHM_NAME = "SHA-256";

    public String issue(Long userId) {
        String token = UUID.randomUUID().toString();
        String refreshToken = sha256(token);
        refreshTokenUtil.save(userId, refreshToken, REFRESH_TOKEN_TTL);
        return token;
    }

    public String rotate(Long userId, String refreshToken) {
        String storedRefreshToken = refreshTokenUtil.findHash(userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.REFRESH_TOKEN_INVALID));

        String requestRefreshToken = sha256(refreshToken);
        if (!requestRefreshToken.equals(storedRefreshToken)) {
            throw new GlobalException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
        return issue(userId);
    }

    public void delete(Long userId) {
        refreshTokenUtil.delete(userId);
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
