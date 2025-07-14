package com.ssafy.moa2zi.notification.application;

import com.ssafy.moa2zi.auth.application.RedisRefreshTokenService;
import com.ssafy.moa2zi.auth.domain.RefreshToken;
import com.ssafy.moa2zi.auth.dto.response.AccessAndRefreshToken;
import com.ssafy.moa2zi.common.util.FirebaseMessagingSnippets;
import com.ssafy.moa2zi.finance.application.FinanceProducer;
import com.ssafy.moa2zi.finance.application.TransactionSyncService;
import com.ssafy.moa2zi.finance.event.FinanceEvent;
import com.ssafy.moa2zi.finance.event.FinanceEventPublisher;
import com.ssafy.moa2zi.finance.event.FinanceEventType;
import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.domain.MemberRepository;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import com.ssafy.moa2zi.notification.domain.*;
import com.ssafy.moa2zi.notification.dto.response.NotificationResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private static final String NOTIFICATION_NAME = "notification";

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final EmitterRepository emitterRepository;
    private final FinanceEventPublisher financeEventPublisher;
    private final FirebaseMessagingSnippets firebaseMessagingSnippets;
    private final FinanceProducer financeProducer;


    public SseEmitter subscribe(CustomMemberDetails loginMember, String lastEventId) {

        Member member = findMemberByMemberId(loginMember.getMemberId());
        if(!Objects.isNull(member) && !member.getAlarm()) {
            return null;
        }

        // 유실된 데이터 재전송을 위해 고유 값 ID 를 만들어 저장
        String emitterId = member.getMemberId() + "_" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.delete(emitterId));
        emitter.onTimeout(() -> emitterRepository.delete(emitterId));
        emitter.onError((e) -> emitterRepository.delete(emitterId));

        sendToClient(emitter, emitterId, "SSE 연결이 되었습니다 [memberId = " + member.getMemberId() + "]");

        // 거래내역 동기화 작업 이벤트 발행
        financeProducer.send(FinanceEvent.builder()
                .memberId(member.getMemberId())
                .type(FinanceEventType.SYNC_TRANSACTION)
                .build());

        // lastEventId 가 존재하면 연결이 끊어졌을 때 누락된 알림을 전송
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithById(member.getMemberId());
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> {
                        sendToClient(emitter, entry.getKey(), entry.getValue());
                    });
        }

        return emitter;
    }

    @Transactional
    public void sendWithSSE(Long receiverId, NotificationMessage message) {
        Notification notification = createAndSaveNotification(receiverId, message);
        Member sender = findMemberByMemberId(message.senderId());
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithById(receiverId);

        emitters.forEach((emitterId, emitter) -> {
                    try {
                        NotificationResponse response = NotificationResponse.from(sender, notification);
                        emitter.send(SseEmitter.event()
                                .id(emitterId)
                                .name(NOTIFICATION_NAME)
                                .data(response));

                        emitterRepository.saveToEventCache(emitterId, response);
                    } catch (IOException exception) {
                        emitterRepository.delete(emitterId);
                        throw new RuntimeException("알림 전송에 실패하였습니다.");
                    }
        });
    }

    @Transactional
    public void sendWithFirebasePush(Long receiverId, NotificationMessage message) throws Exception {
        Notification notification = createAndSaveNotification(receiverId, message);
        firebaseMessagingSnippets.sendToToken(notification);
    }

    @Transactional
    public NotificationResponse sendWithPolling(Long receiverId, NotificationMessage message) {
        Notification notification = createAndSaveNotification(receiverId, message);
        Member sender = findMemberByMemberId(message.senderId());
        return NotificationResponse.from(sender, notification);
    }

    /**
     * Notification 엔티티 생성 및 DB 저장
     */
    private Notification createAndSaveNotification(Long receiverId, NotificationMessage message) {
        Member sender = findMemberByMemberId(message.senderId());
        String template = createMessageTemplate(sender, message);

        Notification notification = Notification.builder()
                .receiverId(receiverId)
                .senderId(message.senderId())
                .notificationType(message.notificationType())
                .message(template)
                .build();

        return notificationRepository.save(notification);
    }

    // 알림 실시간 전송
    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .name(NOTIFICATION_NAME)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.delete(emitterId);
            throw new RuntimeException("알림 전송에 실패하였습니다.");
        }
    }

    private String createMessageTemplate(Member sender, NotificationMessage message) {
        NotificationType type = message.notificationType();

        return switch (type) {
            case POCKET_MONEY -> String.format(type.getTemplate(), message.dailyPocketMoney());
            case TOP_SPENDING ->
                String.format(
                        type.getTemplate(),
                        message.topSpend().merchantName(),
                        message.topSpend().balance(),
                        message.topSpend().rankNum()
                );

            case MONTH_SPENDING_INCREASE ->
                String.format(
                        type.getTemplate(),
                        message.increaseRate()
                );

            default -> String.format(type.getTemplate(), sender.getNickname());
        };
    }

    private Member findMemberByMemberId(Long memberId) {
        if(Objects.isNull(memberId)) {
            return null;
        }

        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("해당 ID의 유저가 존재하지 않습니다, " + memberId));
    }

    public List<NotificationResponse> getUnreadNotifications(CustomMemberDetails loginMember) {
        return notificationRepository.findByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(loginMember.getMemberId())
                .stream()
                .map(notification -> NotificationResponse.from(
                        findMemberByMemberId(notification.getSenderId()),
                        notification)
                )
                .toList();
    }

    public List<NotificationResponse> getAllNotifications(CustomMemberDetails loginMember) {
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(loginMember.getMemberId())
                .stream()
                .map(notification -> NotificationResponse.from(
                        findMemberByMemberId(notification.getSenderId()),
                        notification)
                )
                .toList();
    }

    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("해당 ID의 알림을 찾을 수 없습니다, " + notificationId));

        notification.markAsRead(); // 읽음 표시
    }

    /**
     * 연결 유지 및 예외 발생하는 emitter 정리를 위헤 클라이언트에게 주기적 신호 전송
     */
    @Scheduled(fixedRate = 30000)
    public void sendHeartbeat() throws Exception {

        Map<String, SseEmitter> sseEmitters = emitterRepository.findAll();

        sseEmitters.forEach((emitterId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("heartbeat")
                        .data("ping"));

            } catch (Exception e) {
                emitterRepository.delete(emitterId);
            }
        });

    }

    /**
     * 오래된 캐시 데이터 삭제
     */
    @Scheduled(fixedRate = 60000)
    public void cleanOldEventCache() {
        emitterRepository.cleanOldEventInCache();
    }

    private Long getMemberIdFromEmitterId(String emitterId) {
        return Long.valueOf(emitterId.split("_")[0]);
    }




}
