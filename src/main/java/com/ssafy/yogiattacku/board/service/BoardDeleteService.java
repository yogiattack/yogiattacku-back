package com.ssafy.yogiattacku.board.service;

import com.ssafy.yogiattacku.board.entity.Board;
import com.ssafy.yogiattacku.board.entity.Picture;
import com.ssafy.yogiattacku.board.repository.BoardRepository;
import com.ssafy.yogiattacku.board.repository.CategoryBoardRepository;
import com.ssafy.yogiattacku.board.repository.PictureRepository;
import com.ssafy.yogiattacku.global.exception.ErrorCode;
import com.ssafy.yogiattacku.global.exception.GlobalException;
import com.ssafy.yogiattacku.s3.config.AwsProps;
import com.ssafy.yogiattacku.s3.storage.S3Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardDeleteService {
    private final BoardRepository boardRepository;
    private final PictureRepository pictureRepository;
    private final CategoryBoardRepository categoryBoardRepository;
    private final S3Storage s3Storage;
    private final AwsProps awsProps;

    @Transactional
    public void deleteBoard(Long userId, Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new GlobalException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getUserId().equals(userId)) {
            throw new GlobalException(ErrorCode.ACCESS_DENIED);
        }

        UUID bucketRootKey = board.getBucketRootKey();

        List<String> s3Keys = pictureRepository.findAllByBucketRootKey(bucketRootKey).stream()
                .map(Picture::getS3Key)
                .filter(k -> k != null && !k.isBlank())
                .distinct()
                .toList();

        try {
            s3Storage.deleteAllOrThrow(awsProps.bucket(), s3Keys);
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.S3_DELETE_FAILED);
        }

        pictureRepository.deleteAllByBucketRootKey(bucketRootKey);
        categoryBoardRepository.deleteAllByBoardId(boardId);
        boardRepository.delete(board);
    }
}
