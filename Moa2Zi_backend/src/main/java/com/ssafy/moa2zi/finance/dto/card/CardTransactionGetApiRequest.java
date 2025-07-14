package com.ssafy.moa2zi.finance.dto.card;

import com.ssafy.moa2zi.common.infrastructure.finopenapi.RequestHeader;

public record CardTransactionGetApiRequest(
        RequestHeader Header,
        String cardNo,
        String cvc,
        String startDate,
        String endDate
) {

    public static CardTransactionGetApiRequest from(
            RequestHeader Header,
            CardTransactionGetRequest request
    ) {

        return new CardTransactionGetApiRequest(
                Header,
                request.cardNo(),
                request.cvc(),
                request.startDate(),
                request.endDate()
        );
    }
}
