package com.ssafy.moa2zi.transaction.dto.request;

import jakarta.validation.constraints.NotNull;

public record AnalysisByCategoryListSearchRequest(
        @NotNull
        Integer transactionDate,

        @NotNull
        Integer unitCount,

        Integer unitRankCount,

        String accountNo,
        String cardNo,

        String transactionType,
        String paymentMethod,
        String emotion,
        String memo,
        Boolean isDescending
) {
        public AnalysisByCategoryListSearchRequest {
            // 많이 쓴 순서대로 순위니까 null 이면 기본값을 true 으로 설정
            if (isDescending == null) isDescending = true;
            if (unitRankCount == null) unitRankCount = 3;
        }
}
