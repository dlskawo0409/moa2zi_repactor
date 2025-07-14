package com.ssafy.moa2zi.finance.dto.deposit;

public record DepositListGetResponse(
        String accountTypeUniqueNo,
        String bankCode,
        String bankName,
        String accountTypeCode,
        String accountTypeName,
        String accountName,
        String accountDescription,
        String accountType
) {
}
