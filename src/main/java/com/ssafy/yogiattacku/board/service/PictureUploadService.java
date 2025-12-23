package com.ssafy.yogiattacku.board.service;

import com.ssafy.yogiattacku.global.exception.ErrorCode;
import com.ssafy.yogiattacku.global.exception.GlobalException;
import com.ssafy.yogiattacku.s3.config.AwsProps;
import com.ssafy.yogiattacku.s3.config.ImageProps;
import com.ssafy.yogiattacku.s3.dto.request.PresignPutRequest;
import com.ssafy.yogiattacku.s3.dto.response.PresignPutResponse;
import com.ssafy.yogiattacku.s3.storage.CdnUrlBuilder;
import com.ssafy.yogiattacku.s3.storage.S3KeyFactory;
import com.ssafy.yogiattacku.s3.storage.S3Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PictureUploadService {
    private final AwsProps awsProps;
    private final ImageProps imageProps;

    private final S3Storage storage;
    private final S3KeyFactory s3KeyFactory;
    private final CdnUrlBuilder cdnUrlBuilder;
    private static final Long MAX_FILE_SIZE = 10 * 1024 * 1024L;

    public PresignPutResponse presignPut(PresignPutRequest request) {
        if (request.fileSize() > MAX_FILE_SIZE) {
            throw new GlobalException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        String bucket = awsProps.bucket();
        String key = s3KeyFactory.postImageKey(request.bucketRootKey(), request.fileExt());

        String uploadUrl = storage.presignPut(
                bucket, key, request.contentType(),
                imageProps.presignedPutTtl(),
                request.fileSize()
        );

        String publicUrl = cdnUrlBuilder.toPublishUrl(key);
        return PresignPutResponse.builder()
                .uploadUrl(uploadUrl)
                .s3Key(key)
                .publicUrl(publicUrl)
                .build();
    }
}
