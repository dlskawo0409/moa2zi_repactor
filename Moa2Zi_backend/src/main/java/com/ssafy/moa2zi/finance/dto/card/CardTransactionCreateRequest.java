package com.ssafy.moa2zi.finance.dto.card;

public record CardTransactionCreateRequest(
        String cardNo,
        String cvc,
        Long merchantId,
        Long paymentBalance
) {
}
