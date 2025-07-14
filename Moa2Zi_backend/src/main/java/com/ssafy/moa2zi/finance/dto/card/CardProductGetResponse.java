package com.ssafy.moa2zi.finance.dto.card;

public record CardProductGetResponse(
        String cardUniqueNo,
        String cardIssuerCode,
        String cardIssuerName,
        String cardName,
        String cardTypeCode,
        String cardTypeName
) {
}
