package com.ssafy.yogiattacku.board.processor;

import com.ssafy.yogiattacku.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BoardViewCountBackUpProcessor {
    private final BoardRepository boardRepository;

    @Transactional
    public void backUp(Long boardId, Long viewCount) {
        boardRepository.updateViewCountIfLess(boardId, viewCount);
    }
}
