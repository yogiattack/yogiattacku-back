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
    @Value("${spring.jwt.access-token-ttl}")
    private Duration ACCESS_TOKEN_TTL;

    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        String oldRefreshToken = cookieUtil.getRefreshTokenFromCookie(request);

        if (oldRefreshToken == null) {
            cookieUtil.clearAccessTokenCookie(response);
            cookieUtil.clearRefreshTokenCookie(response);
            throw new GlobalException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        try {
            Long userId = refreshTokenService.getUserIdByRefreshToken(oldRefreshToken);

            String newRefreshToken = refreshTokenService.rotate(oldRefreshToken);
            cookieUtil.addRefreshTokenCookie(response, newRefreshToken, (int) REFRESH_TOKEN_TTL.toSeconds());

            String newAccessToken = tokenProvider.generateAccessToken(userId);
            cookieUtil.addAccessTokenCookie(response, newAccessToken, (int) ACCESS_TOKEN_TTL.toSeconds());
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
        cookieUtil.clearAccessTokenCookie(response);
    }
}
