package com.ssafy.moa2zi.transaction.dto.request;

public record MapClusterRequest(
        double lat,
        double lng,
        int zoomLevel,
        String keyword,
        Long categoryId,
        Integer startDate,
        Integer endDate
) {
}

