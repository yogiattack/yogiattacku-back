package com.ssafy.yogiattacku.board.dto.response.page;

import lombok.Builder;

import java.util.List;

@Builder
public record BoardPageResponse(
        List<BoardListItemResponse> items,
        PageMeta page
) {
}
