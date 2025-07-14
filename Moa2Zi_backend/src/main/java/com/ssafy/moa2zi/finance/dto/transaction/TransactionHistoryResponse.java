package com.ssafy.moa2zi.finance.dto.transaction;

import java.util.List;

public record TransactionHistoryResponse(
        String totalCount,
        List<TransactionInfo> list
) {

    public record TransactionInfo(
            String transactionUniqueNo,
            String transactionDate,
            String transactionTime,
            TransactionApiType transactionType,
            String transactionTypeName,
            String transactionAccountNo,
            Long transactionBalance,
            Long transactionAfterBalance,
            String transactionSummary,
            String transactionMemo
    ) {
    }
}
