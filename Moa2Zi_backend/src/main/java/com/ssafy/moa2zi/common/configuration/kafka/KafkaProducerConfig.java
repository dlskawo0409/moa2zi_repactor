package com.ssafy.moa2zi.common.configuration.kafka;

import java.util.HashMap;
import java.util.Map;

import com.ssafy.moa2zi.chat.domain.Chat;
import com.ssafy.moa2zi.chat.dto.request.ChatSendRequest;
import com.ssafy.moa2zi.finance.event.FinanceEvent;
import com.ssafy.moa2zi.notification.domain.NotificationMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;


@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Value("$${spring.kafka.bootstrap-servers}")
    private String kafkaBroker;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configs.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 200000000); // 200MB
        return configs;
    }

    @Bean
    public ProducerFactory<String, NotificationMessage> notificationProducerFactory() {
        DefaultKafkaProducerFactory<String, NotificationMessage> factory = new DefaultKafkaProducerFactory<>(producerConfigs());
        factory.setTransactionIdPrefix("tx-"); // 트랜잭션 커밋 후 이벤트 발행될 수 있도록 함
        return factory;
    }

    @Bean
    public KafkaTemplate<String, NotificationMessage> notificationKafkaTemplate() {
        return new KafkaTemplate<>(notificationProducerFactory());
    }

    @Bean
    public ProducerFactory<String, ChatSendRequest> chatProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); // 중요
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, ChatSendRequest> chatKafkaTemplate() {
        return new KafkaTemplate<>(chatProducerFactory());
    }

    @Bean
    public ProducerFactory<String, FinanceEvent> financeProducerFactory() {
        DefaultKafkaProducerFactory<String, FinanceEvent> factory = new DefaultKafkaProducerFactory<>(producerConfigs());
        return factory;
    }

    @Bean
    public KafkaTemplate<String, FinanceEvent> financeKafkaTemplate() {
        return new KafkaTemplate<>(financeProducerFactory());
    }

}
