package com.ssafy.yogiattacku.attraction.repository;

import com.ssafy.yogiattacku.attraction.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
