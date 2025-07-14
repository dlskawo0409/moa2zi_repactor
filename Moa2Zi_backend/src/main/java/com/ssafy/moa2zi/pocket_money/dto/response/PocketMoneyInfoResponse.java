package com.ssafy.moa2zi.pocket_money.dto.response;

import java.time.LocalDateTime;

public record PocketMoneyInfoResponse(
        Long totalAmount,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
