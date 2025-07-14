package com.ssafy.moa2zi.finance.dto.merchant;

public record MerchantGetResponse(
        String categoryId,
        String categoryName,
        Long merchantId,
        String merchantName
) {
}
