package com.ssafy.yogiattacku.openai.dto.response;

public enum RecommendEventType {
    SPOTS, DESCRIPTION, DONE;

    public String eventName() {
        return name().toLowerCase();
    }
}
