package com.ssafy.moa2zi.finance.dto.auth;

public record OpenAccountAuthResponse(
        Long transactionUniqueNo,
        String accountNo
) {
}
