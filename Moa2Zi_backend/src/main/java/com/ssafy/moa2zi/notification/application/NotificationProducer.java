package com.ssafy.moa2zi.notification.application;

import com.ssafy.moa2zi.notification.domain.NotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducer {

    @Value("${spring.kafka.topic.notification}")
    private String topic;

    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    public void send (NotificationMessage message) {
        try {
            kafkaTemplate.send(topic, String.valueOf(message.receiverId()), message);
            log.info("[kafka notification producer] sending message: {} to topic: {}", message, topic);
        } catch (Exception e) {
            log.info("[kafka notification producer] fail to send message: {}", message);
            throw new RuntimeException(e);
        }
    }
}
