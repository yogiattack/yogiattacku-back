package com.ssafy.yogiattacku.s3.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class S3Storage {
    private final S3Client client;
    private final S3Presigner presigner;

    public String presignPut(String bucket, String key, String contentType, Duration ttl, Long contentLengthLimit) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PresignedPutObjectRequest presigned = presigner.presignPutObject(p -> p
                .signatureDuration(ttl)
                .putObjectRequest(request));
        return presigned.url().toString();
    }

    public boolean exists(String bucket, String key) {
        try {
            client.headObject(HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
