package com.ssafy.moa2zi.pocket_money.domain;

import com.ssafy.moa2zi.pocket_money.dto.response.PocketMoneyInfoResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PocketMoneyRepository extends JpaRepository<PocketMoney, Long>, PocketMoneyRepositoryCustom {
    PocketMoneyInfoResponse findTotalAmountAndStartTime(LocalDateTime firstDateTimeOfMonth, LocalDateTime lastDateTimeOfMonth, Long memeberId);

    List<PocketMoney> findByEndTime(@NotNull LocalDateTime endTime);
}
