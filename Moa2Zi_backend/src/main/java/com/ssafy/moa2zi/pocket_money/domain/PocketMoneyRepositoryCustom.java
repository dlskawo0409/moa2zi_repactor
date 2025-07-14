package com.ssafy.moa2zi.pocket_money.domain;

import com.ssafy.moa2zi.pocket_money.dto.response.PocketMoneyInfoResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface PocketMoneyRepositoryCustom {

    List<Long> findMembersHavingPocketMoneyInMonth(LocalDateTime dateTime);

    PocketMoneyInfoResponse findTotalAmountAndStartTime(LocalDateTime firstDateTimeOfMonth, LocalDateTime lastDateTimeOfMonth, Long memeberId);
}
