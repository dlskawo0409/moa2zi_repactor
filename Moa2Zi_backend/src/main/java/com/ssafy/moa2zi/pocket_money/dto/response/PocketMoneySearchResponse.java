package com.ssafy.moa2zi.pocket_money.dto.response;

import java.time.LocalDateTime;

public record PocketMoneySearchResponse(
        Boolean thisMonthHave,
        Long totalAmount,
        Long spend,
        Long left,
        Long dayCanUse,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
