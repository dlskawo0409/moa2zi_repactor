package com.ssafy.moa2zi.finance.dto.card;

public record CardTransactionCreateResponse(
        Long transactionUniqueNo,
        String categoryId,
        String categoryName,
        Long merchantId,
        String merchantName,
        String transactionDate,
        String transactionTime,
        Long paymentBalance
) {
}
