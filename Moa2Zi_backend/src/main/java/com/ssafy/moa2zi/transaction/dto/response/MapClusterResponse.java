package com.ssafy.moa2zi.transaction.dto.response;

public record MapClusterResponse(
        double latitude,
        double longitude,
        String geohashCode,
        int count
) {
}
