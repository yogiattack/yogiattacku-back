package com.ssafy.yogiattacku.s3.dto.request;

import java.util.UUID;

public record PresignPutRequest(
        UUID bucketRootKey,
        String contentType,
        String fileExt,
        Long fileSize) {
}
