package com.ssafy.moa2zi.day.domain;

import com.ssafy.moa2zi.day.dto.request.DayCommentSearchRequest;
import com.ssafy.moa2zi.day.dto.response.DayCommentSearchResponse;

public interface DayCommentRepositoryCustom {

    DayCommentSearchResponse findComments(
            Long dayId,
            DayCommentSearchRequest request
    );
}
