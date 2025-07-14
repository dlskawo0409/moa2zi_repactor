package com.ssafy.moa2zi.finance.dto.card;

import com.ssafy.moa2zi.card.domain.Card;

import java.util.List;

/**
 * 카드 혜택은 반드시 하나 이상 지정되어야 함
 */
public record CardProductCreateRequest(
        String cardIssuerCode,
        String cardName,
        Long baselinePerformance,
        Long maxBenefitLimit,
        List<CardBenefit> cardBenefits
) {

    public record CardBenefit(
            String categoryId,
         Double discountRate
    ){
    }
}
