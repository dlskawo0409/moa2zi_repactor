package com.ssafy.moa2zi.finance.dto.deposit;

public record DepositCreateRequest(
        String bankCode,
        String accountName,
        String accountDescription
) {
}
