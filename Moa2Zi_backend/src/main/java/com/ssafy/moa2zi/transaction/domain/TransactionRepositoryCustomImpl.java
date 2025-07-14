package com.ssafy.moa2zi.transaction.domain;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.moa2zi.category.domain.Category;
import com.ssafy.moa2zi.category.domain.CategoryRepository;
import com.ssafy.moa2zi.category.domain.QCategory;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import com.ssafy.moa2zi.merchant.domain.Merchant;
import com.ssafy.moa2zi.transaction.dto.request.AnalysisByCategoryListSearchRequest;
import com.ssafy.moa2zi.transaction.dto.request.MonthlySpendSumListSearchRequest;
import com.ssafy.moa2zi.transaction.dto.request.TransactionSearchRequest;
import com.ssafy.moa2zi.transaction.dto.response.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import java.time.LocalDateTime;
import java.util.*;

import static com.ssafy.moa2zi.day.domain.QDay.day;
import static com.ssafy.moa2zi.member.domain.QMember.member;
import static com.ssafy.moa2zi.transaction.domain.QTransaction.transaction;
import static com.ssafy.moa2zi.transaction.domain.TransactionType.SPEND;

import com.ssafy.moa2zi.transaction.dto.request.TransactionListSearchRequest;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TransactionRepositoryCustomImpl implements TransactionRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final CategoryRepository categoryRepository;

    QCategory category = new QCategory("category");
    QCategory parentCategory = new QCategory("parentCategory");

    // 배치 동작 기준 전날 하루 지출량 합
    @Override
    public Long findYesterdaySpendSum(
            Long memberId,
            Integer intYesterdayDate
    ) {
        return queryFactory
                .select(transaction.balance.sum())
                .from(transaction)
                .join(day).on(transaction.dayId.eq(day.id))
                .where(
                        transaction.memberId.eq(memberId),
                        day.transactionDate.eq(intYesterdayDate),
                        transaction.transactionType.eq(TransactionType.SPEND)
                )
                .fetchOne();
    }

    // 용돈 시작일 ~ 배치 기준 전날 마지막 찰나까지의 지출 누적금액
    @Override
    public Long findAccumulatedSpendSum(
            Long memberId,
            Integer pocketMoneyStartTime,
            Integer intYesterdayDate
    ) {

            return queryFactory
                    .select(transaction.balance.sum())
                    .from(transaction)
                    .join(day).on(transaction.dayId.eq(day.id))
                    .where(
                            transaction.memberId.eq(memberId),
                            transaction.transactionType.eq(TransactionType.SPEND),
                            transaction.isInBudget.eq(true),
                            day.transactionDate.goe(pocketMoneyStartTime),
                            day.transactionDate.loe(intYesterdayDate)
                    )
                    .fetchOne();
    }



    @Override
    public List<AnalysisByCategorySearchResponse> findAnalysisByCategoryList(
            AnalysisByCategoryListSearchRequest abclsr,
            Long memberId
    ) {

        // 입력받은 transactionDate
        Integer inputTransactionDate = abclsr.transactionDate(); // YYYYMM00, YYYYMMDD
        isValidDateFormat(inputTransactionDate); // YYYYMMDD 형식검증

        // 단위(월인지 일인지) 크기 만큼의 과거 데이터 수
        Integer unitCount = abclsr.unitCount();
        if (unitCount < 1) throw new IllegalArgumentException("unitCount 는 1 이상이어야 합니다.");

        // 분석 그래프에 보여줄 분석단위 수
        Integer unitRankCount = abclsr.unitRankCount();
        if (unitRankCount <= 0) throw new IllegalArgumentException("unitRankCount 는 1 이상이어야 합니다.");

        String strDate = inputTransactionDate.toString(); // 날짜 String 으로 변환
        Boolean isEndsWithZero = false; // transactionDate 가 00 으로 끝나는지 여부

        if(strDate.endsWith("00")) { // 00 으로 끝나면

            isEndsWithZero = true;

            // startDay : strDate 20250200 이고 unitCount 3 이면 20241201 찍힘
            Integer startDay = getStartDay(strDate, unitCount, isEndsWithZero); // 20241201
            Integer endDay = getEndDay(strDate, isEndsWithZero); // 20250228

            List<AnalysisByCategorySearchResponse> tempList = getAnalysisByCategoryTempList(memberId, startDay, endDay, abclsr);

            return getAnalysisByCategoryFormmatedList(tempList, unitRankCount);

        } else { // YYYYMMDD 면

            // startDay : strDate 20241201 이고 unitCount 6 이면 20251206 찍힘
            Integer startDay = getStartDay(strDate, unitCount, isEndsWithZero); // 20241201
            Integer endDay = getEndDay(strDate, isEndsWithZero); // 20251206

            List<AnalysisByCategorySearchResponse> tempList = getAnalysisByCategoryTempList(memberId, startDay, endDay, abclsr);
            return getAnalysisByCategoryFormmatedList(tempList, unitRankCount);

        }

    }

    // 한달 내 거래 내역이 10개 이상 있는 멤버 조회
    @Override
    public Set<Long> findMembersSpendingCountOver(Integer dateTimeInt, int spendingCount) {

        List<Long> memberIds = queryFactory
                .select(transaction.memberId)
                .from(transaction)
                .join(day).on(transaction.dayId.eq(day.id))
                .join(member).on(transaction.memberId.eq(member.memberId))
                .where(
                        day.transactionDate.goe(dateTimeInt),
                        transaction.transactionType.eq(SPEND)
                )
                .groupBy(transaction.memberId)
                .having(transaction.count().goe(spendingCount))
                .fetch();

        return new HashSet<>(memberIds);
    }

    @Override
    public List<Transaction> findByMemberIdsAndDateOrderByBalance(Set<Long> memberIds, Integer dateTimeInt) {
        List<Transaction> transactions = queryFactory
                .select(transaction)
                .from(transaction)
                .join(day).on(day.id.eq(transaction.dayId))
                .where(
                        transaction.memberId.in(memberIds),
                        transaction.coordinate.isNotNull(),
                        transaction.transactionType.eq(SPEND),
                        day.transactionDate.goe(dateTimeInt)
                )
                .orderBy(
                        transaction.memberId.asc(),
                        transaction.balance.desc() // 지출 금액 내림차순
                )
                .fetch();

        return transactions;
    }

    @Override
    public void updateLocationFields(Long transactionId, Merchant merchant) {

        queryFactory.update(transaction)
                .set(transaction.merchantId, merchant.getId())
                .set(transaction.sidoCode, merchant.getSidoCode())
                .set(transaction.gugunCode, merchant.getGugunCode())
                .set(transaction.dongCode, merchant.getDongCode())
                .set(transaction.jibunAddress, merchant.getJibunAddress())
                .set(transaction.coordinate, merchant.getCoordinate())
                .set(transaction.geohashCode, merchant.getGeohashCode())
                .where(transaction.transactionId.eq(transactionId))
                .execute();
    }

    private List<AnalysisByCategorySearchResponse> getAnalysisByCategoryFormmatedList(
            List<AnalysisByCategorySearchResponse> tempList,
            Integer unitRankCount
    ) {

        // 초기화
        Long totalSum = 0L; // 총 합계(퍼센트 구하기 위함)
        List<AnalysisByCategorySearchResponse> resultList = new ArrayList<>(); // 퍼센트 계산된 dto

        for(AnalysisByCategorySearchResponse temp : tempList) {
            totalSum += temp.sum(); // 각 카테고리의 모든 sum 들의 총합
        }

        String restCategoryName = "기타";
        Integer restPercent = 0;
        Long restSum = 0L;

        if(tempList.size() == 0) { // 분석된 카테고리가 없을 경우

            return resultList; // 빈배열 반환

        } else if (tempList.size() == 1) { // 분석된 카테고리가 1개뿐일 경우

            if(tempList.size() < unitRankCount) unitRankCount = tempList.size();

            AnalysisByCategorySearchResponse temp = tempList.get(0);
            Integer percent = getPercent(temp.sum(), totalSum);

            Long nowParentId = temp.parentId();

            Long responseCategoryId = temp.categoryId();
            Long responseParentId = temp.parentId();
            String responseCategoryName = temp.categoryName();

            while(nowParentId != null) {
                Category tempCategory = getCategory(nowParentId);
                responseCategoryId = tempCategory.getId();
                responseParentId = tempCategory.getParentId();
                responseCategoryName = tempCategory.getCategoryName();
                nowParentId = tempCategory.getParentId();
            }

            resultList.add(
                    new AnalysisByCategorySearchResponse(
                            responseCategoryId,
                            responseParentId,
                            responseCategoryName,
                            percent,
                            temp.sum()
                    )
            );

            return resultList;

        } else { // 분석된 카테고리가 여러개일 경우

            if(tempList.size() < unitRankCount) unitRankCount = tempList.size();

            addRankedCategoryTypeAnalysis(
                    unitRankCount,
                    tempList,
                    resultList,
                    totalSum,
                    restPercent,
                    restSum
            );

            addRestCategoryTypeAnalysis(
                    unitRankCount,
                    tempList,
                    resultList,
                    totalSum,
                    restPercent,
                    restSum,
                    restCategoryName
            );

            return resultList;

        }

    }

    // 랭킹만 우선 구하는 순회
    private void addRankedCategoryTypeAnalysis(
            Integer unitRankCount,
            List<AnalysisByCategorySearchResponse> tempList,
            List<AnalysisByCategorySearchResponse> resultList,
            Long totalSum,
            Integer restPercent,
            Long restSum
    ) {
        for(int i=0; i<unitRankCount-1; i++) { // 기타 제외하고 (unitRankCount-1) 개가 랭커

            AnalysisByCategorySearchResponse temp = tempList.get(i);
            Integer percent = getPercent(temp.sum(), totalSum);

            // 반올림 된 percent 끼리 더해서 퍼센트가 100 초과로 나오는 경우 방지
            if(percent >= 100) percent = 100;

            Long nowParentId = temp.parentId();

            Long responseCategoryId = temp.categoryId();
            Long responseParentId = temp.parentId();
            String responseCategoryName = temp.categoryName();

            while(nowParentId != null) {
                Category tempCategory = getCategory(nowParentId);
                responseCategoryId = tempCategory.getId();
                responseParentId = tempCategory.getParentId();
                responseCategoryName = tempCategory.getCategoryName();
                nowParentId = tempCategory.getParentId();
            }

            if(temp.categoryId() == null) { // null 이면 기타로 빼기 및 index 조정
                restPercent += percent;
                restSum += temp.sum();
                unitRankCount++; // null 의 경우엔 unitRankCount 증감에서 제외시키기 위해 +1

            } else { // null 아니면 바로 넣기
                resultList.add(
                        new AnalysisByCategorySearchResponse(
                                responseCategoryId,
                                responseParentId,
                                responseCategoryName,
                                percent,
                                temp.sum()
                        )
                );
            }
        }
    }

    // 랭킹 count 에 들지 못한 나머지들 기타로 연산
    private void addRestCategoryTypeAnalysis(
            Integer unitRankCount,
            List<AnalysisByCategorySearchResponse> tempList,
            List<AnalysisByCategorySearchResponse> resultList,
            Long totalSum,
            Integer restPercent,
            Long restSum,
            String restCategoryName
    ) {
        for(int i=unitRankCount-1; i<tempList.size(); i++) {
            restSum += tempList.get(i).sum();
            Integer percent = getPercent(tempList.get(i).sum(), totalSum);
            restPercent += percent;
        }

        // 반올림 된 percent 끼리 더해서 퍼센트가 100 초과로 나오는 경우 방지
        if(restPercent >= 100) restPercent = 100;

        resultList.add(
                new AnalysisByCategorySearchResponse(
                        null,
                        null,
                        restCategoryName,
                        restPercent,
                        restSum
                )
        );
    }

    private Integer getPercent(Long target, Long total) {
        if (total <= 0) return 0;
        return (int) Math.round((target * 100.0) / total); // 반올림
    }

    private List<AnalysisByCategorySearchResponse> getAnalysisByCategoryTempList(
            Long memberId,
            Integer startDay,
            Integer endDay,
            AnalysisByCategoryListSearchRequest abclsr
    ) {



        return queryFactory
                .select(
                        Projections.constructor(
                                AnalysisByCategorySearchResponse.class,
                                category.id, // 최상위 카테고리 ID (transaction.categoryId의 부모 ID)
                                category.parentId,
                                category.categoryName, // 최상위 카테고리 이름 (수정된 부분)
                                ConstantImpl.create(0), // percent를 0으로 고정, 뒷 로직에서 계산할 것
                                transaction.balance.sum() // 소비금액의 합
                        )
                )
                .from(transaction)
                .join(day).on(day.id.eq(transaction.dayId))
                .leftJoin(category).on(category.id.eq(transaction.categoryId)) // 카테고리 ID 기준으로 연결
                .leftJoin(parentCategory).on(category.parentId.eq(parentCategory.id)) // 부모 카테고리 조회 추가
                .where(
                        transaction.memberId.eq(memberId),
                        transaction.transactionType.eq(TransactionType.SPEND), // 소비만
                        inTransactionDateRange(startDay, endDay),
                        eqAccountNo(abclsr.accountNo()),
                        eqCardNo(abclsr.cardNo()),
                        eqPaymentMethod(abclsr.paymentMethod()),
                        eqEmotion(abclsr.emotion()),
                        containsMemo(abclsr.memo())
                )
                .groupBy(category.id) // 최상위 카테고리 기준으로 그룹핑
                .orderBy(
                        abclsr.isDescending()
                                ? transaction.balance.sum().desc()
                                : transaction.balance.sum().asc()
                )
                .fetch();

    }


    @Override
    public List<MonthlySpendSumResponse> findMonthlySpendSumList(
            MonthlySpendSumListSearchRequest msslsr,
            Long memberId
    ) {

        // 입력받은 transactionDate
        Integer inputTransactionDate = msslsr.transactionDate(); // YYYYMM00, YYYYMMDD
        isValidDateFormat(inputTransactionDate); // YYYYMMDD 형식검증

        Integer unitCount = msslsr.unitCount(); // 단위(월인지 일인지) 크기 만큼의 과거 데이터 수
        if (unitCount < 1) throw new IllegalArgumentException("unitCount 는 1 이상이어야 합니다.");

        String strDate = inputTransactionDate.toString(); // 날짜 String 으로 변환
        Boolean isEndsWithZero = false; // transactionDate 가 00 으로 끝나는지 여부

        if(strDate.endsWith("00")) { // 00 으로 끝나면

            isEndsWithZero = true;

            // startDay : strDate 20250200 이고 unitCount 3 이면 20241201 찍힘
            Integer startDay = getStartDay(strDate, unitCount, isEndsWithZero); // 20241201
            Integer endDay = getEndDay(strDate, isEndsWithZero); // 20250228

            List<MonthlySpendSumResponse> tempList = queryFactory
                    .select(
                            Projections.constructor(
                                    MonthlySpendSumResponse.class,
                                    day.transactionDate.divide(100), // YYYYMM 형태로 변환
                                    new CaseBuilder()
                                            .when(transaction.transactionType.eq(SPEND))
                                            .then(transaction.balance.multiply(-1))
                                            .otherwise(transaction.balance)
                                            .sum() // 해당 월의 balance 합계
                            )
                    )
                    .from(transaction)
                    .join(day).on(day.id.eq(transaction.dayId))
                    .where(
                            transaction.memberId.eq(memberId),
                            inTransactionDateRange(startDay, endDay),
                            eqTransactionType(msslsr.transactionType()),
                            eqCategory(msslsr.categoryId()),
                            eqAccountNo(msslsr.accountNo()),
                            eqCardNo(msslsr.cardNo()),
                            eqPaymentMethod(msslsr.paymentMethod()),
                            eqEmotion(msslsr.emotion()),
                            containsMemo(msslsr.memo())
                    )
                    .groupBy(day.transactionDate) // 같은 월별로 그룹핑
                    .orderBy(
                            msslsr.isDescending()
                                    ? day.transactionDate.divide(100).desc()
                                    : day.transactionDate.divide(100).asc()
                    )
                    .fetch();

            return createFormatDtoWhenYYYYMM(tempList, startDay, endDay, msslsr);

        } else { // YYYYMMDD 면

            // startDay : strDate 20241201 이고 unitCount 6 이면 20251206 찍힘
            Integer startDay = getStartDay(strDate, unitCount, isEndsWithZero); // 20241201
            Integer endDay = getEndDay(strDate, isEndsWithZero); // 20251206

            List<MonthlySpendSumResponse> tempList = queryFactory
                    .select(
                            Projections.constructor(
                                    MonthlySpendSumResponse.class,
                                    day.transactionDate,
                                    new CaseBuilder()
                                            .when(transaction.transactionType.eq(SPEND))
                                            .then(transaction.balance.multiply(-1))
                                            .otherwise(transaction.balance)
                                            .sum() // 해당 월의 balance 합계
                            )
                    )
                    .from(transaction)
                    .join(day).on(day.id.eq(transaction.dayId))
                    .where(
                            transaction.memberId.eq(memberId),
                            inTransactionDateRange(startDay, endDay),
                            eqTransactionType(msslsr.transactionType()),
                            eqCategory(msslsr.categoryId()),
                            eqAccountNo(msslsr.accountNo()),
                            eqCardNo(msslsr.cardNo()),
                            eqPaymentMethod(msslsr.paymentMethod()),
                            eqEmotion(msslsr.emotion()),
                            containsMemo(msslsr.memo())
                    )
                    .groupBy(day.transactionDate) // 같은 월별로 그룹핑
                    .orderBy(
                            msslsr.isDescending()
                                    ? day.transactionDate.desc()
                                    : day.transactionDate.asc()
                    )
                    .fetch();

            return createFormatDtoWhenYYYYMMDD(tempList, startDay, endDay, msslsr);

        }

    }

    private List<MonthlySpendSumResponse> createFormatDtoWhenYYYYMM(
            List<MonthlySpendSumResponse> tempList,
            Integer startDay,
            Integer endDay,
            MonthlySpendSumListSearchRequest msslsr
    ) {
        // 가공 : 그룹핑, 데이터 없는날도 데이터넣어주기
        List<MonthlySpendSumResponse> resultList = new ArrayList<>();
        List<Integer> dateCheckList = getYearMonthDateCheckList(startDay, endDay); // [202512, 202601, 202602, ..]
        if(msslsr.isDescending()) { // 내림차순 조건일 때 내림차순으로 정렬
            Collections.sort(dateCheckList, Collections.reverseOrder());
        }

        Integer loopStartCnt = 0;
        outerLoop : for(Integer dateCheck : dateCheckList) { // 202412
            Long sum = 0L;

            for(int i=loopStartCnt; i<tempList.size(); i++) {
                if(tempList.get(i).transactionDate().equals(dateCheck)) {
                    loopStartCnt++;
                    sum += tempList.get(i).sum();
                } else {
                    MonthlySpendSumResponse dto = new MonthlySpendSumResponse(dateCheck*100, sum);
                    resultList.add(dto);
                    continue outerLoop;
                }
            }

            MonthlySpendSumResponse dto = new MonthlySpendSumResponse(dateCheck*100, sum);
            resultList.add(dto);
        }

        return resultList;
    }

    private List<MonthlySpendSumResponse> createFormatDtoWhenYYYYMMDD(
            List<MonthlySpendSumResponse> tempList,
            Integer startDay,
            Integer endDay,
            MonthlySpendSumListSearchRequest msslsr
    ) {
        // 가공 : 데이터 없는날도 데이터넣어주기 (일별 계산의 경우 그룹핑은 쿼리DSL 로직에서 완료되어 있음)
        List<MonthlySpendSumResponse> resultList = new ArrayList<>();
        List<Integer> dateCheckList = getDayDateCheckList(startDay, endDay); // [20241230, 20241231, 20250101, 20250102..]
        if(msslsr.isDescending()) { // 내림차순 조건일 때 내림차순으로 정렬
            Collections.sort(dateCheckList, Collections.reverseOrder());
        }

        Integer loopStartCnt = 0;
        outerLoop : for(Integer dateCheck : dateCheckList) { // 20241230
            Long sum = 0L;

            for(int i=loopStartCnt; i<tempList.size(); i++) {
                if(tempList.get(i).transactionDate().equals(dateCheck)) {
                    loopStartCnt++;
                    sum += tempList.get(i).sum();
                } else {
                    MonthlySpendSumResponse dto = new MonthlySpendSumResponse(dateCheck, sum);
                    resultList.add(dto);
                    continue outerLoop;
                }
            }

            MonthlySpendSumResponse dto = new MonthlySpendSumResponse(dateCheck, sum);
            resultList.add(dto);
        }

        return resultList;
    }

    private List<Integer> getDayDateCheckList(Integer startDay, Integer endDay) {
        List<Integer> result = new ArrayList<>();

        // YYYYMMDD -> LocalDate 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate startDate = LocalDate.parse(startDay.toString(), formatter);
        LocalDate endDate = LocalDate.parse(endDay.toString(), formatter);

        // startDate부터 endDate까지 하루씩 증가하면서 리스트에 추가
        while (!startDate.isAfter(endDate)) {
            result.add(Integer.parseInt(startDate.format(formatter))); // YYYYMMDD 형태로 추가
            startDate = startDate.plusDays(1); // 하루 증가
        }

        return result;
    }

    private List<Integer> getYearMonthDateCheckList(Integer startDay, Integer endDay) {
        List<Integer> result = new ArrayList<>();

        // YYYYMM 변환
        Integer startYearMonth = startDay / 100;
        Integer endYearMonth = endDay / 100;

        // 연월을 한 달씩 증가시키면서 리스트에 추가
        while (startYearMonth <= endYearMonth) {

            result.add(startYearMonth);

            // 다음 달로 이동
            Integer year = startYearMonth / 100;
            Integer month = startYearMonth % 100;

            if (month == 12) { // 12월이면 년월 +1씩
                year++;
                month = 1;
            } else {
                month++;
            }

            startYearMonth = year * 100 + month;
        }

        return result;
    }

    private Integer getStartDay(String strDate, Integer unitCount, Boolean isEndsWithZero) {
        if(isEndsWithZero) { // 00 으로 끝나면
            String yearMonth = strDate.substring(0, 6) + "01"; // "20250201"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate date = LocalDate.parse(yearMonth, formatter); // 2025-02-01
            LocalDate result = date.minusMonths(unitCount-1); // 2025-02-01  // 당월 포함
            return Integer.parseInt(result.format(DateTimeFormatter.ofPattern("yyyyMMdd"))); // 20250201
        } else {
            // unitCount 가 6일 때로 가정
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate date = LocalDate.parse(strDate, formatter); // "20250201" => 2025-02-01
            LocalDate result = date.minusDays(unitCount-1); // 2025-01-27 // 당일 포함
            return Integer.parseInt(result.format(formatter)); // 20250127
        }
    }

    private Integer getEndDay(String strDate, Boolean isEndsWithZero) {
        if (isEndsWithZero) { // 00 으로 끝나면
            String yearMonth = strDate.substring(0, 6); // "20250201"
            LocalDate firstDayOfMonth = LocalDate.parse(yearMonth + "01", DateTimeFormatter.ofPattern("yyyyMMdd")); // 2025-02-01
            LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth()); // 2025-02-28
            return Integer.parseInt(lastDayOfMonth.format(DateTimeFormatter.ofPattern("yyyyMMdd"))); // 20250228
        } else { // dd 로 끝나면
            return Integer.parseInt(strDate);
        }
    }

    private void isValidDateFormat(Integer transactionDate) {
        // 1900년대부터 2000년대까지 YYYYMMDD 형식 유효성 검사
        if (!transactionDate.toString().matches("^(19|20)\\d{6}$")) {
            throw new IllegalArgumentException("transactionDate 는 YYYYMMDD 형식이어야 합니다.");
        }
    }

    @Override
    public Long findTransactionSpendSumOfMonth(
            Integer integerPocketMoneyStartDate,
            Integer integerLastDate,
            Long memberId
    ) {

        Long spendSum = queryFactory
                .select(transaction.balance.sum())
                .from(transaction)
                .join(day).on(day.id.eq(transaction.dayId))
                .where(
                        transaction.memberId.eq(memberId),
                        transaction.transactionType.eq(TransactionType.SPEND),
                        transaction.isInBudget.eq(true),
                        day.transactionDate.goe(integerPocketMoneyStartDate),
                        day.transactionDate.loe(integerLastDate)
                ).fetchOne();

        return spendSum;
    }

    @Override
    public List<String> findTopSpendingCategoriesByMemberId(Long memberId, int limit) {

        return queryFactory
                .select(category.categoryName)
                .from(transaction)
                .join(category).on(category.id.eq(transaction.categoryId))
                .where(transaction.memberId.eq(memberId))
                .groupBy(transaction.categoryId)
                .orderBy(transaction.balance.sum().desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public TransactionSearchResponse findTransactions(
            TransactionSearchRequest request,
            CustomMemberDetails loginMember
    ) {

        List<TransactionInfoResponse> transactionList = queryFactory
                .select(
                        Projections.constructor(
                                TransactionInfoResponse.class,
                                transaction.transactionId,
                                day.transactionDate,
                                transaction.transactionTime,
                                transaction.balance,
                                transaction.paymentMethod,
                                transaction.emotion,
                                transaction.merchantName,
                                transaction.categoryId,
                                Expressions.numberTemplate(Double.class, "ST_X({0})", transaction.coordinate),
                                Expressions.numberTemplate(Double.class, "ST_Y({0})", transaction.coordinate)
                        )
                )
                .from(transaction)
                .join(day).on(transaction.dayId.eq(day.id))
                .where(
                        transaction.transactionId.goe(request.next()),
                        transaction.memberId.eq(loginMember.getMemberId()),
                        eqGeohashCode(request.geohashCode()),
                        eqCategory(request.categoryId()),
                        inTransactionDateRange(request.startDate(), request.endDate()),
                        containsKeyword(request.keyword())
                )
                .orderBy(transaction.transactionId.asc())
                .limit(request.size()+1)
                .fetch();

        Long total = queryFactory
                .select(transaction.count())
                .from(transaction)
                .join(day).on(transaction.dayId.eq(day.id))
                .where(
                        transaction.memberId.eq(loginMember.getMemberId()),
                        eqGeohashCode(request.geohashCode()),
                        eqCategory(request.categoryId()),
                        inTransactionDateRange(request.startDate(), request.endDate()),
                        containsKeyword(request.keyword())
                )
                .fetchOne();

        int size = Math.min(transactionList.size(), request.size());
        boolean hasNext = transactionList.size() > request.size();
        Long next = (hasNext) ? transactionList.get(transactionList.size() - 1).transactionId() : null;
        List<TransactionInfoResponse> content = (hasNext) ? transactionList.subList(0, request.size()) : transactionList;

        return new TransactionSearchResponse(content, total, size, hasNext, next);
    }

    @Override
    public Map<Long, Long> findTotalSpendingBetween(
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {

        List<Tuple> oneMonthTotalSpendingList = queryFactory
                .select(transaction.memberId, transaction.balance.sum())
                .from(transaction)
                .where(
                        transaction.createdAt.goe(startTime),
                        transaction.createdAt.lt(endTime),
                        transaction.transactionType.eq(SPEND)
                )
                .groupBy(transaction.memberId)
                .fetch();

        return oneMonthTotalSpendingList.stream()
                .collect(Collectors.toMap(
                    tuple -> tuple.get(transaction.memberId),
                    tuple -> Optional.ofNullable(tuple.get(transaction.balance.sum())).orElse(0L)
            ));
    }

    @Override
    public List<Transaction> findSpendingWithinRadiusAndDate(
            double latitude,
            double longitude,
            Long memberId,
            int startDateTimeInt,
            int endDateTimeInt
    ) {

        final int radiusMeters = 200;

        return queryFactory
                .select(transaction)
                .from(transaction)
                .join(day).on(day.id.eq(transaction.dayId))
                .where(
                        transaction.memberId.eq(memberId),
                        withinRadius(latitude, longitude, radiusMeters),
                        day.transactionDate.goe(startDateTimeInt).and(day.transactionDate.lt(endDateTimeInt)),
                        transaction.transactionType.eq(SPEND)
                )
                .fetch();
    }

    private BooleanTemplate withinRadius(double latitude, double longitude, int radiusMeters) {
        String target = "Point(%f %f)".formatted(latitude, longitude);
        String geoFunction = "ST_Distance_Sphere(ST_GeomFromText('%s', 4326), coordinate) <= {0}" ;
        String expression = String.format(geoFunction, target);

        return Expressions.booleanTemplate(expression, radiusMeters);
    }

    private BooleanExpression containsKeyword(String keyword) {
        if(Objects.isNull(keyword)) {
            return null;
        }

        return transaction.merchantName.contains(keyword);
    }

    private BooleanExpression inTransactionDateRange(Integer startDate, Integer endDate) {
        if(Objects.isNull(startDate) || Objects.isNull(endDate)) {
            return null;
        }
        return day.transactionDate.between(startDate, endDate);
    }

    private BooleanExpression eqGeohashCode(String geohashCode) {
        if(Objects.isNull(geohashCode)) {
            return null;
        }

        return transaction.geohashCode.startsWith(geohashCode);
    }

    private BooleanExpression eqCategory(Long categoryId) {
        if(Objects.isNull(categoryId)) {
            return null;
        }

        return transaction.categoryId.eq(categoryId);
    }

    @Override
    public List<Transaction> findTransactionListByDayId(Long dayId) {
        QTransaction transaction = QTransaction.transaction;

        BooleanExpression predicate = transaction.dayId.eq(dayId);

        return queryFactory
                .selectFrom(transaction)
                .where(predicate)
                .fetch();
    }

    @Override
    public List<Transaction> findTransactionListWithFilters(Long memberId, Long dayId, TransactionListSearchRequest tlsr) {

        return queryFactory
                .selectFrom(transaction)
                .where(
                        transaction.memberId.eq(memberId),
                        transaction.dayId.eq(dayId),
                        eqAccountNo(tlsr.accountNo()),
                        eqCardNo(tlsr.cardNo()),
                        eqTransactionType(tlsr.transactionType()),
                        eqPaymentMethod(tlsr.paymentMethod()),
                        eqEmotion(tlsr.emotion()),
                        containsMemo(tlsr.memo()),
                        containsMerchantName(tlsr.merchantName()),
                        eqTransactionTime(tlsr.transactionTime())
                )
                .fetch();
    }

    private BooleanExpression eqAccountNo(String accountNo) {
        if (Objects.isNull(accountNo) || accountNo.isBlank()) {
            return null;
        }

        return transaction.accountNo.eq(accountNo);
    }

    private BooleanExpression eqCardNo(String cardNo) {
        if (Objects.isNull(cardNo) || cardNo.isBlank()) {
            return null;
        }

        return transaction.cardNo.eq(cardNo);
    }

    private BooleanExpression eqTransactionType(String transactionType) {
        if (Objects.isNull(transactionType) || transactionType.isBlank()) {
            return null;
        }

        return transaction.transactionType.eq(TransactionType.valueOf(transactionType));
    }

    private BooleanExpression eqPaymentMethod(String paymentMethod) {
        if (Objects.isNull(paymentMethod) || paymentMethod.isBlank()) {
            return null;
        }

        return transaction.paymentMethod.eq(paymentMethod);
    }

    private BooleanExpression eqEmotion(String emotion) {
        if (Objects.isNull(emotion) || emotion.isBlank()) {
            return null;
        }

        return transaction.emotion.eq(Emotion.valueOf(emotion));
    }

    private BooleanExpression containsMerchantName(String merchantName) {
        if (Objects.isNull(merchantName)) {
            return null;
        }

        return transaction.merchantName.contains(merchantName);
    }

    private BooleanExpression containsMemo(String memo) {
        if (Objects.isNull(memo)) {
            return null;
        }

        return transaction.memo.contains(memo);
    }

    private BooleanExpression eqTransactionTime(String transactionTime) {
        if (Objects.isNull(transactionTime) || transactionTime.isBlank()) {
            return null;
        }

        return transaction.transactionTime.eq(transactionTime);
    }

    private Category getCategory(Long parentId) {
        if(parentId == null) return null; // categoryId 가 null 일 경우 null 반환
        return categoryRepository.findById(parentId)
                .orElse(null);
    }

}
