package com.ssafy.yogiattacku.s3.dto.response;

import lombok.Builder;

@Builder
public record PresignPutResponse(String uploadUrl, String s3Key, String publicUrl) {
}
