package com.ssafy.moa2zi.yono_point.domain;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.moa2zi.pocket_money.domain.QPocketMoney;
import com.ssafy.moa2zi.yono_point.dto.response.YonoPointResponse;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class YonoPointRepositoryCustomImpl implements YonoPointRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    QYonoPoint yonoPoint = new QYonoPoint("yonoPoint");
    QPocketMoney pocketMoney = new QPocketMoney("pocketMoney");

    @Override
    public YonoPoint findYesterdayYonoPoint(
            LocalDateTime yesterdayStartTime,
            LocalDateTime yesterdayEndTime,
            Long memberId
    ) {

        return queryFactory
                .selectFrom(yonoPoint)
                .join(pocketMoney).on(pocketMoney.id.eq(yonoPoint.pocketMoneyId))
                .where(
                        pocketMoney.memberId.eq(memberId),
                        yonoPoint.createdAt.goe(yesterdayStartTime),
                        yonoPoint.createdAt.loe(yesterdayEndTime)
                )
                .fetchOne();
    }

    @Override
    public List<YonoPointResponse> findYonoPointListWithDateTimeFilter(
            LocalDateTime monthlyEndTime,
            Long memberId
    ) {

        return queryFactory
                .select(
                        Projections.constructor(
                                YonoPointResponse.class,
                                yonoPoint.id,
                                yonoPoint.pocketMoneyId,
                                yonoPoint.score,
                                yonoPoint.createdAt
                        )
                )
                .from(yonoPoint)
                .join(pocketMoney).on(pocketMoney.id.eq(yonoPoint.pocketMoneyId))
                .where(
                        pocketMoney.memberId.eq(memberId),
                        pocketMoney.endTime.eq(monthlyEndTime)
                )
                .orderBy(
                        yonoPoint.createdAt.asc()
                )
                .fetch();
    }
}
