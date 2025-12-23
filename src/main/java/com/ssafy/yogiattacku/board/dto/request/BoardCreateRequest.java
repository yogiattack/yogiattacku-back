package com.ssafy.yogiattacku.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record BoardCreateRequest(
        @NotNull UUID bucketRootKey,
        @NotBlank @Size(max = 255) String title,
        @NotBlank String content,
        @NotEmpty List<Long> categoryIds,
        List<String> s3Keys
) {
}
