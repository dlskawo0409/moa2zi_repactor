package com.ssafy.moa2zi.yono_point.domain;

import com.ssafy.moa2zi.yono_point.dto.response.YonoPointResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface YonoPointRepository extends JpaRepository<YonoPoint, Long>, YonoPointRepositoryCustom {
    List<YonoPointResponse> findYonoPointListWithDateTimeFilter(LocalDateTime monthlyEndDateTime, Long memberId);
    YonoPoint findYesterdayYonoPoint(LocalDateTime yesterdayStartTime, LocalDateTime yesterdayEndTime, Long memberId);
}
