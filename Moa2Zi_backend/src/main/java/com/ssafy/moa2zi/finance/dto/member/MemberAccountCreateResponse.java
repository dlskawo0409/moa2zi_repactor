package com.ssafy.moa2zi.finance.dto.member;

public record MemberAccountCreateResponse(
        String bankCode,
        String accountNo,
        Currency currency
) {

    public record Currency (
            String currency,
            String currencyName
    ) {
    }
}
