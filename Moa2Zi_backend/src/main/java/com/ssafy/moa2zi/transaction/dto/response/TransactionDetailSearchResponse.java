package com.ssafy.moa2zi.transaction.dto.response;

import com.ssafy.moa2zi.transaction.domain.TransactionType;

public record TransactionDetailSearchResponse(
        Long transactionId,
        String categoryName,
        SubCategoryCommonResponse subCategory,
        Long transactionBalance,
        TransactionType transactionType,
        Integer transactionDate,
        String paymentType,
        String memo,
        String transactionTime
) { }
