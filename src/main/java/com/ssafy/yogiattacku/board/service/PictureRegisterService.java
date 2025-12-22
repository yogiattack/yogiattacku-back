package com.ssafy.yogiattacku.board.service;

import com.ssafy.yogiattacku.global.exception.ErrorCode;
import com.ssafy.yogiattacku.global.exception.GlobalException;
import com.ssafy.yogiattacku.s3.config.AwsProps;
import com.ssafy.yogiattacku.board.dto.request.RegistPictureRequest;
import com.ssafy.yogiattacku.board.dto.response.RegistPictureResponse;
import com.ssafy.yogiattacku.board.entity.Picture;
import com.ssafy.yogiattacku.board.repository.PictureRepository;
import com.ssafy.yogiattacku.s3.storage.CdnUrlBuilder;
import com.ssafy.yogiattacku.s3.storage.S3Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PictureRegisterService {
    private final AwsProps awsProps;
    private final S3Storage storage;
    private final CdnUrlBuilder cdnUrlBuilder;
    private final PictureRepository pictureRepository;

    @Transactional
    public RegistPictureResponse registerUploaded(UUID bucketRootKey, RegistPictureRequest request) {
        String bucket = awsProps.bucket();

        if (!storage.exists(bucket, request.s3Key())) {
            throw new GlobalException(ErrorCode.S3_OBJECT_NOT_FOUND);
        }

        String publicUrl = cdnUrlBuilder.toPublishUrl(request.s3Key());
        Picture picture = Picture.init(bucketRootKey, request.s3Key(), request.contentType());
        pictureRepository.save(picture);

        return new RegistPictureResponse(picture.getId(), publicUrl);
    }
}
