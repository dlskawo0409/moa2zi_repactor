package com.ssafy.moa2zi.finance.dto.merchant;

import com.ssafy.moa2zi.common.infrastructure.finopenapi.RequestHeader;

public record MerchantRegisterApiRequest(
        RequestHeader Header,
        String categoryId,
        String merchantName
) {

    public static MerchantRegisterApiRequest from(
            RequestHeader Header,
            MerchantRegisterRequest request
    ) {

        return new MerchantRegisterApiRequest(
                Header,
                request.categoryId(),
                request.merchantName()
        );
    }
}
