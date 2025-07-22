package com.ssafy.moa2zi.day.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DayCommentCreateRequest(
    Long parentId,
    @NotBlank(message = "댓글 내용은 비어 있으면 안됩니다.")
    String content
) {
}
