package com.ssafy.moa2zi.category.application;

import com.ssafy.moa2zi.category.domain.CategoryRepository;
import com.ssafy.moa2zi.category.domain.CategoryType;
import com.ssafy.moa2zi.category.dto.response.CategoryInfo;
import com.ssafy.moa2zi.category.dto.response.CategoryTypeListSearchResponse;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // [API] 카테고리 종류 가져오기 =====
    public List<CategoryTypeListSearchResponse> getCategoryTypeList(
            Long categoryId,
            CategoryType categoryType,
            Integer level,
            CustomMemberDetails loginMember
    ) {

        Long memberId = loginMember.getMemberId();
        if( level == null ) level = 0; // 카테고리 id 값 null 이면 최상위카테고리

        List<CategoryInfo> categoryInfoList = categoryRepository.findCategoryInfoList(categoryId, categoryType, level, memberId);
        List<CategoryInfo> subCategoryInfoList = categoryRepository.findCategoryInfoList(null, categoryType, level+1, memberId);

        List<CategoryTypeListSearchResponse> resultList = new ArrayList<>();

        for(CategoryInfo categoryInfo : categoryInfoList) { // 상위 카테고리 순회
            List<CategoryTypeListSearchResponse.SubcategoryType> subCategoryList = new ArrayList<>();
            CategoryTypeListSearchResponse categoryInfoTemp = new CategoryTypeListSearchResponse(
                    categoryInfo.categoryId(),
                    categoryInfo.categoryName(),
                    subCategoryList
            );

            for(CategoryInfo subCategoryInfo : subCategoryInfoList) { // 하위 카테고리 순회

                if(categoryInfo.categoryId().equals(subCategoryInfo.parentId())) { // 부모, 자식 관계가 맞으면

                    subCategoryList.add(
                            new CategoryTypeListSearchResponse.SubcategoryType(
                                    subCategoryInfo.categoryId(),
                                    subCategoryInfo.categoryName()
                            ));

                }
            }
            resultList.add(categoryInfoTemp);
        }

        return resultList;
    }

}
