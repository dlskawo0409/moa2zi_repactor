package com.ssafy.moa2zi.category.domain;

import com.ssafy.moa2zi.category.dto.response.CategoryInfo;

import java.util.List;

public interface CategoryRepositoryCustom {
    List<CategoryInfo> findCategoryInfoList(Long categoryId, CategoryType categoryType, Integer level, Long memberId);
}