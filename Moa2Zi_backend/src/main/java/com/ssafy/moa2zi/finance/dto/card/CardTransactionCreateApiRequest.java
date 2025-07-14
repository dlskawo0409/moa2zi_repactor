package com.ssafy.moa2zi.finance.dto.card;

import com.ssafy.moa2zi.common.infrastructure.finopenapi.RequestHeader;

public record CardTransactionCreateApiRequest(
        RequestHeader Header,
        String cardNo,
        String cvc,
        Long merchantId,
        Long paymentBalance
) {

    public static CardTransactionCreateApiRequest from(
            RequestHeader Header,
            CardTransactionCreateRequest request
    ) {

        return new CardTransactionCreateApiRequest(
                Header,
                request.cardNo(),
                request.cvc(),
                request.merchantId(),
                request.paymentBalance()
        );
    }
}
