package com.ssafy.yogiattacku.s3.dto.request;

public record PresignPutRequest(String contentType, String fileExt, Long fileSize) {
}
