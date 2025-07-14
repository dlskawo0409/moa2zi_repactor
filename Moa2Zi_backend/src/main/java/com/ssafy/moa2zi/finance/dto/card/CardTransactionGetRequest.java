package com.ssafy.moa2zi.finance.dto.card;

public record CardTransactionGetRequest(
        String cardNo,
        String cvc,
        String startDate,
        String endDate
) {
}
