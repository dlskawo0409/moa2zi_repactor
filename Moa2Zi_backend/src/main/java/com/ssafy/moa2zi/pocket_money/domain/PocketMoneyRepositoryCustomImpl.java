package com.ssafy.moa2zi.pocket_money.domain;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.moa2zi.pocket_money.dto.response.PocketMoneyInfoResponse;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class PocketMoneyRepositoryCustomImpl implements PocketMoneyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    QPocketMoney pocketMoney = new QPocketMoney("pocketMoney");

    @Override
    public PocketMoneyInfoResponse findTotalAmountAndStartTime(
            LocalDateTime firstDateTimeOfMonth,
            LocalDateTime lastDateTimeOfMonth,
            Long memberId
    ) {

        PocketMoneyInfoResponse pocketMoneyInfoResponse = queryFactory
                .select(
                        Projections.constructor(
                                PocketMoneyInfoResponse.class,
                                pocketMoney.totalAmount,
                                pocketMoney.startTime,
                                pocketMoney.endTime
                        )
                )
                .from(pocketMoney)
                .where(
                        pocketMoney.memberId.eq(memberId),
                        pocketMoney.startTime.goe(firstDateTimeOfMonth),
                        pocketMoney.endTime.loe(lastDateTimeOfMonth)
                )
                .fetchOne();

        return pocketMoneyInfoResponse;
    }

    @Override
    public List<Long> findMembersHavingPocketMoneyInMonth(LocalDateTime dateTime) {

        return queryFactory
                .select(pocketMoney.memberId)
                .from(pocketMoney)
                .where(
                        pocketMoney.startTime.goe(dateTime),
                        pocketMoney.endTime.loe(dateTime)
                )
                .groupBy(pocketMoney.memberId)
                .fetch();
    }

}