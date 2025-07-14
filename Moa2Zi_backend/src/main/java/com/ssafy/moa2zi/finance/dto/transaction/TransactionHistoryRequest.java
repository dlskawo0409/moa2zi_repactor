package com.ssafy.moa2zi.finance.dto.transaction;

public record TransactionHistoryRequest(
        String accountNo,
        String startDate,
        String endDate,
        TransactionApiType transactionType,
        OrderByType orderByType
) {
}
