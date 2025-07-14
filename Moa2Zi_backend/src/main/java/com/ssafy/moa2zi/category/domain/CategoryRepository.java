package com.ssafy.moa2zi.category.domain;

import com.ssafy.moa2zi.category.dto.response.CategoryInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>, CategoryRepositoryCustom {
    Optional<Category> findById(Long id);

    List<CategoryInfo> findCategoryInfoList(Long categoryId, CategoryType categoryType, Integer level, Long memberId);

    List<Category> findByParentIdIsNullAndCategoryType(CategoryType categoryType);

    List<Category> findByCategoryType(CategoryType categoryType);

}
