package com.ssafy.yogiattacku.board.controller;

import com.ssafy.yogiattacku.board.dto.request.BoardCreateRequest;
import com.ssafy.yogiattacku.board.dto.response.BoardCreateResponse;
import com.ssafy.yogiattacku.board.dto.response.BoardDetailResponse;
import com.ssafy.yogiattacku.board.dto.response.BoardWriteResponse;
import com.ssafy.yogiattacku.board.dto.response.page.BoardListItemResponse;
import com.ssafy.yogiattacku.board.dto.response.page.BoardPageResponse;
import com.ssafy.yogiattacku.board.service.BoardDeleteService;
import com.ssafy.yogiattacku.board.service.BoardQueryService;
import com.ssafy.yogiattacku.board.service.BoardService;
import com.ssafy.yogiattacku.global.response.ResponseBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final BoardDeleteService boardDeleteService;
    private final BoardQueryService boardQueryService;

    @GetMapping("/write")
    public ResponseBody<BoardWriteResponse> getBoardBucketRootKey() {
        return ResponseBody.success(boardService.getBucketRootKey());
    }

    @PostMapping
    public ResponseBody<BoardCreateResponse> createBoard(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody BoardCreateRequest request
    ) {
        return ResponseBody.success(boardService.createBoard(userId, request));
    }

    @GetMapping("/{boardId}")
    public ResponseBody<BoardDetailResponse> getBoardDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long boardId
    ) {
        return ResponseBody.success(boardService.getBoardDetail(boardId, userId));
    }

    @DeleteMapping("/{boardId}")
    public ResponseBody<Void> deleteBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal Long userId
    ) {
        boardDeleteService.deleteBoard(userId, boardId);
        return ResponseBody.success(null);
    }

    @GetMapping
    public ResponseBody<BoardPageResponse> readAll(
            @AuthenticationPrincipal Long userId,
            @RequestParam(name = "page") long page,
            @RequestParam(name = "pageSize") long pageSize,
            @RequestParam(required = false) List<Long> categoryIds
    ) {
        return ResponseBody.success(boardQueryService.readAll(userId, page, pageSize, categoryIds));
    }

    @GetMapping("/mypage")
    public ResponseBody<BoardPageResponse> readMyPage(
            @AuthenticationPrincipal Long userId,
            @RequestParam(name = "page") long page,
            @RequestParam(name = "pageSize") long pageSize
    ) {
        return ResponseBody.success(boardQueryService.readMyPage(userId, page, pageSize));
    }

    @GetMapping("/popular")
    public ResponseBody<List<BoardListItemResponse>> readPopular(@AuthenticationPrincipal Long userId) {
        return ResponseBody.success(boardQueryService.readPopular(userId));
    }
}
