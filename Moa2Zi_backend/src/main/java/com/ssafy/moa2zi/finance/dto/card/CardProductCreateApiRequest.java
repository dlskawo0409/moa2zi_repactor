package com.ssafy.moa2zi.finance.dto.card;

import com.ssafy.moa2zi.common.infrastructure.finopenapi.RequestHeader;

import java.util.List;

public record CardProductCreateApiRequest(
        RequestHeader Header,
        String cardIssuerCode,
        String cardName,
        Long baselinePerformance,
        Long maxBenefitLimit,
        List<CardProductCreateRequest.CardBenefit> cardBenefits
) {

    public static CardProductCreateApiRequest from(
            RequestHeader Header,
            CardProductCreateRequest request
    ) {

        return new CardProductCreateApiRequest(
                Header,
                request.cardIssuerCode(),
                request.cardName(),
                request.baselinePerformance(),
                request.maxBenefitLimit(),
                request.cardBenefits()
        );
    }
}
