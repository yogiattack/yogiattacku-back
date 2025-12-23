package com.ssafy.yogiattacku.board.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RegistPictureResponse(UUID pictureId, String publicUrl) {
}
