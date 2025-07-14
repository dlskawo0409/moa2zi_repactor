package com.ssafy.moa2zi.game.dto.response;

import java.time.LocalDateTime;

import com.ssafy.moa2zi.game.domain.GameStatus;
import com.ssafy.moa2zi.lounge.domain.LoungeStatus;

import lombok.Builder;

@Builder
public record GameGetResponse(
	Long gameId,

	Long loungeId,

	GameStatus gameStatus,

	Long totalMember,

	Long solvedMember,

	LocalDateTime createdAt,

	LocalDateTime endTime,

	Long nextQuizId

) {
}
