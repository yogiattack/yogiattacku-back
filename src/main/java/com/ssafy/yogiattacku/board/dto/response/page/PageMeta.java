package com.ssafy.yogiattacku.board.dto.response.page;

import lombok.Builder;

@Builder
public record PageMeta(long page, long pageSize, boolean hasNext) {
}
