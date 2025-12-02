package com.ssafy.yogiattacku.security.controller;

import com.ssafy.yogiattacku.global.response.ResponseBody;
import com.ssafy.yogiattacku.security.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/refresh")
    public ResponseBody<Void> refresh(HttpServletRequest request, HttpServletResponse response) {
        authService.refresh(request, response);
        return ResponseBody.success(null);
    }

    @PostMapping("/logout")
    public ResponseBody<Void> logout(@AuthenticationPrincipal Long userId, HttpServletRequest request, HttpServletResponse response) {
        authService.logout(userId, response);
        return ResponseBody.success(null);
    }
}
