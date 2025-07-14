package com.ssafy.moa2zi.transaction.dto.request;

import jakarta.validation.constraints.NotNull;

public record TransactionListSearchRequest(
        @NotNull(message = "memberId 는 필수 값입니다.")
        Long memberId,

        Long categoryId,

        @NotNull(message = "transactionDate 는 필수 값입니다.")
        Integer transactionDate,

        String merchantName,
        String accountNo,
        String cardNo,
        String transactionType,
        String paymentMethod,
        String emotion,
        String memo,
        String transactionTime
) { }