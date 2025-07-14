package com.ssafy.moa2zi.yono_point.domain;

import com.ssafy.moa2zi.yono_point.dto.response.YonoPointResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface YonoPointRepositoryCustom {
    List<YonoPointResponse> findYonoPointListWithDateTimeFilter(LocalDateTime monthlyEndTime, Long memberId);
    YonoPoint findYesterdayYonoPoint(LocalDateTime yesterdayStartTime, LocalDateTime yesterdayEndTime, Long memberId);
}
