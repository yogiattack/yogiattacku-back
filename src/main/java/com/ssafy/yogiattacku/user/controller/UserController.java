package com.ssafy.yogiattacku.user.controller;

import com.ssafy.yogiattacku.global.response.ResponseBody;
import com.ssafy.yogiattacku.user.dto.response.UserResponse;
import com.ssafy.yogiattacku.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping()
    public ResponseBody<UserResponse> getUser(@AuthenticationPrincipal Long userId) {
        return ResponseBody.success(userService.getUserInfo(userId));
    }
}
