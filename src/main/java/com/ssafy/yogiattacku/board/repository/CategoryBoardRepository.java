package com.ssafy.yogiattacku.board.repository;

import com.ssafy.yogiattacku.board.entity.CategoryBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryBoardRepository extends JpaRepository<CategoryBoard, Long> {

    @Query("""
                select cb
                from CategoryBoard cb
                join fetch cb.category c
                where cb.board.id = :boardId
                order by cb.id asc
            """)
    List<CategoryBoard> findAllByBoardIdWithCategory(Long boardId);
}
