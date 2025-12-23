package com.ssafy.yogiattacku.s3.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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

    public void deleteAllOrThrow(String bucket, List<String> keys) {
        if (keys == null || keys.isEmpty()) return;

        List<String> cleaned = keys.stream()
                .filter(k -> k != null && !k.isBlank())
                .distinct()
                .toList();
        if (cleaned.isEmpty()) return;

        final int BATCH_SIZE = 1000;

        for (int i = 0; i < cleaned.size(); i += BATCH_SIZE) {
            List<String> chunk = cleaned.subList(i, Math.min(i + BATCH_SIZE, cleaned.size()));

            List<ObjectIdentifier> objects = new ArrayList<>(chunk.size());
            for (String key : chunk) {
                objects.add(ObjectIdentifier.builder().key(key).build());
            }

            DeleteObjectsResponse res = client.deleteObjects(DeleteObjectsRequest.builder()
                    .bucket(bucket)
                    .delete(Delete.builder().objects(objects).quiet(true).build())
                    .build());

            if (res.hasErrors() && !res.errors().isEmpty()) {
                throw S3Exception.builder()
                        .message("S3 deleteObjects partial errors: " + res.errors())
                        .build();
            }
        }
    }
}
