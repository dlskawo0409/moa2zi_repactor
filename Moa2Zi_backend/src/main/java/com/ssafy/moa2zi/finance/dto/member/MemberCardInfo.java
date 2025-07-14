package com.ssafy.moa2zi.finance.dto.member;

public record MemberCardInfo(
        String cardNo,
        String cvc,
        String cardUniqueNo,
        String cardIssuerCode,
        String cardIssuerName,
        String cardName,
        String baselinePerformance,
        String maxBenefitLimit,
        String cardDescription,
        String cardExpiryDate,
        String withdrawalAccountNo,
        String withdrawalDate
) {
}
