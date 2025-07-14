package com.ssafy.moa2zi.geo.dto;

import com.ssafy.moa2zi.geo.domain.Dong;

public record DongResponse(
        int dongCode,
        String dongName
) {

    public static DongResponse of(Dong dong) {

        return new DongResponse(
                dong.getDongCode(),
                dong.getDongName()
        );
    }
}
