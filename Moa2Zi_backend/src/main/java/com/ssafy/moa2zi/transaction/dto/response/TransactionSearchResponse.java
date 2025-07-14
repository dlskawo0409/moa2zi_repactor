package com.ssafy.moa2zi.transaction.dto.response;

import com.ssafy.moa2zi.transaction.domain.Emotion;

import java.util.List;

public record TransactionSearchResponse(
        List<TransactionInfoResponse> transactionList,
        Long total,
        int size,
        boolean hasNext,
        Long next
) {
}
