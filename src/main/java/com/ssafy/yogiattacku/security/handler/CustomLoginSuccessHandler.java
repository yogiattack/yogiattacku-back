package com.ssafy.yogiattacku.security.handler;

import com.ssafy.yogiattacku.security.jwt.TokenProvider;
import com.ssafy.yogiattacku.security.oauth2.CustomOAuth2User;
import com.ssafy.yogiattacku.security.service.RefreshTokenService;
import com.ssafy.yogiattacku.security.util.CookieUtil;
import com.ssafy.yogiattacku.user.entity.User;
import com.ssafy.yogiattacku.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final UserService userService;
    private final CookieUtil cookieUtil;
    private final RefreshTokenService refreshTokenService;
    private final TokenProvider tokenProvider;

    @Value("${spring.jwt.refresh-token-ttl}")
    private Duration REFRESH_TOKEN_TTL;

    @Value("${spring.jwt.access-token-ttl}")
    private Duration ACCESS_TOKEN_TTL;

    @Value("${direct.home}")
    private String REDIRECTION_HOME;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("CustomLoginSuccessHandler onAuthenticationSuccess");
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        User user = userService.upsertFromKakao(
                customOAuth2User.getSocialId(),
                customOAuth2User.getEmail(),
                customOAuth2User.getNickname(),
                customOAuth2User.getProfileImageUrl()
        );

        log.info("user email: {}", user.getEmail());
        String refreshToken = refreshTokenService.issue(user.getId());
        String accessToken = tokenProvider.generateAccessToken(user.getId());

        log.info("access token: {}", accessToken);
        log.info("refresh token: {}", refreshToken);

        cookieUtil.addAccessTokenCookie(response, accessToken, (int) ACCESS_TOKEN_TTL.toSeconds());
        cookieUtil.addRefreshTokenCookie(response, refreshToken, (int) REFRESH_TOKEN_TTL.toSeconds());
        response.sendRedirect(REDIRECTION_HOME);
    }
}
