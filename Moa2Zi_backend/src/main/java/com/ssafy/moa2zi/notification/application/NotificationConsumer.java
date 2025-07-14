package com.ssafy.moa2zi.notification.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.moa2zi.notification.domain.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${spring.kafka.topic.notification}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "notificationKafkaListenerContainerFactory"
    )
    public void consume(NotificationMessage message) throws Exception {
        log.info("[kafka notification consumer] Consume the event {}", message);
//        notificationService.sendWithSSE(message.receiverId(), message);
        notificationService.sendWithFirebasePush(message.receiverId(), message);
    }

}
