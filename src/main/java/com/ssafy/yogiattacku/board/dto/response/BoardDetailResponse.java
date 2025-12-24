package com.ssafy.yogiattacku.board.dto.response;

import com.ssafy.yogiattacku.attraction.dto.response.CategoryResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record BoardDetailResponse(
        Long boardId,
        Long userId,
        String nickname,
        String profileImageUrl,
        String title,
        String content,
        long viewCount,
        boolean isAuthor,
        UUID bucketRootKey,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<CategoryResponse> categories,
        List<PictureResponse> pictures
) {
}
