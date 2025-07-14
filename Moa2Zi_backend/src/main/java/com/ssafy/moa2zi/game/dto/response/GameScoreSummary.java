package com.ssafy.moa2zi.game.dto.response;

import java.time.LocalDateTime;

public record GameScoreSummary (
        Long loungeId,
        String loungeName,
        Long gameId,
        Long memberId,
        Integer correct,
        LocalDateTime endTime,
        Long maxAnswerId
){
}
