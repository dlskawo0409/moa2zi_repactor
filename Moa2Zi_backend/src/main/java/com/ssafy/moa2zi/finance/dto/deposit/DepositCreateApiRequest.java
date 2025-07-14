package com.ssafy.moa2zi.finance.dto.deposit;

import com.ssafy.moa2zi.common.infrastructure.finopenapi.RequestHeader;

public record DepositCreateApiRequest(
        RequestHeader Header,
        String bankCode,
        String accountName,
        String accountDescription
) {

    public static DepositCreateApiRequest from(
            RequestHeader Header,
            DepositCreateRequest request
    ) {

        return new DepositCreateApiRequest(
                Header,
                request.bankCode(),
                request.accountName(),
                request.accountDescription()
        );
    }
}
