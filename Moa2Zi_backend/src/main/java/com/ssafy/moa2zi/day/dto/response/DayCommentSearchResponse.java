package com.ssafy.moa2zi.day.dto.response;

import java.util.List;

public record DayCommentSearchResponse(
        List<DayCommentInfoResponse> commentList,
        Long total,
        int size,
        boolean hasNext,
        Long next
) {
}
