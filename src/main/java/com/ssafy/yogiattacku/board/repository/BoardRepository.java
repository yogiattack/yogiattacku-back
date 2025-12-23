package com.ssafy.yogiattacku.board.repository;

import com.ssafy.yogiattacku.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Modifying
    @Query(value = """
            update board
               set view_count = :viewCount
             where board_id = :boardId
               and view_count < :viewCount
            """, nativeQuery = true)
    int updateViewCountIfLess(@Param("boardId") Long boardId,
                              @Param("viewCount") Long viewCount);


    @Query(value = """
            select *
            from board
            order by board_id desc
            limit :limit offset :offset
            """, nativeQuery = true)
    List<Board> findPage(@Param("offset") long offset, @Param("limit") int limit);

    @Query(value = """
            select distinct b.*
            from board b
            join category_board cb on cb.board_id = b.board_id
            where cb.category_id in (:categoryIds)
            order by b.board_id desc
            limit :limit offset :offset
            """, nativeQuery = true)
    List<Board> findPageByCategoryIds(@Param("categoryIds") List<Long> categoryIds,
                                      @Param("offset") long offset,
                                      @Param("limit") int limit);

    @Query(value = """
            select *
            from board
            where user_id = :userId
            order by board_id desc
            limit :limit offset :offset
            """, nativeQuery = true)
    List<Board> findMyPage(@Param("userId") Long userId,
                           @Param("offset") long offset,
                           @Param("limit") int limit);

    @Query(value = """
                select *
                from board
                order by view_count desc, board_id desc
                limit :limit
            """, nativeQuery = true)
    List<Board> findPopular(@Param("limit") int limit);
}
