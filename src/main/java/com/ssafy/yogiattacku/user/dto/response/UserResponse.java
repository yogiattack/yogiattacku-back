package com.ssafy.yogiattacku.user.dto.response;

import lombok.Builder;

@Builder
public record UserResponse(String email, String nickname) {
}
