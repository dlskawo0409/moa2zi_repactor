package com.ssafy.moa2zi.transaction.dto.response;

import com.ssafy.moa2zi.transaction.domain.Emotion;

public record TransactionInfoResponse(
        Long transactionId,
        Integer transactionDate,
        String transactionTime,
        Long transactionBalance,
        String paymentMethod,
        Emotion emotion,
        String merchantName,
        Long categoryId,
        Double latitude,
        Double longitude
) {
}
