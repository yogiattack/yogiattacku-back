package com.ssafy.yogiattacku.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    private static final String ACCESS_TOKEN_COOKIE_NAME = "ACCESS_TOKEN";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";
    private static final String DOMAIN = "yogiattacku.n-e.kr";

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
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
//        cookie.setDomain(DOMAIN);
        cookie.setMaxAge(maxAgeSeconds);
        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);
    }

    private void clearTokenFromCookie(String cookieName, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
//        cookie.setDomain(DOMAIN);
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);
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
