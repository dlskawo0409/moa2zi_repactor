package com.ssafy.moa2zi.transaction.dto.request;

public record TransactionSearchRequest(
        String keyword,
        Long categoryId,
        Integer startDate,
        Integer endDate,
        String geohashCode,
        Long next,
        Integer size
) {
    public TransactionSearchRequest {
        if (next == null) {
            next = 0L;
        }
        if (size == null || size == 0) {
            size = 10;
        }
    }

}
