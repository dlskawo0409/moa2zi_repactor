package com.ssafy.moa2zi.transaction.dto.response;

import java.util.List;

public record TransactionCalenderSearchResponse(
        Long spendSum,
        Long amountDiffPrevMonth,
        List<DailySumWithDate> dailySumWithDate
) {
    public record DailySumWithDate(
            Long dayId,
            Integer date,
            Long sum
    ) {}
}