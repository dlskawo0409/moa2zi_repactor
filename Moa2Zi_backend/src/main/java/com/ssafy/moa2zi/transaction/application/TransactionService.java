package com.ssafy.moa2zi.transaction.application;

import com.ssafy.moa2zi.category.domain.Category;
import com.ssafy.moa2zi.category.domain.CategoryRepository;
import com.ssafy.moa2zi.common.util.FirebaseMessagingSnippets;
import com.ssafy.moa2zi.day.domain.Day;
import com.ssafy.moa2zi.day.domain.DayRepository;
import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import com.ssafy.moa2zi.merchant.domain.Merchant;
import com.ssafy.moa2zi.notification.application.NotificationProducer;
import com.ssafy.moa2zi.notification.application.NotificationService;
import com.ssafy.moa2zi.notification.domain.NotificationMessage;
import com.ssafy.moa2zi.notification.domain.NotificationType;
import com.ssafy.moa2zi.notification.dto.response.NotificationResponse;
import com.ssafy.moa2zi.transaction.domain.*;
import com.ssafy.moa2zi.transaction.dto.request.*;

import com.ssafy.moa2zi.transaction.dto.response.MapClusterResponse;
import com.ssafy.moa2zi.transaction.dto.response.TransactionSearchResponse;

import com.ssafy.moa2zi.transaction.dto.response.TransactionDetailSearchResponse;
import com.ssafy.moa2zi.transaction.dto.response.TransactionListSearchResponse;
import com.ssafy.moa2zi.transaction.dto.response.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final DayRepository dayRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionCacheService transactionCacheService;
    private final TransactionTopSpendRepository transactionTopSpendRepository;
    private final NotificationService notificationService;
    private final NotificationProducer notificationProducer;

    private static final int ALERT_THRESHOLD_PERCENT = 10;

    // [API] 가계부 요소 삭제하기
    @Transactional
    public void deleteTransaction(Long transactionId, CustomMemberDetails loginMember) {
        Long memberId = loginMember.getMemberId();
        Transaction transaction = transactionRepository.findById(transactionId).orElse(null);
        if(transaction == null) throw new NotFoundException("해당 Transaction Id 에 해당하는 거래내역이 없습니다.");
        if(transaction.getMemberId().equals(memberId)) {
            transactionRepository.delete(transaction);
        } else {
            throw new AccessDeniedException("본인의 거래내역만 삭제할 수 있습니다.");
        }
    }

    // [API] 기분 등록하기 =====
    @Transactional
    public void createTransactionEmotion(
            EmotionCreateRequest ecr,
            CustomMemberDetails loginMember
    ) {
        Long memberId = loginMember.getMemberId();
        Transaction transaction = transactionRepository.findByTransactionIdAndMemberId(ecr.transactionId(), memberId)
                .orElseThrow(() -> new NotFoundException("요청한 Transaction ID 에 해당하는 Transaction 데이터가 없습니다."));
        transaction.registerEmotion(ecr.emotion());
    }

    // [API] 카테고리별 분석 가져오기 =====
    public List<AnalysisByCategorySearchResponse> getAnalysisByCategory(
            AnalysisByCategoryListSearchRequest abclsr,
            CustomMemberDetails loginMember) throws Exception {
        Long memberId = loginMember.getMemberId();
        return createAnalysisByCategorySearchResponseList(abclsr, memberId);
    }

    // [API] 총 지출 금액 가져오기 =====
    public List<MonthlySpendSumResponse> getTotalSpendSumList(MonthlySpendSumListSearchRequest msslsr, CustomMemberDetails loginMember) {
        Long memberId = loginMember.getMemberId();
        return createMonthlySpendSumResponseList(msslsr, memberId);
    }

    // [API] 가계부 일별 리스트 가져오기 =====
    @Transactional
    public DailyTransactionListSearchResponse getDailyTransactionList(Long memberId, Integer transactionDate, CustomMemberDetails loginMember) {
        isValidDateFormat(transactionDate);
        return createDailyTransactionListSearchResponse(memberId, transactionDate);
    }

    // [API] 가계부 달력 가져오기 =====
    @Transactional
    public TransactionCalenderSearchResponse getTransactionCalender(Long memberId, Integer transactionDate, CustomMemberDetails loginMember) {
        isValidDateFormat(transactionDate);
        return createTransactionCalenderSearchResponse(memberId, transactionDate);
    }

    // [API] 가계부 상세 가져오기 =====
    @Transactional
    public TransactionDetailSearchResponse getTransactionDetail(CustomMemberDetails loginMember, Long transactionId) {
        if (transactionId == null) throw new RuntimeException("Transaction ID 가 필요합니다.");
        return createTransactionDetailSearchResponse(transactionId);
    }

    // [API] 가계부 리스트 가져오기
    @Transactional
    public TransactionListSearchResponse getTransactionList(CustomMemberDetails loginMember, TransactionListSearchRequest tlsr) {
        validateDateAndTimeFormat(tlsr.transactionDate(), tlsr.transactionTime()); // 날짜,시간 형식검증
        List<Day> dayList = getDayList(tlsr.memberId(), tlsr.transactionDate()); // 조건충족 dayList 조회
        return createTransactionListSearchResponse(dayList, tlsr);
    }

    // [API] 가계부 수기 입력
    @Transactional
    public void createTransactionManually(CustomMemberDetails loginMember, TransactionCreateRequest tcr) {

        // 날짜(YYYYMMDD), 시간형식(HHmmss) 유효성 확인하기
        Integer transactionDate = tcr.transactionDate();
        String transactionTime = tcr.transactionTime();
        validateDateAndTimeFormat(transactionDate, transactionTime);

        // Date 값 가진 Day 이미 있는지 판단
        Long dayId;

        List<Day> day = dayRepository.findDayByMemberIdAndTransactionDate(loginMember.getMemberId(), transactionDate);

        if(!day.isEmpty()) { // 있으면
            dayId = day.get(0).getId();
        } else { // 없으면
            Day newDay = Day.builder()
                    .memberId(loginMember.getMemberId())
                    .transactionDate(transactionDate)
                    .build();
            dayRepository.save(newDay);
            dayId = newDay.getId();
        }

        Transaction transaction = createTransactionByIsInBudget(
               tcr, dayId ,loginMember.getMemberId()
        );

        transactionRepository.save(transaction);
    }

    private Transaction createTransactionByIsInBudget(
            TransactionCreateRequest tcr,
            Long dayId,
            Long memberId
    ) {
        return Transaction.builder()
                .memberId(memberId)
                .dayId(dayId)
                .categoryId(tcr.categoryId())
                .balance(tcr.transactionBalance())
                .transactionType(tcr.transactionType())
                .paymentMethod(tcr.paymentType())
                .emotion(tcr.emotion())
                .memo(tcr.memo())
                .transactionTime(tcr.transactionTime())
                .merchantName(tcr.merchantName())
                .isInBudget(tcr.isInBudget()) // 지출 이외 거래내역 및
                .build();
    }

    // AnalysisByCategorySearchResponseList 만들기
    private List<AnalysisByCategorySearchResponse> createAnalysisByCategorySearchResponseList(
            AnalysisByCategoryListSearchRequest abclsr,
            Long memberId) throws Exception {
        return transactionRepository.findAnalysisByCategoryList(abclsr, memberId);
    }

    // MonthlySpendSumResponseList 만들기
    private List<MonthlySpendSumResponse> createMonthlySpendSumResponseList(MonthlySpendSumListSearchRequest msslsr, Long memberId) {
        return transactionRepository.findMonthlySpendSumList(msslsr, memberId);
    }

    // DailyTransactionListSearchResponse 만들기
    private DailyTransactionListSearchResponse createDailyTransactionListSearchResponse(Long memberId, Integer transactionDate) {

        List<DailyTransactionListSearchResponse.Transaction> transactionDataList = new ArrayList<>();

        List<Day> dayList = getDayList(memberId, transactionDate);

        for(Day day : dayList) {
            List<Transaction> transactionList = transactionRepository.findTransactionListByDayId(day.getId());

            for(Transaction transaction : transactionList) {

                Category subCategory = getSubCategory(transaction.getCategoryId());
                Category category = (subCategory != null) ? getCategory(subCategory.getParentId()) : null;
                String categoryName = (category != null) ? category.getCategoryName() : null;

                // SubCategoryData 만들기
                SubCategoryCommonResponse subCategoryData = getSubCategoryData(subCategory);

                DailyTransactionListSearchResponse.Transaction transactionData =
                        new DailyTransactionListSearchResponse.Transaction(
                                transaction.getTransactionId(),
                                day.getTransactionDate(),
                                transaction.getTransactionTime(),
                                transaction.getMemo(),
                                categoryName,
                                subCategoryData,
                                transaction.getBalance(),
                                transaction.getTransactionType(),
                                transaction.getPaymentMethod(),
                                transaction.getMerchantName(),
                                transaction.getEmotion()
                        );
                transactionDataList.add(transactionData);
            }
        }

        return new DailyTransactionListSearchResponse(
                transactionDataList
        );
    }

    // TransactionCalenderSearchResponse 만들기
    private TransactionCalenderSearchResponse createTransactionCalenderSearchResponse(Long memberId, Integer transactionDate) {

        // 20250122 -> 20241222
        Integer prevMonthTransactionDate = getPreviousMonth(transactionDate);

        // YYYYMM00, YYYYMMDD -> YYYYMM00
        Integer prevMonthDate = convertDateMonthly(prevMonthTransactionDate);
        Integer thisMonthDate = convertDateMonthly(transactionDate);

        List<Day> prevMonthDayList = getDayList(memberId, prevMonthDate);
        List<Day> thisMonthDayList = getDayList(memberId, thisMonthDate);

        // spendSums
        Long prevMonthSpendSum = 0L;
        Long thisMonthSpendSum = 0L;

        Long amountDiffPrevMonth = 0L;

        // dailySumWithDateList
        List<TransactionCalenderSearchResponse.DailySumWithDate> dailySumWithDateList = new ArrayList<>();

        // prevMonthSpendSum 계산
        for(Day day : prevMonthDayList) {
            List<Transaction> transactionList = transactionRepository.findTransactionListByDayId(day.getId());
            for (Transaction transaction : transactionList) {
                if(transaction.getTransactionType() == TransactionType.SPEND) {
                    prevMonthSpendSum += transaction.getBalance();
                }
            }
        }

        // thisMonthSpendSum, dailySpendSum 계산 및 dailySumWithDate 구하기
        for (Day day : thisMonthDayList) {
            Long dailySpendSum = 0L;
            // dayId 하나에 딸린 Transaction 다 뽑기
            List<Transaction> transactionList = transactionRepository.findTransactionListByDayId(day.getId());
            // thisMonthSpendSum, dailySpendSum 계산
            for (Transaction transaction : transactionList) {
                if(transaction.getTransactionType() == TransactionType.SPEND) {
                    dailySpendSum -= transaction.getBalance();
                    thisMonthSpendSum += transaction.getBalance();
                }
            }

            if( dailySpendSum < 0 ) {
                dailySumWithDateList.add(new TransactionCalenderSearchResponse.DailySumWithDate(
                        day.getId(),
                        day.getTransactionDate(),
                        dailySpendSum
                ));
            }

        }

        amountDiffPrevMonth = thisMonthSpendSum - prevMonthSpendSum;

        // Response 객체 생성
        return new TransactionCalenderSearchResponse(
                thisMonthSpendSum,
                amountDiffPrevMonth,
                dailySumWithDateList
        );
    }

    // YYYYMM00, YYYYMMDD 둘다 YYYYMM00 으로 변환
    private Integer convertDateMonthly(Integer transactionDate) {
        String strDate = transactionDate.toString();
        String normalizedDate = strDate.substring(0, 6) + "00";
        return Integer.parseInt(normalizedDate);
    }

    private Integer getPreviousMonth(Integer transactionDate) {
        // YYYYMM00 → YYYY-MM 변환
        int year = transactionDate / 10000;  // 연도 (YYYY)
        int month = (transactionDate / 100) % 100;  // 월 (MM)

        // 전월 계산
        YearMonth currentMonth = YearMonth.of(year, month);
        YearMonth previousMonth = currentMonth.minusMonths(1);

        // YYYYMM00 형식으로 반환
        return (previousMonth.getYear() * 10000) + (previousMonth.getMonthValue() * 100);
    }

    // TransactionDetailSearchResponse 만들기
    private TransactionDetailSearchResponse createTransactionDetailSearchResponse(Long transactionId) {

        Transaction transaction = getTransactionByTransactionId(transactionId);
        Category subCategory = getSubCategory(transaction.getCategoryId());
        Long parentId = subCategory != null ? subCategory.getParentId() : null;
        Category category = getCategory(parentId);
        String categoryName = category != null ? category.getCategoryName() : null;
        SubCategoryCommonResponse subCategoryData = getSubCategoryData(subCategory);
        Day day = getDayByDayId(transaction.getDayId());

        // Response 객체 생성
        return new TransactionDetailSearchResponse(
                transactionId,
                categoryName,
                subCategoryData,
                transaction.getBalance(),
                transaction.getTransactionType(),
                day.getTransactionDate(),
                transaction.getPaymentMethod(),
                transaction.getMemo(),
                transaction.getTransactionTime()
        );
    }

    private Transaction getTransactionByTransactionId(Long transactionId) {
        return transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new NotFoundException("요청한 Transaction ID 에 해당하는 Transaction 데이터가 없습니다."));
    }

    private Category getCategory(Long parentId) {
        if(parentId == null) return null; // categoryId 가 null 일 경우 null 반환
        return categoryRepository.findById(parentId)
                .orElse(null);
    }

    private Category getSubCategory(Long parentId) {
        if(parentId == null) return null; // parentId 가 null 일 경우 null 반환
        return categoryRepository.findById(parentId)
                .orElse(null);
    }

    private SubCategoryCommonResponse getSubCategoryData(Category subCategory) {
        if(subCategory != null) {
            return new SubCategoryCommonResponse(
                    subCategory.getId(),
                    subCategory.getCategoryName()
            );
        } else {
            return null;
        }
    }

    private Day getDayByDayId(Long dayId) {
        return dayRepository.findDayById(dayId)
                .orElseThrow(() -> new NotFoundException("요청한 Day ID 에 해당하는 Day 데이터가 없습니다."));
    }

    // TransactionListSearchResponse 만들기
    private TransactionListSearchResponse createTransactionListSearchResponse(List<Day> dayList, TransactionListSearchRequest tlsr) {

        // incomeSum, spendSum
        Long incomeSum = 0L;
        Long spendSum = 0L;
        Long totalSum = 0L;

        // transactionWithDate, transactionData
        List<TransactionListSearchResponse.TransactionWithDate> transactionWithDateList = new ArrayList<>();

        for (Day day : dayList) {

            // dayId 하나에 딸린 Transaction 다 뽑기
            List<Transaction> transactionList = transactionRepository.findTransactionListWithFilters(day.getMemberId(), day.getId(), tlsr);
            List<TransactionListSearchResponse.Transaction> transactionDataList = new ArrayList<>();

            String dayOfWeek = getDayOfWeek(day.getTransactionDate());

            // 데이에 지금 거래가 없음
            for(Transaction transaction : transactionList) {

                Category subCategory; // 서브카테고리
                Category category = getCategory(transaction.getCategoryId()); // 거래내역이 가진 카테고리

                if (tlsr.categoryId() != null) { // category 에 대한 요청이 왔을 때

                    Category inputCategory = getCategory(tlsr.categoryId()); // 프론트가 요청한 카테고리
                    if(inputCategory == null) throw new NotFoundException("요청 Category Id 에 해당하는 카테고리가 없습니다.");
                    Integer inputCategoryLevel = inputCategory.getLevel(); // 0

                    if(category != null) { // 거래내역 카테고리가 있으면

                        Integer categoryLevel = category.getLevel(); // 3
                        if(inputCategoryLevel > categoryLevel) continue; // 더 하위 카테고리를 요청하면 어차피 없으니 다음반복으로

                        Category tempCategory = category;

                        int depthCount = categoryLevel - inputCategoryLevel; // 3
                        for(int i=0; i<depthCount; i++) {
                            tempCategory = getCategory(tempCategory.getParentId()); // 거래내역 카테고리를 요청된 카테고리 level 만큼 상위로 올렸을 떄 !
                        }

                        if(tempCategory.getId().equals(inputCategory.getId())) {
                            if( category.getLevel().equals(0)) { // 대분류이면 서브카테고리는 없음 -> 왜냐면 이 카테고리가 최하위기 때문
                                subCategory = null;
                            } else { // 레벨이 0 이 아니면
                                subCategory = category; // 현재 카테고리가 서브 카테고리가 되고
                                category = getCategory(subCategory.getParentId()); // 바로 위 상위 카테고리를 찾아온다
                            }
                        } else {
                            continue; // 요청 카테고리 Id 가 거래내역 카테고리의 상위 카테고리의 Id 가 아니라면
                        }

                        // IncomeSum, SpendSum 계산
                        switch (transaction.getTransactionType()) { // 거래유형(수입, 지출, 이체)
                            case INCOME:
                                incomeSum += transaction.getBalance();
                                break;
                            case SPEND:
                                spendSum += transaction.getBalance();
                                break;
                            case TRANSFER:
                                break;
                            default:
                                // 위의 모든 case에 해당하지 않으면 실행되는 코드
                        }

                    } else { // 거래내역 카테고리가 없으면
                        continue;
                    }

                } else { // 요청이 오지 않았을 때

                    // IncomeSum, SpendSum 계산
                    switch (transaction.getTransactionType()) { // 거래유형(수입, 지출, 이체)
                        case INCOME:
                            incomeSum += transaction.getBalance();
                            break;
                        case SPEND:
                            spendSum += transaction.getBalance();
                            break;
                        case TRANSFER:
                            break;
                        default:
                            // 위의 모든 case에 해당하지 않으면 실행되는 코드
                    }

                    if ( category != null) {
                        if( category.getLevel().equals(0)) { // 대분류이면 서브카테고리는 없음 -> 왜냐면 이 카테고리가 최하위기 때문
                            subCategory = null;
                        } else { // 레벨이 0 이 아니면
                            subCategory = category; // 현재 카테고리가 서브 카테고리가 되고
                            category = getCategory(subCategory.getParentId()); // 바로 위 상위 카테고리를 찾아온다
                        }
                    } else {
                        subCategory = null;
                    }

                }

                // SubCategoryData 만들기
                SubCategoryCommonResponse subCategoryData = createSubCategoryData(subCategory);
                addDataInTransactionDataList(transactionDataList, transaction, category, subCategoryData);
            }

            if(transactionDataList.size() == 0) continue;

            transactionWithDateList.add(
                    new TransactionListSearchResponse.TransactionWithDate(
                            day.getId(),
                            day.getTransactionDate(),
                            dayOfWeek,
                            transactionDataList
                    )
            );

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");

            transactionDataList.sort(
                    Comparator.comparing((TransactionListSearchResponse.Transaction t) ->
                            LocalTime.parse(t.transactionTime(), formatter)
                    ).reversed()
            );

        }

        transactionWithDateList.sort(
                Comparator.comparing(TransactionListSearchResponse.TransactionWithDate::transactionDate).reversed()
        );

        totalSum = incomeSum - spendSum;

        return new TransactionListSearchResponse(
                incomeSum,
                spendSum,
                totalSum,
                transactionWithDateList
        );
    }

    private String getDayOfWeek(Integer transactionDate) {
        // 정수를 문자열로 변환 후 DateTimeFormatter로 파싱
        String dateString = String.valueOf(transactionDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate = LocalDate.parse(dateString, formatter);

        // 오늘이면 "TODAY" 반환
        if (localDate.equals(LocalDate.now())) return "TODAY";

        // 요일 반환
        return localDate.getDayOfWeek().toString();
    }

    private SubCategoryCommonResponse createSubCategoryData(Category subCategory) {
        if(subCategory == null) {
            return new SubCategoryCommonResponse(
                null,
                null
            );
        }
        return new SubCategoryCommonResponse(
                subCategory.getId(),
                subCategory.getCategoryName()
        );
    }

    private void addDataInTransactionDataList(List<TransactionListSearchResponse.Transaction> transactionDataList, Transaction transaction, Category category, SubCategoryCommonResponse subCategoryData) {

        String categoryName;

        if(category == null) {
            categoryName = null;
        } else {
            categoryName = category.getCategoryName();
        }

        transactionDataList.add(
                new TransactionListSearchResponse.Transaction(
                        transaction.getTransactionId(),
                        transaction.getTransactionTime(),
                        transaction.getMemo(),
                        categoryName, // 카테고리가 없는데 카테고리 getCategoryName
                        subCategoryData,
                        transaction.getMerchantName(),
                        transaction.getBalance(),
                        transaction.getTransactionType().toString(),
                        transaction.getPaymentMethod()
                )
        );
    }

    // 날짜조건(transactionDate)으로만 거른 가계부 리스트 불러오기
    private List<Day> getDayList(Long memberId, Integer transactionDate) {
        String strDate = transactionDate.toString();
        List<Day> dayList;
        // 00이 포함된 경우 해당월 전체로 처리
        if (strDate.endsWith("00")) {
            List<Integer> firstAndLastDayList = getFirstAndLastDayOfMonth(strDate);
            Integer firstDay = firstAndLastDayList.get(0); // 해당월 첫날
            Integer lastDay = firstAndLastDayList.get(1); // 해당월 마지막날
            // 날짜 범위에 해당하는 가계부일자(Day) 리스트 가져오기
            dayList = dayRepository.findDayListInRange(memberId, firstDay, lastDay);
        } else { // 일반적인 날짜 처리
            dayList = dayRepository.findDayByMemberIdAndTransactionDate(memberId, transactionDate);
        }
        return dayList;
    }

    // 20250200 일 경우 2월의 첫날, 마지막날 구하기
    public List<Integer> getFirstAndLastDayOfMonth(String strDate) {
        String yearMonth = strDate.substring(0, 6);
        LocalDate firstDayOfMonth = LocalDate.parse(yearMonth + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth());

        Integer firstDayInt = Integer.parseInt(firstDayOfMonth.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Integer lastDayInt = Integer.parseInt(lastDayOfMonth.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        return List.of(firstDayInt, lastDayInt);
    }

    private void validateDateAndTimeFormat(Integer transactionDate, String transactionTime) {
        // 날짜형식(YYYYMMDD) 검증
        if( transactionDate != null ) {
            isValidDateFormat(transactionDate);
        }

        // 시간형식(HHmmss) 검증
        if ( transactionTime != null && !transactionTime.isBlank()) {
            isValidTimeFormat(transactionTime);
        }
    }

    public void isValidDateFormat(Integer transactionDate) {
        // 1900년대부터 2000년대까지 YYYYMMDD 형식 유효성 검사
        if (!transactionDate.toString().matches("^(19|20)\\d{6}$")) {
            throw new IllegalArgumentException("transactionDate 는 YYYYMMDD 형식이어야 합니다.");
        }
    }

    private void isValidTimeFormat(String transactionTime) {
        // HHmmss 형식 유효성 검사
        if (!transactionTime.matches("^[0-2][0-9][0-5][0-9][0-5][0-9]$")) {
            throw new IllegalArgumentException("transactionTime 은 HHmmss 형식이어야 합니다.");
        }
    }

    public List<MapClusterResponse> getClustersByMapSearch(
            MapClusterRequest request,
            CustomMemberDetails loginMember
    ) {

        return transactionRepository.findClustersByMapSearch(request, loginMember);
    }

    public TransactionSearchResponse findTransactions(
            TransactionSearchRequest request,
            CustomMemberDetails loginMember
    ) {

        return transactionRepository.findTransactions(request, loginMember);
    }

    /**
     * 현재 클라이언트의 위치 기반으로 알림 생성
     */
    public List<NotificationResponse> checkAlertByLocation(
            float latitude,
            float longitude,
            Member member
    )  {

        Long memberId = member.getMemberId();

        // 비슷한 위치에서의 중복 알람 방지
        if(transactionCacheService.hasRequestNearBy(latitude, longitude, memberId)) {
            log.info("[checkAlertByLocation] 비슷한 위치에서의 중복 알림입니다.");
            return List.of();
        }

        // 클라이언트 요청 기록을 캐싱
        transactionCacheService.cacheRequestFromClient(latitude, longitude, memberId);

        // 주변 반경에서 top 5 지출이 존재하는지 확인
        List<NotificationResponse> topSpendingAlerts = checkTop5WithinRadius(latitude, longitude, memberId);
        List<NotificationResponse> result = new ArrayList<>(topSpendingAlerts);

        // 해당 지역에서 한달 전 소비와 비교
        NotificationResponse spendingChangeAlert = analyzeSpendingChangeInArea(latitude, longitude, memberId);
        if(spendingChangeAlert != null) {
            result.add(spendingChangeAlert);
        }

        return result;
    }

    /*
        이 지역에서의 최근 한달 - 이전 한달 소비내역 비교
     */
    private NotificationResponse analyzeSpendingChangeInArea(
            float latitude,
            float longitude,
            Long memberId
    ) {

        // 현재 시점 기준 한달 전과 두달 전 날짜를 정수형으로 변환
        LocalDateTime today = LocalDateTime.now();
        int todayInt = convertToIntTime(today);
        int oneMonthAgo = convertToIntTime(today.minusMonths(1));
        int twoMonthAgo = convertToIntTime(today.minusMonths(2));

        // 최근 한달 간 이 지역에서의 거래 내역
        List<Transaction> transactionsRecent = transactionRepository.findSpendingWithinRadiusAndDate(
                latitude, longitude, memberId, oneMonthAgo, todayInt
        );

        // 이전 한달 간 이 지역에서의 거래 내역
        List<Transaction> transactionsPast = transactionRepository.findSpendingWithinRadiusAndDate(
                latitude, longitude, memberId, twoMonthAgo, oneMonthAgo
        );

        // 한달 간 거래내역을 과거와 비교하여 지출 증가 퍼센티지 계산
        int increaseRate = calcGrowthRate(transactionsRecent, transactionsPast);
        if(increaseRate < ALERT_THRESHOLD_PERCENT) return null;

        log.info("[checkAlertByLocation] 이 주변에서의 지출이 이전 달보다 {} 퍼센트 올랐어요.", increaseRate);

        // 기준 이상 지출이 증가하면 알람 생성
        return notificationService.sendWithPolling(memberId, NotificationMessage.builder()
                .receiverId(memberId)
                .notificationType(NotificationType.MONTH_SPENDING_INCREASE)
                .increaseRate(increaseRate)
                .build());
    }

    private int calcGrowthRate(List<Transaction> transactionsRecent, List<Transaction> transactionsPast) {

        long recentTotal = getSpendingSum(transactionsRecent);
        long pastTotal = getSpendingSum(transactionsPast);

        // 이전 한달 간의 소비내역이 0원인 경우
        if (pastTotal == 0) {
            if (recentTotal == 0 || recentTotal < pastTotal) return 0;
            else return -1; // 새로 발생한 소비
        }

        double increaseRate = ((double) (recentTotal - pastTotal) / pastTotal) * 100;
        return (int) Math.round(increaseRate);
    }

    private long getSpendingSum(List<Transaction> transactions) {
        return transactions.stream()
                .mapToLong(Transaction::getBalance)
                .sum();
    }

    /*
        이 지역에서 top 5 소비내역이 있는지 확인
     */
    private List<NotificationResponse> checkTop5WithinRadius(
            float latitude,
            float longitude,
            Long memberId
    ) {

        List<NotificationResponse> result = new ArrayList<>();

        // 반경 200 미터 내의 TOP 5 소비내역 조회
        List<Long> topSpendIds = transactionCacheService.geoHashTop5SearchWithinRadius(
                latitude, longitude, memberId);

        for(Long topSpendId : topSpendIds) {
            // 이미 알림을 받은 소비내역인지 중복 확인
            if(transactionCacheService.isAlreadyTop5Alerted(memberId, topSpendId)) {
                continue;
            }

            // 아니면 알림 생성
            TransactionTopSpend topSpend = findTopSpendByMemberIdAndSpendId(memberId, topSpendId);
            NotificationResponse notificationResponse = notificationService.sendWithPolling(memberId, NotificationMessage.builder()
                    .receiverId(memberId)
                    .notificationType(NotificationType.TOP_SPENDING)
                    .topSpend(NotificationMessage.TopSpend.of(topSpend))
                    .build());

            log.info("[checkAlertByLocation] top5 소비내역이 주변에 존재해요. topSpendId : {}" , topSpendId);
            // 알림 기록 캐싱
            transactionCacheService.cacheTop5Alert(memberId, topSpendId);
            result.add(notificationResponse);
        }

        return result;
    }

    private TransactionTopSpend findTopSpendByMemberIdAndSpendId(Long memberId, Long topSpendId) {
        return transactionTopSpendRepository.findByMemberIdAndId(memberId, topSpendId)
                .orElseThrow(() -> new NotFoundException("현재 ID의 유저에 해당하는 top5 소비내역이 없습니다, memberId = " + memberId + " spendId = " + topSpendId));
    }

    private int convertToIntTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return Integer.parseInt(dateTime.format(formatter));
    }

}
