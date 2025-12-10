package com.ssafy.yogiattacku.attraction.dto.response;

public record SimilarAttraction(
        Long id,
        String sidoName,
        String gugunName,
        String contentTypeName,
        String attractionName,
        String address,
        double latitude,
        double longitude,
        double distancecontent
) {
}
