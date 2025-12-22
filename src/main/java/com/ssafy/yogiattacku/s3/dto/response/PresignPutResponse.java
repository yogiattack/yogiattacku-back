package com.ssafy.yogiattacku.s3.dto.response;

import java.util.UUID;

public record PresignPutResponse(String uploadUrl, String s3Key, String publicUrl) {
}
