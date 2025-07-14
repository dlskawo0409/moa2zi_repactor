package com.ssafy.moa2zi.transaction.domain;

import com.ssafy.moa2zi.transaction.dto.request.AnalysisByCategoryListSearchRequest;
import com.ssafy.moa2zi.transaction.dto.request.MonthlySpendSumListSearchRequest;
import com.ssafy.moa2zi.transaction.dto.request.TransactionListSearchRequest;
import com.ssafy.moa2zi.transaction.dto.response.AnalysisByCategorySearchResponse;
import com.ssafy.moa2zi.transaction.dto.response.MonthlySpendSumResponse;
import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, TransactionJdbcRepository, TransactionRepositoryCustom {
    Optional<Transaction> findByTransactionIdAndMemberId(Long transactionId, Long memberId);
    Optional<Transaction> findByTransactionId(Long transactionId);
    List<Transaction> findTransactionListByDayId(@NotNull Long dayId);
    List<Transaction> findTransactionListWithFilters(@NotNull Long memberId, Long dayId, TransactionListSearchRequest tlsr);
    Long findTransactionSpendSumOfMonth(Integer integerPocketMoneyStartDate, Integer integerLastDate, Long memberId);
    List<MonthlySpendSumResponse> findMonthlySpendSumList(MonthlySpendSumListSearchRequest msslsr, Long memberId);
    List<AnalysisByCategorySearchResponse> findAnalysisByCategoryList(AnalysisByCategoryListSearchRequest abclsr, Long memberId);

    @Query("SELECT t FROM Transaction t " +
            "WHERE t.memberId = :memberId AND t.merchantName IS NOT NULL " +
            "AND t.coordinate IS NULL")
    List<Transaction> findIdByCoordinateIsNull(@Param("memberId") Long memberId);

    Long findAccumulatedSpendSum(Long memberId, Integer pocketMoneyStartTime, Integer intYesterdayDate);
    
    Long findYesterdaySpendSum(Long memberId, Integer intYesterdayDate);

    @Query("SELECT t FROM Transaction t WHERE t.categoryId IS NULL AND t.merchantName IS NOT NULL")
    List<Transaction> findByCategoryIdIsNullAndMerchantNameIsNotNull();

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Transaction t SET t.categoryId = :categoryId WHERE t.transactionId = :id")
    void updateCategory(@Param("id") Long id, @Param("categoryId") Long categoryId);

    @Query("SELECT t FROM Transaction t WHERE t.coordinate IS NULL AND t.merchantId IS NOT NULL AND t.memberId = :memberId")
    List<Transaction> findIdByCoordinateIsNullAndMerchantIdIsNotNull(@Param("memberId") Long memberId);
}


