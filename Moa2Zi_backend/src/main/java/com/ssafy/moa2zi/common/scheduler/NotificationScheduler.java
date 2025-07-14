package com.ssafy.moa2zi.common.scheduler;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.ssafy.moa2zi.common.util.FirebaseMessagingSnippets;
import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.domain.MemberRepository;
import com.ssafy.moa2zi.notification.application.NotificationProducer;
import com.ssafy.moa2zi.notification.domain.NotificationMessage;
import com.ssafy.moa2zi.notification.domain.NotificationType;
import com.ssafy.moa2zi.pocket_money.domain.PocketMoney;
import com.ssafy.moa2zi.pocket_money.domain.PocketMoneyRepository;
import com.ssafy.moa2zi.pocket_money.dto.response.PocketMoneyInfoResponse;
import com.ssafy.moa2zi.transaction.domain.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.jmx.export.notification.NotificationPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final TransactionRepository transactionRepository;
    private final PocketMoneyRepository pocketMoneyRepository;
    private final NotificationProducer notificationProducer;

    /**
     *  매일 오후 1시
     *  용돈(예산) 목표를 위해 하루 얼마나 지출해야 하는 지 알림 생성
     */
    @Scheduled(cron = "0 0 13 * * *")
    public void generateNotificationsFromPocketMoney() throws Exception {

        LocalDateTime now = LocalDateTime.now();
        log.info("NotificationScheduler : 용돈 기반 알림 생성 스케줄러 시작 - {}", now);

        // 현재 용돈이 설정되어 있는 모든 멤버 ID
        List<Long> memberIds = pocketMoneyRepository.findMembersHavingPocketMoneyInMonth(now);

        for(Long memberId : memberIds) {
            // 현재 유저의 용돈 설정 정보 조회
            PocketMoneyInfoResponse pocketMoneyInfo = pocketMoneyRepository.findTotalAmountAndStartTime(now, now, memberId); // 총 용돈
            if (Objects.isNull(pocketMoneyInfo)) {
                log.warn("NotificationScheduler : memberId {} 에 대한 유효한 PocketMoney 가 존재하지 않습니다", memberId);
                continue;
            }

            // 용돈 설정 기간 가져오기
            LocalDateTime startTime = pocketMoneyInfo.startTime();
            LocalDateTime endTime = pocketMoneyInfo.endTime();

            // 용돈 기간의 마지막 날짜와 오늘 날짜를 계산하여 남은 일자 구하기
            LocalDate endDate = endTime.toLocalDate();
            long remainingDays = ChronoUnit.DAYS.between(now.toLocalDate(), endDate) + 1;

            if (remainingDays <= 0)
                continue;

            Long totalAmount = pocketMoneyInfo.totalAmount();
            Long totalSpending = findTotalSpendingOfMonth(startTime, endTime, memberId); // 기간 내 지출한 돈
            long remainingPocketMoney = totalAmount - totalSpending; // 용돈까지 남아있는 금액

            if(remainingPocketMoney <= 0) {
                log.info("NotificationScheduler : memberId {} 유저는 남은 용돈 금액이 없습니다.", memberId);
                continue;
            }

            // 하루 평균 사용할 수 있는 돈
            long dailyPocketMoney = remainingPocketMoney / remainingDays;
            if(dailyPocketMoney >= 1000) { // 1000 원 이상이어야 알람
                notificationProducer.send(NotificationMessage.builder()
                        .receiverId(memberId)
                        .notificationType(NotificationType.POCKET_MONEY)
                        .dailyPocketMoney(dailyPocketMoney)
                        .build());
            }
        }

        log.info("NotificationScheduler : 용돈 기반 알림 생성 스케줄러 종료 - {}", LocalDateTime.now());
    }

    private Long findTotalSpendingOfMonth(
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long memberId)
    {
        // LocalDateTIME 을 정수형 yyyyMMdd 로 포맷 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        int startTimeInt = Integer.parseInt(startTime.format(formatter));
        int endTimeInt = Integer.parseInt(endTime.format(formatter));

        return transactionRepository.findTransactionSpendSumOfMonth(startTimeInt, endTimeInt, memberId);
    }
}
