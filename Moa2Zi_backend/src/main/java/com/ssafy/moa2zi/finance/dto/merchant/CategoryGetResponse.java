package com.ssafy.moa2zi.finance.dto.merchant;

public record CategoryGetResponse(
        String categoryId,
        String categoryName,
        String categoryDescription
) {
}
