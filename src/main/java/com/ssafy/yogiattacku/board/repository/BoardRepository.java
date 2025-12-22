package com.ssafy.yogiattacku.board.repository;

import com.ssafy.yogiattacku.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

}
