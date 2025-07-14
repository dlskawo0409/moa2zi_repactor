package com.ssafy.moa2zi.transaction.dto.request;

import jakarta.validation.constraints.NotNull;

public record MonthlySpendSumListSearchRequest(
        Long categoryId,

        @NotNull
        Integer transactionDate,

        @NotNull
        Integer unitCount,

        String accountNo,
        String cardNo,

        String transactionType,
        String paymentMethod,
        String emotion,
        String memo,
        Boolean isDescending
) {
        public MonthlySpendSumListSearchRequest {
                // isDescending 이 null 이면 기본값을 false 으로 설정
                if (isDescending == null) isDescending = false;
        }
}
