package com.ssafy.yogiattacku.board.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PictureResponse(
        UUID pictureId,
        String s3Key,
        String contentType
) {
}
