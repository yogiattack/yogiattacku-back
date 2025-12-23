package com.ssafy.yogiattacku.board.repository;

import com.ssafy.yogiattacku.board.entity.CategoryBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryBoardRepository extends JpaRepository<CategoryBoard, Long> {

    @Query("""
                select cb
                from CategoryBoard cb
                join fetch cb.category c
                where cb.board.id = :boardId
                order by cb.id asc
            """)
    List<CategoryBoard> findAllByBoardIdWithCategory(@Param("boardId") Long boardId);

    @Query("""
                select cb
                from CategoryBoard cb
                join fetch cb.category c
                where cb.board.id in :boardIds
                order by cb.board.id asc, cb.id asc
            """)
    List<CategoryBoard> findAllByBoardIdsWithCategory(@Param("boardIds") List<Long> boardIds);

    void deleteAllByBoardId(Long boardId);
}
