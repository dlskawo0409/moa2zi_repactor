package com.ssafy.moa2zi.finance.application;

import com.ssafy.moa2zi.finance.event.FinanceEvent;
import com.ssafy.moa2zi.finance.event.FinanceEventType;
import com.ssafy.moa2zi.notification.domain.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinanceProducer {

    @Value("${spring.kafka.topic.finance}")
    private String topic;

    private final KafkaTemplate<String, FinanceEvent> kafkaTemplate;

    public void send(FinanceEvent event) {
        try{
            if (event.type().equals(FinanceEventType.SYNC_TRANSACTION)) {
                // 즉시 메시지 발행
                kafkaTemplate.send(topic, String.valueOf(event.memberId()), event);
            } else {
                // 거래내역 동기화 처리 트랜잭션이 정상적으로 커밋된 후에 메시지 발행
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        kafkaTemplate.send(topic, String.valueOf(event.memberId()), event);
                    }
                });
            }
            log.info("[kafka finance producer] sending event: {} to topic: {}", event, topic);
        } catch (Exception e) {
            log.error("[kafka finance producer] fail to send event: {}", event);
            throw new RuntimeException(e);
        }
    }
}
