package com.ssafy.moa2zi.lounge.dto.response;

import java.time.LocalDateTime;

public record LoungeWithGame(
	Long id,
	String title,
	LocalDateTime createdAt,
	LocalDateTime loungeEndTime,
	Integer duration,
	LocalDateTime gameEndTime

) {
}
