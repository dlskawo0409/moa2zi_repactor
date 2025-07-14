package com.ssafy.moa2zi.chat.application;

import com.ssafy.moa2zi.chat.domain.Chat;
import com.ssafy.moa2zi.chat.dto.request.ChatSendRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, ChatSendRequest> kafkaTemplate;

    public void send(String topic, ChatSendRequest chat) {
        try {
            log.info("📤 [Kafka Producer] Sending message: {} to topic: {}", chat, topic);
            kafkaTemplate.send(topic, chat);
        } catch (Exception e) {
            log.error("❌ [Kafka Producer] Failed to send message: {}", e);
            throw new RuntimeException("채팅 전송에 실패했습니다.");
        }
    }
}
