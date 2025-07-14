package com.ssafy.moa2zi.category.dto.response;

import java.util.List;

public record CategoryTypeListSearchResponse(
        Long categoryId,
        String categoryName,
        List<SubcategoryType> categoryList
) {
    public record SubcategoryType (
            Long subCategoryId,
            String subCategoryName
    ) { }
}
