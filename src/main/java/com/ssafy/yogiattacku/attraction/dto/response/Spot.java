package com.ssafy.yogiattacku.attraction.dto.response;

public record Spot(
        Long id,
        String name,
        String sidoName,
        String gugunName,
        double latitude,
        double longitude
) {
}
