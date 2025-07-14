package com.ssafy.moa2zi.game.dto.response;

import java.time.LocalDateTime;

public record GameScoreRanking(
        Long loungeId,
        String loungeName,
        Long gameId,
        Long memberId,
        LocalDateTime endTime,
        Integer ranking,
        Long totalMembers
) {}
