package com.ssafy.moa2zi.finance.dto.card;

import java.util.List;

public record CardTransactionGetResponse(
        String cardIssuerCode,
        String cardIssuerName,
        String cardName,
        String cardNo,
        Long estimatedBalance,
        List<TransactionInfo> transactionList
) {

    public record TransactionInfo(
            String transactionUniqueNo,
            String categoryId,
            String categoryName,
            Long merchantId,
            String merchantName,
            String transactionDate,
            String transactionTime,
            Long transactionBalance,
            String billStatementsYn,
            String billStatementsStatus
    ) {
    }
}
