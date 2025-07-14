package com.ssafy.moa2zi.geo.dto;

import com.ssafy.moa2zi.geo.domain.Gugun;

public record GugunResponse(
        int gugunCode,
        String gugunName
) {

    public static GugunResponse of(Gugun gugun) {

        return new GugunResponse(
                gugun.getGugunCode(),
                gugun.getGugunName()
        );
    }
}
