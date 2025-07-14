package com.ssafy.moa2zi.lounge.dto.response;

import java.util.List;

public record LoungeListWithNicknameResponse(
	List<LoungeSearchResponse> loungeList,
	Long total,
	Integer size,
	Boolean hasNext,
	Long next
) {
}
