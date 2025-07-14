package com.ssafy.moa2zi.day.domain;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DayRepository extends JpaRepository<Day, Long>, DayRepositoryCustom {
    // 해당일 가계부일자 조회 쿼리
    List<Day> findDayByMemberIdAndTransactionDate(@NotNull Long memberId, @NotNull Integer transactionDate);
    Optional<Day> findDayById(Long id);
    Optional<Day> findDayByTransactionDateAndMemberId(Integer transactionDate, Long memberId);
}
