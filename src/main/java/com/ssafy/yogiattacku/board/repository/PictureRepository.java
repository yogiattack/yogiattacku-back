package com.ssafy.yogiattacku.board.repository;

import com.ssafy.yogiattacku.board.entity.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PictureRepository extends JpaRepository<Picture, UUID> {
    List<Picture> findAllByBucketRootKey(UUID bucketRootKey);

    void deleteAllByBucketRootKey(UUID bucketRootKey);

    @Query(value = """
            select distinct on (p.bucket_root_key) p.*
            from picture p
            where p.bucket_root_key in (:bucketRootKeys)
            order by p.bucket_root_key, p.created_at asc
            """, nativeQuery = true)
    List<Picture> findThumbnails(@Param("bucketRootKeys") List<UUID> bucketRootKeys);
}
