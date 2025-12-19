package com.ssafy.yogiattacku.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    private static final String ACCESS_TOKEN_COOKIE_NAME = "ACCESS_TOKEN";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";
    private static final String DOMAIN = ".yogiattacku.n-e.kr";

    public void addAccessTokenCookie(HttpServletResponse response, String value, int maxAgeSeconds) {
        addTokenCookie(ACCESS_TOKEN_COOKIE_NAME, value, maxAgeSeconds, response);
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String value, int maxAgeSeconds) {
        addTokenCookie(REFRESH_TOKEN_COOKIE_NAME, value, maxAgeSeconds, response);
    }


    public void clearAccessTokenCookie(HttpServletResponse response) {
        clearTokenFromCookie(ACCESS_TOKEN_COOKIE_NAME, response);
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        clearTokenFromCookie(REFRESH_TOKEN_COOKIE_NAME, response);
    }

    public String getAccessTokenFromCookie(HttpServletRequest request) {
        return getTokenFromCookie(ACCESS_TOKEN_COOKIE_NAME, request);
    }

    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        return getTokenFromCookie(REFRESH_TOKEN_COOKIE_NAME, request);
    }

    private void addTokenCookie(String cookieName, String value, int maxAgeSeconds, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, value)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .domain(DOMAIN)
                .maxAge(maxAgeSeconds)
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearTokenFromCookie(String cookieName, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(cookieName, "")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .domain(DOMAIN)
                .maxAge(0)
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }


    private String getTokenFromCookie(String cookieName, HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
            return null;
    }
}
