package com.ssafy.moa2zi.transaction.dto.response;

public record MonthlySpendSumResponse(
        Integer transactionDate,
        Long sum
) {
}
