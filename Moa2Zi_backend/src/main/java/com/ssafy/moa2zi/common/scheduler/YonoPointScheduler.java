package com.ssafy.moa2zi.common.scheduler;

import com.ssafy.moa2zi.pocket_money.domain.PocketMoney;
import com.ssafy.moa2zi.pocket_money.domain.PocketMoneyRepository;
import com.ssafy.moa2zi.transaction.domain.TransactionRepository;
import com.ssafy.moa2zi.yono_point.domain.YonoPoint;
import com.ssafy.moa2zi.yono_point.domain.YonoPointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class YonoPointScheduler {

    private final TransactionRepository transactionsRepository;
    private final PocketMoneyRepository pocketMoneyRepository;
    private final YonoPointRepository yonoPointRepository;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 실행
    public void batchYonoPoint() {

        // "어제" 날짜뽑기(Integer)
        // 2025년 4월 4일 00시 13분 -> 20250403
        Integer intYesterdayDate = intYesterdayDate();

        // "어제" 년월 정보 가져오기
        YearMonth yesterdayYearMonth = getYearMonth(LocalDateTime.now().minusDays(1));
        // Integer howManyDaysInThisMonth = yesterdayYearMonth.lengthOfMonth(); // 어제 년월에 속한 날짜 수

        // "어제" 년월의 첫날, 마지막 찰나시점 구하기
        LocalDateTime yesterdayStartTime = getStartDateTimeOfMonth(yesterdayYearMonth);
        LocalDateTime yesterdayEndTime = getLastDateTimeOfMonth(yesterdayYearMonth); // 2025-04-04 23:59:59999

        List<PocketMoney> pocketMoneyList = pocketMoneyRepository.findByEndTime(yesterdayEndTime);

        if(pocketMoneyList.size() == 0) { // 아무런 용돈 데이터 없으면 배치종료
            return;
        }

        // 요노 계산하려고 pocketMoney 순회
        for (PocketMoney pocketMoney : pocketMoneyList) { // 모든 사람의 pocketMoney 순회
            Long deposit = pocketMoney.getTotalAmount(); // 이건 한사람의 deposit
            Long memberId = pocketMoney.getMemberId(); // 이건 한사람의 memberId

            // 용돈 시작 시점 타입 변환 : LocalDateTime -> 20250404 -> Integer
            LocalDateTime startTime = pocketMoney.getStartTime();
            Integer pocketMoneyStartTime = Integer.parseInt(startTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

            // pocketMoney 의 시작날 포함한 그달에 남은 일수
            Long remainingDaysCount = ChronoUnit.DAYS.between(startTime.toLocalDate(), yesterdayEndTime.toLocalDate()) + 1;
            if(remainingDaysCount <= 0) continue;

            // 용돈 시작일부터 배치 기준 전날까지 누적 소비량
            Long accumulatedSpendSum = getAccumulatedSpendSum(
                    memberId,
                    pocketMoneyStartTime,
                    intYesterdayDate
            );

            // 하루 소비 가능량
            double canDailySpendAmount = (double) (deposit - accumulatedSpendSum) / remainingDaysCount;

            // 어제 하루 소비량
            Long yesterdaySpendSum = getYesterdaySpendSum(memberId, intYesterdayDate);

            // canDailySpendAmount 가 0일 경우에 아주 작은 양수로 대체(나눠야 하니깐)
            if(canDailySpendAmount == 0) canDailySpendAmount = Double.MIN_VALUE;

            // yonoPointParmeter(x) = 어제하루소비량 / 하루 소비 가능량
            double yonoPointParmeter = (double) yesterdaySpendSum / canDailySpendAmount;
            float yonoPoint = calculateYonoPoint(yonoPointParmeter);

            YonoPoint yesterdayYonoPoint = yonoPointRepository.findYesterdayYonoPoint(yesterdayStartTime, yesterdayEndTime, memberId);

            if(yesterdayYonoPoint == null) { // 이번 순회에 해당하는 멤버가 어제 저장한 요노가 없으면
                YonoPoint newYonoPoint = YonoPoint.builder()
                        .pocketMoneyId(pocketMoney.getId())
                        .score(yonoPoint)
                        .build();

                yonoPointRepository.save(newYonoPoint);
            }

        }
    }

    private float calculateYonoPoint(double yonoPointParmeter) {
        double yonoPointTemp;

        if( yonoPointParmeter < 0 ) {
            yonoPointTemp = Math.exp(yonoPointParmeter) - 1;
        } else if ( 0 <= yonoPointParmeter && yonoPointParmeter < 1) {
            yonoPointTemp = 1;
        } else if ( 1<= yonoPointParmeter && yonoPointParmeter < 2) {
            yonoPointTemp = 2 - yonoPointParmeter;
        } else {
            yonoPointTemp = 0;
        }
        return (float) yonoPointTemp * 100;
    }

    private Long getYesterdaySpendSum(Long memberId, Integer intYesterdayDate) {
        Long yesterdaySpendSum = transactionsRepository.findYesterdaySpendSum(
                memberId,
                intYesterdayDate
        );
        if(yesterdaySpendSum == null) yesterdaySpendSum = 0L;
        return yesterdaySpendSum;
    }

    private Long getAccumulatedSpendSum(Long memberId, Integer pocketMoneyStartTime, Integer intYesterdayDate) {
        Long accumulatedSpendSum = transactionsRepository.findAccumulatedSpendSum(
                memberId,
                pocketMoneyStartTime,
                intYesterdayDate
        );
        if(accumulatedSpendSum == null) accumulatedSpendSum = 0L;
        return accumulatedSpendSum;
    }

    // 입력된 날짜의 연월 정보 가져오기
    // (출력예시) YearMonth : 2025-03
    private YearMonth getYearMonth(LocalDateTime dateTime) {
        return YearMonth.from(dateTime);
    }

    private LocalDateTime getStartDateTimeOfMonth(YearMonth yearMonth) {
        // 해당 월의 첫 날의 00:00:00
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        return firstDayOfMonth.atStartOfDay(); // 2025-03-01T00:00
    }

    // 해당월 endTime 구하기
    private LocalDateTime getLastDateTimeOfMonth(YearMonth yearMonth) {
        // 해당 월의 마지막 날 구하기
        // (출력예시) lastDayOfMonth : 2025-03-31
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        // endTime 설정 - DB 에는 다음 월 시작시점 찍힘
        return lastDayOfMonth.atTime(LocalTime.MAX);
    }

    private Integer intYesterdayDate() {
        return Integer.parseInt(LocalDateTime.now()
                .minusDays(1)
                .format(DateTimeFormatter.ofPattern("yyyyMMdd")));
    }

}
