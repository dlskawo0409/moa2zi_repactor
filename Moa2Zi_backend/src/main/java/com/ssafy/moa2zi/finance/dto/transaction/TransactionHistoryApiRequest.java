package com.ssafy.moa2zi.finance.dto.transaction;

import com.ssafy.moa2zi.common.infrastructure.finopenapi.RequestHeader;

public record TransactionHistoryApiRequest(
        RequestHeader Header,
        String accountNo,
        String startDate,
        String endDate,
        String transactionType,
        String orderByType
) {

    public static TransactionHistoryApiRequest from(
            RequestHeader Header,
            TransactionHistoryRequest request
    ) {

        return new TransactionHistoryApiRequest(
                Header,
                request.accountNo(),
                request.startDate(),
                request.endDate(),
                String.valueOf(request.transactionType()),
                String.valueOf(request.orderByType())
        );
    }
}
