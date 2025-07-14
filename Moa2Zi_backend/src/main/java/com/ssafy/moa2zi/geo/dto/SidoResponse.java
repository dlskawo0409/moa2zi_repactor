package com.ssafy.moa2zi.geo.dto;

import com.ssafy.moa2zi.geo.domain.Sido;

public record SidoResponse(
        int sidoCode,
        String sidoName
) {

    public static SidoResponse of(Sido sido) {

        return new SidoResponse(
                sido.getSidoCode(),
                sido.getSidoName()
        );
    }
}
