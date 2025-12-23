package com.ssafy.yogiattacku.attraction.dto.response;

import lombok.Builder;

@Builder
public record CategoryResponse(Long categoryId, String name) {
}
