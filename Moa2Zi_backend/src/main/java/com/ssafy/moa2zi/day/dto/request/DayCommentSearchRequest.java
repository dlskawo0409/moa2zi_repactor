package com.ssafy.moa2zi.day.dto.request;

public record DayCommentSearchRequest(
        Long parentId,
        Long next,
        int size
) {
}
