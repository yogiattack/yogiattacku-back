package com.ssafy.yogiattacku.board.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import com.ssafy.yogiattacku.global.common.TimeBaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "picture")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Picture extends TimeBaseEntity {
    @Id
    @Column(name = "picture_id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "bucket_root_key", nullable = false)
    private UUID bucketRootKey;

    @Column(name = "s3_key", nullable = false, length = 1024)
    private String s3Key;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Builder(access = AccessLevel.PRIVATE)
    private Picture(UUID id, UUID bucketRootKey, String s3Key, String contentType) {
        this.id = id;
        this.bucketRootKey = bucketRootKey;
        this.s3Key = s3Key;
        this.contentType = contentType;
    }

    public static Picture init(UUID bucketRootKey, String s3Key, String contentType) {
        return Picture.builder()
                .id(UuidCreator.getTimeOrderedEpoch())
                .bucketRootKey(bucketRootKey)
                .s3Key(s3Key)
                .contentType(contentType)
                .build();
    }
}
