package com.ssafy.moa2zi.finance.dto.auth;

public record CheckAuthApiResponse(
        String status, // SUCCESS, FAIL
        Long transactionUniqueNo,
        String accountNo
) {
}
