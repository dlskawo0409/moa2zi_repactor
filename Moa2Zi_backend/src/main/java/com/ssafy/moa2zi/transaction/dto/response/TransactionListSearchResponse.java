package com.ssafy.moa2zi.transaction.dto.response;

import java.util.List;

public record TransactionListSearchResponse(
        Long incomeSum,
        Long spendSum,
        Long totalSum,
        List<TransactionWithDate> transactionWithDate
) {
    public record TransactionWithDate(
            Long dayId,
            Integer transactionDate,
            String dayOfWeek,
            List<Transaction> transactionList
    ) {}

    public record Transaction(
            Long transactionId,
            String transactionTime,
            String memo,
            String categoryName,
            SubCategoryCommonResponse subCategory,
            String merchantName,
            Long transactionBalance,
            String transactionType,
            String paymentType
    ) {}
}