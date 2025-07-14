package com.ssafy.moa2zi.finance.dto.card;

import com.ssafy.moa2zi.common.infrastructure.finopenapi.RequestHeader;

public record CardCreateApiRequest(
        RequestHeader Header,
        String cardUniqueNo,
        String withdrawalAccountNo,
        String withdrawalDate
) {

    public static CardCreateApiRequest from(
            RequestHeader Header,
            CardCreateRequest request
    ) {

        return new CardCreateApiRequest(
                Header,
                request.cardUniqueNo(),
                request.withdrawalAccountNo(),
                request.withdrawalDate()
        );
    }
}
