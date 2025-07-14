package com.ssafy.moa2zi.finance.event;

import com.ssafy.moa2zi.transaction.domain.Transaction;
import lombok.Builder;

import java.util.List;

@Builder
public record FinanceEvent(
        Long memberId,
        FinanceEventType type,
        List<Transaction> transactions
) {
}
