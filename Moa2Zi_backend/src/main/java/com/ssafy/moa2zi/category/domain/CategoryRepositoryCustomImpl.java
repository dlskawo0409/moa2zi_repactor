package com.ssafy.moa2zi.category.domain;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.moa2zi.category.dto.response.CategoryInfo;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class CategoryRepositoryCustomImpl implements CategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    QCategory category = new QCategory("category");

    @Override
    public List<CategoryInfo> findCategoryInfoList(Long categoryId, CategoryType categoryType, Integer level, Long memberId) {

        return queryFactory
                .select(
                        Projections.constructor(
                                CategoryInfo.class,
                                category.id,
                                category.parentId,
                                category.categoryType,
                                category.categoryName
                        )
                )
                .from(category)
                .where(
                        eqCategoryId(categoryId),
                        category.categoryType.eq(categoryType),
                        category.level.eq(level),
                        includeNullMemberId(memberId)
                )
                .fetch();

    }

    private BooleanExpression eqCategoryId(Long categoryId) {
        if (Objects.isNull(categoryId)) {
            return null; // level : 0 은 최상위 카테고리
        }
        return category.id.eq(categoryId);
    }

    private BooleanExpression includeNullMemberId(Long memberId) {
        return memberId != null ? category.memberId.eq(memberId).or(category.memberId.isNull()) : category.memberId.isNull();
    }

}