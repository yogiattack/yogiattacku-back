package com.ssafy.yogiattacku.security.service;

import com.ssafy.yogiattacku.global.exception.ErrorCode;
import com.ssafy.yogiattacku.global.exception.GlobalException;
import com.ssafy.yogiattacku.security.jwt.TokenProvider;
import com.ssafy.yogiattacku.security.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CookieUtil cookieUtil;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    private static final Duration ACCESS_TOKEN_TTL = Duration.ofMinutes(30);
    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);

    public void refresh(HttpServletRequest request, HttpServletResponse response) {
        String oldRefreshToken = cookieUtil.getRefreshTokenFromCookie(request);

        if (oldRefreshToken == null) {
            cookieUtil.clearRefreshTokenCookie(response);
            throw new GlobalException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        String accessToken = cookieUtil.getAccessTokenFromCookie(request);
        Long userId = getUserIdFromExpiredAccessToken(accessToken);

        try {
            String newRefreshToken = refreshTokenService.rotate(userId, oldRefreshToken);
            cookieUtil.addRefreshTokenCookie(response, newRefreshToken, (int) ACCESS_TOKEN_TTL.toSeconds());

            String newAccessToken = tokenProvider.generateAccessToken(userId);
            cookieUtil.addAccessTokenCookie(response, newAccessToken, (int) REFRESH_TOKEN_TTL.toSeconds());
        } catch (GlobalException e) {
            refreshTokenService.delete(userId);
            cookieUtil.clearRefreshTokenCookie(response);
            throw e;
        }
    }

    public void logout(Long userId, HttpServletResponse response) {
        if (userId == null) {
            throw new GlobalException(ErrorCode.USER_NOT_AUTHENTICATED);
        }
        refreshTokenService.delete(userId);
        cookieUtil.clearRefreshTokenCookie(response);
        cookieUtil.clearAccessTokenCookie(response);
    }

    private Long getUserIdFromExpiredAccessToken(String accessToken) {
        try {
            return tokenProvider.getUserIdAllowExpire(accessToken);
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.ACCESS_TOKEN_NOT_FOUND);
        }
    }
}
