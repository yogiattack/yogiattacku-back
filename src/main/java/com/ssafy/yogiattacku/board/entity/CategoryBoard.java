package com.ssafy.yogiattacku.board.entity;

import com.ssafy.yogiattacku.attraction.entity.Category;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "category_board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Builder(access = AccessLevel.PRIVATE)
    private CategoryBoard(Board board, Category category) {
        this.board = board;
        this.category = category;
    }

    public static CategoryBoard link(Board board, Category category) {
        return CategoryBoard.builder()
                .board(board)
                .category(category)
                .build();
    }
}
