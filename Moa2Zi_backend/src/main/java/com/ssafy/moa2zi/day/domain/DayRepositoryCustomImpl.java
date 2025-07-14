package com.ssafy.moa2zi.day.domain;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.moa2zi.day.dto.request.DayTransactionSumGetRequest;
import com.ssafy.moa2zi.day.dto.request.SumWithCategory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.ssafy.moa2zi.day.domain.QDay.day;
import static com.ssafy.moa2zi.transaction.domain.QTransaction.transaction;
import static com.ssafy.moa2zi.category.domain.QCategory.category;

@RequiredArgsConstructor
public class DayRepositoryCustomImpl implements DayRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Day> findDayListInRange(Long memberId, Integer firstDay, Integer lastDay) {

        return queryFactory
                .selectFrom(day)
                .join(transaction).on(day.id.eq(transaction.dayId))
                .where(
                        day.transactionDate.between(firstDay, lastDay),
                        day.memberId.eq(memberId)
                )
                .fetch();
    }

    @Override
    public List<SumWithCategory> getSumByDayGroupByCategory(
            DayTransactionSumGetRequest dayTransactionSumGetRequest,
            Long memberId
    ){
        return queryFactory
                .select(
                        Projections.constructor(
                                SumWithCategory.class,
                                transaction.balance.sum(),
                                category.categoryName
                        )
                )
                .from(day)
                .join(transaction).on(day.id.eq(transaction.dayId))
                .leftJoin(category).on(transaction.categoryId.eq(category.id))
                .where(
                        day.memberId.eq(memberId),
                        day.transactionDate.between(dayTransactionSumGetRequest.startTime(), dayTransactionSumGetRequest.endTime())
                )
                .groupBy(category.id)
                .fetch();
    }

}

