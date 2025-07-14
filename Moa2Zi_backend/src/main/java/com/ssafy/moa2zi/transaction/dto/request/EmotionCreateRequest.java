package com.ssafy.moa2zi.transaction.dto.request;

import com.ssafy.moa2zi.transaction.domain.Emotion;

public record EmotionCreateRequest(
        Long transactionId,
        Emotion emotion
) {
}
