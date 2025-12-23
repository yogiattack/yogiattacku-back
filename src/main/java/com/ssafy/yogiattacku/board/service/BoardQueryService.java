package com.ssafy.yogiattacku.board.service;

import com.ssafy.yogiattacku.attraction.dto.response.CategoryResponse;
import com.ssafy.yogiattacku.board.dto.response.page.BoardListItemResponse;
import com.ssafy.yogiattacku.board.dto.response.page.BoardPageResponse;
import com.ssafy.yogiattacku.board.dto.response.page.PageMeta;
import com.ssafy.yogiattacku.board.entity.Board;
import com.ssafy.yogiattacku.board.entity.Picture;
import com.ssafy.yogiattacku.board.repository.BoardRepository;
import com.ssafy.yogiattacku.board.repository.CategoryBoardRepository;
import com.ssafy.yogiattacku.board.repository.PictureRepository;
import com.ssafy.yogiattacku.global.exception.ErrorCode;
import com.ssafy.yogiattacku.global.exception.GlobalException;
import com.ssafy.yogiattacku.user.entity.User;
import com.ssafy.yogiattacku.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardQueryService {
    @Value("${thumbnail.basic-url}")
    private String defaultThumbnailS3Key;

    private final BoardRepository boardRepository;
    private final PictureRepository pictureRepository;
    private final UserRepository userRepository;
    private final CategoryBoardRepository categoryBoardRepository;

    @Transactional(readOnly = true)
    public BoardPageResponse readAll(long page, long pageSize, List<Long> categoryIds) {
        validatePage(page, pageSize);

        long offset = (page - 1) * pageSize;
        int limit = Math.toIntExact(pageSize + 1);

        List<Board> fetched = (categoryIds == null || categoryIds.isEmpty())
                ? boardRepository.findPage(offset, limit)
                : boardRepository.findPageByCategoryIds(categoryIds, offset, limit);
        return toPageResponse(fetched, page, pageSize);
    }

    @Transactional(readOnly = true)
    public BoardPageResponse readMyPage(Long userId, long page, long pageSize) {
        validatePage(page, pageSize);

        long offset = (page - 1) * pageSize;
        int limit = Math.toIntExact(pageSize + 1);

        List<Board> fetched = boardRepository.findMyPage(userId, offset, limit);
        return toPageResponse(fetched, page, pageSize);
    }

    @Transactional(readOnly = true)
    public List<BoardListItemResponse> readPopular() {
        int limit = 9;
        List<Board> boards = boardRepository.findPopular(limit);
        return mapToListItems(boards);
    }

    private BoardPageResponse toPageResponse(List<Board> fetched, long page, long pageSize) {
        boolean hasNext = fetched.size() > pageSize;
        List<Board> boards = hasNext
                ? fetched.subList(0, Math.toIntExact(pageSize))
                : fetched;

        List<BoardListItemResponse> items = mapToListItems(boards);

        PageMeta pageMeta = PageMeta.builder()
                .page(page)
                .pageSize(pageSize)
                .hasNext(hasNext)
                .build();

        return BoardPageResponse.builder()
                .items(items)
                .page(pageMeta)
                .build();
    }

    private List<BoardListItemResponse> mapToListItems(List<Board> boards) {
        if (boards.isEmpty()) {
            return List.of();
        }

        Map<Long, User> userMap = userRepository.findAllById(
                        boards.stream()
                                .map(Board::getUserId)
                                .distinct()
                                .toList()
                ).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<UUID> bucketKeys = boards.stream()
                .map(Board::getBucketRootKey)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<UUID, Picture> thumbMap = bucketKeys.isEmpty()
                ? Map.of()
                : pictureRepository.findThumbnails(bucketKeys).stream()
                .collect(Collectors.toMap(
                        Picture::getBucketRootKey,
                        Function.identity(),
                        (a, b) -> a
                ));

        List<Long> boardIds = boards.stream().map(Board::getId).toList();

        Map<Long, List<CategoryResponse>> categoriesMap =
                categoryBoardRepository.findAllByBoardIdsWithCategory(boardIds).stream()
                        .collect(Collectors.groupingBy(
                                cb -> cb.getBoard().getId(),
                                Collectors.mapping(
                                        cb -> CategoryResponse.builder()
                                                .categoryId(cb.getCategory().getId())
                                                .name(cb.getCategory().getName())
                                                .build(),
                                        Collectors.toList()
                                )
                        ));


        return boards.stream()
                .map(b -> {
                    User user = userMap.get(b.getUserId());
                    Picture thumb = thumbMap.get(b.getBucketRootKey());

                    String thumbnailKey =
                            (thumb == null || thumb.getS3Key() == null || thumb.getS3Key().isBlank())
                                    ? defaultThumbnailS3Key
                                    : thumb.getS3Key();

                    List<CategoryResponse> categories =
                            categoriesMap.getOrDefault(b.getId(), List.of());

                    return BoardListItemResponse.builder()
                            .boardId(b.getId())
                            .userId(b.getUserId())
                            .nickname(user != null ? user.getNickname() : null)
                            .title(b.getTitle())
                            .viewCount(b.getViewCount())
                            .bucketRootKey(b.getBucketRootKey())
                            .thumbnailS3Key(thumbnailKey)
                            .createdAt(b.getCreatedAt())
                            .categories(categories)
                            .build();
                })
                .toList();
    }

    private void validatePage(long page, long pageSize) {
        if (page < 1 || pageSize < 1) {
            throw new GlobalException(ErrorCode.INVALID_REQUEST);
        }
    }
}
