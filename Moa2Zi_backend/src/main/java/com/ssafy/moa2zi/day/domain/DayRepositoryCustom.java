package com.ssafy.moa2zi.day.domain;

import com.ssafy.moa2zi.day.dto.request.DayTransactionSumGetRequest;
import com.ssafy.moa2zi.day.dto.request.SumWithCategory;

import java.util.List;

public interface DayRepositoryCustom {
    // 해당월 전체기간 가계부일자 조회 쿼리
    List<Day> findDayListInRange(Long memberId, Integer firstDay, Integer lastDay);
    List<SumWithCategory> getSumByDayGroupByCategory(
            DayTransactionSumGetRequest dayTransactionSumGetRequest,
            Long memberId
    );
}
