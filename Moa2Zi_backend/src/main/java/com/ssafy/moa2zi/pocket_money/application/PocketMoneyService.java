package com.ssafy.moa2zi.pocket_money.application;

import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import com.ssafy.moa2zi.pocket_money.domain.PocketMoney;
import com.ssafy.moa2zi.pocket_money.domain.PocketMoneyRepository;
import com.ssafy.moa2zi.pocket_money.dto.request.PocketMoneyCreateRequest;
import com.ssafy.moa2zi.pocket_money.dto.response.PocketMoneyInfoResponse;
import com.ssafy.moa2zi.pocket_money.dto.response.PocketMoneySearchResponse;
import com.ssafy.moa2zi.transaction.application.TransactionService;
import com.ssafy.moa2zi.transaction.domain.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PocketMoneyService {

    private final PocketMoneyRepository pocketMoneyRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    // [API] 셀프 용돈 가져오기 =====
    public PocketMoneySearchResponse getPocketMoneyInfo(CustomMemberDetails loginMember) {
        Long memberId = loginMember.getMemberId();
        return createPocketMoneySearchResponse(memberId);
    }

    // [API] 셀프 용돈 설정 =====
    @Transactional
    public void setPocketMoney(PocketMoneyCreateRequest pmcr, CustomMemberDetails loginMember) {

        YearMonth nowYearMonth = getYearMonth(LocalDateTime.now());
        YearMonth nextYearMonth = getNextMonth(nowYearMonth);

        // 셀프 용돈 설정 시점 기준 다음월로 startTime, startTime 지정
        LocalDateTime startTime;
        LocalDateTime endTime;

        if(!pmcr.thisMonthHave()) { // 이번달 설정 안했으면

            startTime = LocalDateTime.now();
            endTime = getLastDateTimeOfMonth(nowYearMonth);

        } else { // 이번달 설정 되어있으면 다음달로 저장

            // 셀프 용돈 설정 시점 기준 다음월로 startTime, startTime 지정
            startTime = getFirstDateTimeOfMonth(nextYearMonth);
            // DB 에는 다음 월 시작시점 찍힘
            // 2025-04-01 00:00:00.000000
            endTime = getLastDateTimeOfMonth(nextYearMonth);

            PocketMoneyInfoResponse nextMonthTotalAmount = pocketMoneyRepository.findTotalAmountAndStartTime(
                    startTime,
                    endTime,
                    loginMember.getMemberId()
            );

            // 만약 다음달 설정 되어 있으면
            if ( nextMonthTotalAmount != null ) throw new IllegalStateException("다음 달 용돈이 이미 설정되어 있습니다.");

        }

        PocketMoney newPocketMoney = PocketMoney.builder()
                .memberId(loginMember.getMemberId())
                .totalAmount(pmcr.totalAmount())
                .startTime(startTime)
                .endTime(endTime)
                .build();

        pocketMoneyRepository.save(newPocketMoney);

    }

    // createPocketMoneySearchResponse 만들기
    private PocketMoneySearchResponse createPocketMoneySearchResponse(Long memberId) {
        LocalDateTime nowDateTime = LocalDateTime.now();

        // 현재시간으로 월간 시작과 끝 DateTime 구하기
        List<LocalDateTime> dateTimeOfMonthList = getFirstAndLastDateTimeOfMonth(nowDateTime);
        LocalDateTime firstDateTimeOfMonth = dateTimeOfMonthList.get(0); // 월간 시작 DateTime
        LocalDateTime lastDateTimeOfMonth = dateTimeOfMonthList.get(1); // 월간 끝 DateTime

        PocketMoneyInfoResponse pocketMoneyInfoResponse = pocketMoneyRepository.findTotalAmountAndStartTime(
                firstDateTimeOfMonth,
                lastDateTimeOfMonth,
                memberId
        );

        if(pocketMoneyInfoResponse == null) throw new NotFoundException("설정된 셀프 용돈 정보가 없습니다.");

        // 20250301 Integer 형식으로 날짜 구하기
        String strNowDate = nowDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        List<Integer> integerFirstAndLastDateList = transactionService.getFirstAndLastDayOfMonth(strNowDate);
        Integer integerLastDate = integerFirstAndLastDateList.get(1);

        // 용돈 시작일 LocalDateTime
        LocalDateTime dateTime = pocketMoneyInfoResponse.startTime();
        // 용돈 시작일 LocalDateTime -> Integer(yyyymmdd)로 형변환
        Integer integerPocketMoneyStartDate = Integer.parseInt(dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        Long spendSum = transactionRepository.findTransactionSpendSumOfMonth(
                integerPocketMoneyStartDate,
                integerLastDate,
                memberId
        );

        Boolean thisMonthHave = true;
        Long totalAmount;
        LocalDateTime startTime = null;

        if(pocketMoneyInfoResponse == null) { // 용돈 설정 안되어 있으면 thisMonthHave = false
            thisMonthHave = false;
            totalAmount = 0L; // 계산위해 0 으로 변환
            lastDateTimeOfMonth = null;
        } else { // null 이 아니면
            thisMonthHave = true;
            totalAmount = pocketMoneyInfoResponse.totalAmount();
            startTime = pocketMoneyInfoResponse.startTime();
        }

        if(spendSum == null) { // 계산위해 0 으로 변환
            spendSum = 0L;
        }

        Long left = totalAmount - spendSum; // null 이면 계산 못함
        Long dayCanUse = getDayCanUse(nowDateTime, left);

        return new PocketMoneySearchResponse(
                thisMonthHave,
                totalAmount,
                spendSum,
                left,
                dayCanUse,
                startTime,
                lastDateTimeOfMonth
        );
    }

    private Long getDayCanUse(LocalDateTime nowDateTime, Long left) {
        // 해당 월의 마지막 날짜 구하기
        Integer lastDayOfMonth = YearMonth.from(nowDateTime).lengthOfMonth();
        // 남은 일수 계산
        Integer remainingDays = lastDayOfMonth - nowDateTime.getDayOfMonth() +1; // 오늘 포함 +1
        if(remainingDays <= 0) throw new IllegalArgumentException("남은 일수가 올바르지 않습니다.");
        return left/remainingDays;
    }

    // 해당월의 startTime 과 endTime 모두 구하기
    private List<LocalDateTime> getFirstAndLastDateTimeOfMonth(LocalDateTime dateTime) {
        // 해당월의 시작날과 끝날 구하기
        YearMonth yearMonthOfNow = getYearMonth(dateTime);
        LocalDateTime firstDateTimeOfMonth = getFirstDateTimeOfMonth(yearMonthOfNow);
        LocalDateTime lastDateTimeOfMonth = getLastDateTimeOfMonth(yearMonthOfNow);

        // firstDateTimeOfMonth(인덱스0), lastDateTimeOfMonth(인덱스1) 담은 리스트
        List<LocalDateTime> dateTimeOfMonthList = new ArrayList<>();
        dateTimeOfMonthList.add(firstDateTimeOfMonth); // 월간 시작날,시각
        dateTimeOfMonthList.add(lastDateTimeOfMonth); // 월간 끝나는날,시각

        return dateTimeOfMonthList;
    }

    // 입력된 값 기준 다음 월 구하기
    private YearMonth getNextMonth(YearMonth yearMonth) {
        return yearMonth.plusMonths(1); // 다음 달로 변경
    }

    // 입력된 날짜의 연월 정보 가져오기
    // (출력예시) YearMonth : 2025-03
    private YearMonth getYearMonth(LocalDateTime dateTime) {
        return YearMonth.from(dateTime);
    }

    // 해당월 stratTime 구하기
    private LocalDateTime getFirstDateTimeOfMonth(YearMonth yearMonth) {
        // 해당 월의 첫 날 구하기
        // (출력예시) firstDayOfMonth : 2025-03-01
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        // startTime 설정
        return firstDayOfMonth.atTime(LocalTime.MIN);
    }

    // 해당월 endTime 구하기
    private LocalDateTime getLastDateTimeOfMonth(YearMonth yearMonth) {
        // 해당 월의 마지막 날 구하기
        // (출력예시) lastDayOfMonth : 2025-03-31
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();
        // endTime 설정 - DB 에는 다음 월 시작시점 찍힘
        return lastDayOfMonth.atTime(LocalTime.MAX);
    }
}
