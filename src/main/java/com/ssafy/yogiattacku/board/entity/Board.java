package com.ssafy.yogiattacku.board.entity;

import com.ssafy.yogiattacku.global.common.TimeBaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "board")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Board extends TimeBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "view_count", nullable = false)
    private long viewCount;

    @Column(name = "bucket_root_key", nullable = false, unique = true)
    private UUID bucketRootKey;

    @Builder(access = AccessLevel.PRIVATE)
    private Board(Long userId, String title, String content, UUID bucketRootKey) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.viewCount = 0L;
        this.bucketRootKey = bucketRootKey;
    }

    public static Board init(Long userId, String title, String content, UUID bucketRootKey) {
        return Board.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .bucketRootKey(bucketRootKey)
                .build();
    }
}
