package com.ssafy.yogiattacku.openai.dto.response;

import com.ssafy.yogiattacku.attraction.dto.response.Spot;
import lombok.Builder;

import java.util.List;

@Builder
public record RecommendStreamEvent(
        RecommendEventType type,
        List<Spot> spots,
        String descriptionChunk
) {
}
