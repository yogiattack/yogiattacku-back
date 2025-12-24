package com.ssafy.yogiattacku.board.dto.response.page;

import com.ssafy.yogiattacku.attraction.dto.response.CategoryResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record BoardListItemResponse(
        Long boardId,
        Long userId,
        String nickname,
        String title,
        long viewCount,
        UUID bucketRootKey,
        String thumbnailS3Key,
        boolean isAuthor,
        List<CategoryResponse> categories,
        LocalDateTime createdAt
) {
}
