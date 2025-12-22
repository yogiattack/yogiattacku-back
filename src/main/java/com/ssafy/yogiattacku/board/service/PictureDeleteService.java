package com.ssafy.yogiattacku.board.service;

import com.ssafy.yogiattacku.global.exception.ErrorCode;
import com.ssafy.yogiattacku.global.exception.GlobalException;
import com.ssafy.yogiattacku.s3.config.AwsProps;
import com.ssafy.yogiattacku.board.entity.Picture;
import com.ssafy.yogiattacku.board.repository.PictureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PictureDeleteService {
    private final S3Client s3Client;
    private final AwsProps awsProps;
    private final PictureRepository pictureRepository;

    @Transactional
    public void deleteByBoardId(Long boardId) {
        List<Picture> pictures = pictureRepository.findByBoardId(boardId);

        if (pictures.isEmpty()) {
            return;
        }

        for (Picture picture : pictures) {
            try {
                s3Client.deleteObject(b -> b.bucket(awsProps.bucket()).key(picture.getS3Key()));
            } catch (Exception e) {
                throw new GlobalException(ErrorCode.S3_DELETE_FAILED);
            }
        }
        pictureRepository.deleteAll(pictures);
    }
}
