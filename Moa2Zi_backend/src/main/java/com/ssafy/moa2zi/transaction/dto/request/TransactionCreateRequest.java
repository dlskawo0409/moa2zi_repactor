package com.ssafy.moa2zi.transaction.dto.request;

import com.ssafy.moa2zi.transaction.domain.Emotion;
import com.ssafy.moa2zi.transaction.domain.TransactionType;
import jakarta.validation.constraints.NotNull;

public record TransactionCreateRequest(

        @NotNull(message = "categoryId 는 필수 값입니다.")
        Long categoryId,

        @NotNull(message = "transactionDate 는 필수 값입니다.")
        Integer transactionDate,

        @NotNull(message = "transactionBalance 는 필수 값입니다.")
        Long transactionBalance,

        @NotNull(message = "transactionType 은 필수 값입니다.")
        TransactionType transactionType,

        @NotNull(message = "paymentType 은 필수 값입니다.")
        String paymentType,

        Emotion emotion,

        String memo,

        @NotNull(message = "transactionTime 은 필수 값입니다.")
        String transactionTime,

        @NotNull(message = "merchantName 은 필수 값입니다.")
        String merchantName,

        Boolean isInBudget

) {
        public TransactionCreateRequest {
                if (isInBudget == null) {
                        isInBudget = false;
                }
        }
}
