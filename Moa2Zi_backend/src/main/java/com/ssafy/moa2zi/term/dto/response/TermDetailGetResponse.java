package com.ssafy.moa2zi.term.dto.response;

import com.ssafy.moa2zi.term.domain.TermDetail;
import lombok.Builder;

import java.util.List;

@Builder
public record TermDetailGetResponse(
    String title,
    String subTitle,
    List<TermDetail> termDetailList
) {
}
