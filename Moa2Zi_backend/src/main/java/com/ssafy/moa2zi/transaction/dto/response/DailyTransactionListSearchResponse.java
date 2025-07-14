package com.ssafy.moa2zi.transaction.dto.response;

import com.ssafy.moa2zi.transaction.domain.Emotion;
import com.ssafy.moa2zi.transaction.domain.TransactionType;

import java.util.List;

public record DailyTransactionListSearchResponse(
        List<Transaction> transactionList
) {
    public record Transaction(
            Long transactionId,
            Integer transactionDate,
            String transactionTime,
            String memo,
            String categoryName,
            SubCategoryCommonResponse subCategory,
            Long transactionBalance,
            TransactionType transactionType,
            String paymentType,
            String merchantName,
            Emotion emotion
    ) {}
}
