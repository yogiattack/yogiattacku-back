package com.ssafy.yogiattacku.security.service;

import com.ssafy.yogiattacku.global.exception.ErrorCode;
import com.ssafy.yogiattacku.global.exception.GlobalException;
import com.ssafy.yogiattacku.security.jwt.TokenProvider;
import com.ssafy.yogiattacku.security.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CookieUtil cookieUtil;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Value("${spring.jwt.refresh-token-ttl}")
    private Duration REFRESH_TOKEN_TTL;
    private final String BEARER = "Bearer ";

    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        String oldRefreshToken = cookieUtil.getRefreshTokenFromCookie(request);

        if (oldRefreshToken == null) {
            cookieUtil.clearRefreshTokenCookie(response);
            throw new GlobalException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        try {
            Long userId = refreshTokenService.getUserIdByRefreshToken(oldRefreshToken);

            String newRefreshToken = refreshTokenService.rotate(oldRefreshToken);
            cookieUtil.addRefreshTokenCookie(response, newRefreshToken, (int) REFRESH_TOKEN_TTL.toSeconds());

            String newAccessToken = tokenProvider.generateAccessToken(userId);
            response.addHeader("Authorization", BEARER + newAccessToken);
        } catch (GlobalException e) {
            cookieUtil.clearRefreshTokenCookie(response);
            throw e;
        }
    }

    public void logout(Long userId, HttpServletRequest request, HttpServletResponse response) {
        if (userId == null) {
            throw new GlobalException(ErrorCode.USER_NOT_AUTHENTICATED);
        }

        String refreshToken = cookieUtil.getRefreshTokenFromCookie(request);

        if (refreshToken != null) {
            refreshTokenService.delete(refreshToken);
        }
        cookieUtil.clearRefreshTokenCookie(response);
    }
}
