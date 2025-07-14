package com.ssafy.moa2zi.transaction.domain;

import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import com.ssafy.moa2zi.merchant.domain.Merchant;
import com.ssafy.moa2zi.transaction.application.TransactionService;
import com.ssafy.moa2zi.transaction.dto.request.AnalysisByCategoryListSearchRequest;
import com.ssafy.moa2zi.transaction.dto.request.MonthlySpendSumListSearchRequest;
import com.ssafy.moa2zi.transaction.dto.request.TransactionSearchRequest;
import com.ssafy.moa2zi.transaction.dto.response.*;
import com.ssafy.moa2zi.transaction.dto.request.TransactionListSearchRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TransactionRepositoryCustom {

    List<Transaction> findTransactionListByDayId(Long dayId);
    
    List<Transaction> findTransactionListWithFilters(Long memberId, Long dayId, TransactionListSearchRequest tlsr);

    TransactionSearchResponse findTransactions(
            TransactionSearchRequest request,
            CustomMemberDetails loginMember
    );

    Map<Long, Long> findTotalSpendingBetween(
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    List<Transaction> findSpendingWithinRadiusAndDate(
            double latitude,
            double longitude,
            Long memberId,
            int startDateTimeInt,
            int endDateTimeInt
    );

    Long findTransactionSpendSumOfMonth(Integer integerPocketMoneyStartDate, Integer integerLastDate, Long memberId);

    List<String> findTopSpendingCategoriesByMemberId(Long memberId, int limit);

    List<MonthlySpendSumResponse> findMonthlySpendSumList(MonthlySpendSumListSearchRequest msslsr, Long memberId);

    List<AnalysisByCategorySearchResponse> findAnalysisByCategoryList(AnalysisByCategoryListSearchRequest abclsr, Long memberId);

    Long findAccumulatedSpendSum(Long memberId, Integer pocketMoneyStartTime, Integer intYesterdayDate);

    Long findYesterdaySpendSum(Long memberId, Integer intYesterdayDate);

    Set<Long> findMembersSpendingCountOver(Integer dateTimeInt, int spendingCount);

    List<Transaction> findByMemberIdsAndDateOrderByBalance(Set<Long> memberIds, Integer dateTimeInt);

    void updateLocationFields(Long transactionId, Merchant merchant);
}

