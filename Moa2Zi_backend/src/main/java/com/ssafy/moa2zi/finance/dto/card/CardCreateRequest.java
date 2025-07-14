package com.ssafy.moa2zi.finance.dto.card;

public record CardCreateRequest(
        String cardUniqueNo,
        String withdrawalAccountNo,
        String withdrawalDate
) {
}
