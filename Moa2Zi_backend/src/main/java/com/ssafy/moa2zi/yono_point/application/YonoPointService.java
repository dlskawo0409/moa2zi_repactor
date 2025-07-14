package com.ssafy.moa2zi.yono_point.application;

import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import com.ssafy.moa2zi.transaction.application.TransactionService;
import com.ssafy.moa2zi.yono_point.domain.YonoPointRepository;
import com.ssafy.moa2zi.yono_point.dto.response.YonoPointResponse;
import com.ssafy.moa2zi.yono_point.dto.response.YonoPointSearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class YonoPointService {

    private final YonoPointRepository yonoPointRepository;
    private final TransactionService transactionService;

    // [API] yono 포인트 가져오기 =====
    public YonoPointSearchResponse getYonoPoint(
            Integer transactionDate,
            CustomMemberDetails loginMember
    ) {
        Long memberId = loginMember.getMemberId();
        return createYonoPointSearchResponse(transactionDate, memberId);
    }

    // YonoPointSearchResponse 만들기
    private YonoPointSearchResponse createYonoPointSearchResponse(
            Integer transactionDate,
            Long memberId
    ) {
        transactionService.isValidDateFormat(transactionDate);

        Float scoreSum = 0f;
        List<YonoPointSearchResponse.dayScore> dayScoreList = new ArrayList<>();

        // transactionDate 로 받은 값을 -> 용돈 endTime 값(LocalDate) 으로 변환
        LocalDateTime monthlyEndTime = getMonthlyEndTimeByIntDate(transactionDate);

        // 그 달의 모든 날들 리스트
        List<LocalDateTime> allDaysInThisMonth = getAllDaysOfMonth(monthlyEndTime);

        // 날짜 조건에 해당하는 요노리스트 가져오기
        // memberId 이고 용돈의 endTime 이 같아야함, 즉 그달의 내 요노포인트 리스트를 가져온 것
        List<YonoPointResponse> yonoPointList = yonoPointRepository.findYonoPointListWithDateTimeFilter(monthlyEndTime, memberId);

        if(yonoPointList.size()>0) { // 요노 데이터가 있으면

            // 시작 날짜의 순번 (1일부터 시작하므로 1 빼줌)
            int startIndex = yonoPointList.get(0).createdAt().getDayOfMonth() - 2;

            // null 채우기 (용돈 시작 전)
            for (int i = 0; i < startIndex; i++) {
                dayScoreList.add(new YonoPointSearchResponse.dayScore(null, allDaysInThisMonth.get(i)));
            }

            // 공통 처리 로직
            for (YonoPointResponse yonoPointResponse : yonoPointList) {
                scoreSum += yonoPointResponse.score();
                dayScoreList.add(new YonoPointSearchResponse.dayScore(
                        yonoPointResponse.score(),
                        yonoPointResponse.createdAt().minusDays(1)
                ));
            }

            Integer sizeOfDayScoreList = dayScoreList.size();

            // 아직 도래하지 않은 날 데이터 채워넣기
            for(int i=sizeOfDayScoreList; i<allDaysInThisMonth.size(); i++) {
                dayScoreList.add(new YonoPointSearchResponse.dayScore(null, allDaysInThisMonth.get(i)));
            }

        } else { // 요노 데이터 없으면 모든 날짜에 대해 null 채우기
            for (LocalDateTime dateTime : allDaysInThisMonth) {
                dayScoreList.add( new YonoPointSearchResponse.dayScore(null, dateTime));
            }
        }

        // 월 요노 점수 평균(월점수) = 요노점수 있는날 단순합 / 요노있는날 수
        float monthScore = (float) scoreSum / yonoPointList.size();

        return new YonoPointSearchResponse(monthScore, dayScoreList);
    }

    private List<LocalDateTime> getAllDaysOfMonth(LocalDateTime dateTime) {
        Integer year = dateTime.getYear();
        Integer month = dateTime.getMonthValue();

        YearMonth yearMonth = YearMonth.of(year, month);
        Integer lengthOfMonth = yearMonth.lengthOfMonth();

        List<LocalDateTime> dateTimes = new ArrayList<>();

        for (int day = 1; day <= lengthOfMonth; day++) {
            dateTimes.add(LocalDateTime.of(year, month, day, 0, 0));
        }

        return dateTimes;
    }

    // 2025-03-31T23:59:59.999999999
    private LocalDateTime getMonthlyEndTimeByIntDate(Integer transactionDate) {
        // 연도와 월 추출
        Integer year = transactionDate / 10000;
        Integer month = (transactionDate % 10000) / 100;

        // 해당 월의 마지막 날짜와 시간 (23:59:59.999999999)
        YearMonth yearMonth = YearMonth.of(year, month);
        return yearMonth.atEndOfMonth().atTime(LocalTime.MAX);
    }

}
