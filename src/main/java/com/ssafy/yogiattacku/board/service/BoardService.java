package com.ssafy.yogiattacku.board.service;

import com.github.f4b6a3.uuid.UuidCreator;
import com.ssafy.yogiattacku.attraction.dto.response.CategoryResponse;
import com.ssafy.yogiattacku.attraction.entity.Category;
import com.ssafy.yogiattacku.attraction.repository.CategoryRepository;
import com.ssafy.yogiattacku.board.dto.request.BoardCreateRequest;
import com.ssafy.yogiattacku.board.dto.response.BoardCreateResponse;
import com.ssafy.yogiattacku.board.dto.response.BoardDetailResponse;
import com.ssafy.yogiattacku.board.dto.response.BoardWriteResponse;
import com.ssafy.yogiattacku.board.dto.response.PictureResponse;
import com.ssafy.yogiattacku.board.entity.Board;
import com.ssafy.yogiattacku.board.entity.CategoryBoard;
import com.ssafy.yogiattacku.board.entity.Picture;
import com.ssafy.yogiattacku.board.processor.BoardViewCountBackUpProcessor;
import com.ssafy.yogiattacku.board.repository.BoardRepository;
import com.ssafy.yogiattacku.board.repository.CategoryBoardRepository;
import com.ssafy.yogiattacku.board.repository.PictureRepository;
import com.ssafy.yogiattacku.board.repository.view.BoardViewCountRepository;
import com.ssafy.yogiattacku.board.repository.view.BoardViewDistributedLockRepository;
import com.ssafy.yogiattacku.global.exception.ErrorCode;
import com.ssafy.yogiattacku.global.exception.GlobalException;
import com.ssafy.yogiattacku.user.entity.User;
import com.ssafy.yogiattacku.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryBoardRepository categoryBoardRepository;
    private final PictureRepository pictureRepository;
    private final UserRepository userRepository;
    private final BoardViewDistributedLockRepository boardViewDistributedLockRepository;

    private static final int BACK_UP_BATCH_SIZE = 100;
    private static final Duration TTL = Duration.ofMinutes(10);
    private final BoardViewCountRepository boardViewCountRepository;
    private final BoardViewCountBackUpProcessor boardViewCountBackUpProcessor;

    public BoardWriteResponse getBucketRootKey() {
        UUID key = UuidCreator.getTimeOrderedEpoch();
        return BoardWriteResponse.builder()
                .bucketRootKey(key)
                .build();
    }

    @Transactional
    public BoardCreateResponse createBoard(Long userId, BoardCreateRequest request) {
        UUID bucketRootKey = validateAndGetBucketRootKey(request);

        List<String> s3Keys = normalizeAndValidateS3Keys(bucketRootKey, request.s3Keys());
        List<Category> categories = fetchAndValidateCategories(request.categoryIds());

        Board savedBoard = saveBoard(userId, request, bucketRootKey);
        saveCategoryLinks(savedBoard, categories);
        savePictures(bucketRootKey, s3Keys);

        return BoardCreateResponse.builder()
                .boardId(savedBoard.getId())
                .build();
    }

    @Transactional
    public BoardDetailResponse getBoardDetail(Long boardId, Long viewerUserId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new GlobalException(ErrorCode.BOARD_NOT_FOUND));

        boolean locked = boardViewDistributedLockRepository.lock(boardId, viewerUserId, TTL);

        boardViewCountRepository.setIfAbsent(boardId, board.getViewCount());

        Long viewCount = locked
                ? boardViewCountRepository.increase(boardId)
                : boardViewCountRepository.read(boardId);

        if (locked && viewCount != null && viewCount % BACK_UP_BATCH_SIZE == 0) {
            boardViewCountBackUpProcessor.backUp(boardId, viewCount);
        }

        User user = userRepository.findById(board.getUserId())
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        List<CategoryResponse> categories =
                categoryBoardRepository.findAllByBoardIdWithCategory(boardId)
                        .stream()
                        .map(cb -> CategoryResponse.builder()
                                .categoryId(cb.getCategory().getId())
                                .name(cb.getCategory().getName())
                                .build()
                        ).toList();

        List<PictureResponse> pictures =
                pictureRepository.findAllByBucketRootKey(board.getBucketRootKey())
                        .stream()
                        .map(p -> PictureResponse.builder()
                                .pictureId(p.getId())
                                .s3Key(p.getS3Key())
                                .contentType(p.getContentType())
                                .build()
                        ).toList();
        return BoardDetailResponse.builder()
                .boardId(board.getId())
                .userId(board.getUserId())
                .nickname(user.getNickname())
                .title(board.getTitle())
                .content(board.getContent())
                .viewCount(viewCount == null ? board.getViewCount() : viewCount)
                .bucketRootKey(board.getBucketRootKey())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .categories(categories)
                .pictures(pictures)
                .build();
    }


    private UUID validateAndGetBucketRootKey(BoardCreateRequest request) {
        if (request == null || request.bucketRootKey() == null) {
            throw new GlobalException(ErrorCode.INVALID_REQUEST);
        }
        return request.bucketRootKey();
    }

    private List<String> normalizeAndValidateS3Keys(UUID bucketRootKey, List<String> rawS3Keys) {
        List<String> uniqueS3Keys = Optional.ofNullable(rawS3Keys)
                .orElse(List.of())
                .stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();

        if (uniqueS3Keys.isEmpty()) {
            return List.of();
        }

        String prefix = "images/" + bucketRootKey + "/";
        for (String s3Key : uniqueS3Keys) {
            if (!s3Key.startsWith(prefix)) {
                throw new GlobalException(ErrorCode.S3_KEY_INVALID);
            }
        }
        return uniqueS3Keys;
    }

    private List<Category> fetchAndValidateCategories(List<Long> categoryIds) {
        List<Long> ids = Optional.ofNullable(categoryIds).orElse(List.of());
        if (ids.isEmpty()) {
            throw new GlobalException(ErrorCode.INVALID_REQUEST);
        }

        List<Category> categories = categoryRepository.findAllById(ids);
        if (categories.size() != ids.size()) {
            throw new GlobalException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        return categories;
    }

    private Board saveBoard(Long userId, BoardCreateRequest request, UUID bucketRootKey) {
        Board board = Board.init(userId, request.title(), request.content(), bucketRootKey);
        return boardRepository.save(board);
    }

    private void saveCategoryLinks(Board savedBoard, List<Category> categories) {
        List<CategoryBoard> links = categories.stream()
                .map(c -> CategoryBoard.link(savedBoard, c))
                .toList();
        categoryBoardRepository.saveAll(links);
    }

    private void savePictures(UUID bucketRootKey, List<String> s3Keys) {
        List<Picture> pictures = s3Keys.stream()
                .map(s3Key -> Picture.init(bucketRootKey, s3Key, "image/*"))
                .toList();
        pictureRepository.saveAll(pictures);
    }
}
