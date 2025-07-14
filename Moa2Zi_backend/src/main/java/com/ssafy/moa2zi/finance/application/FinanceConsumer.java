package com.ssafy.moa2zi.finance.application;

import com.ssafy.moa2zi.finance.event.FinanceEvent;
import com.ssafy.moa2zi.finance.event.FinanceEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.ssafy.moa2zi.finance.event.FinanceEventType.*;
import static com.ssafy.moa2zi.finance.event.FinanceEventType.SYNC_CATEGORY_AI;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinanceConsumer {

    private final TransactionSyncService transactionSyncService;
    private final MerchantSyncService merchantSyncService;
    private final CategorySyncService categorySyncService;

    @KafkaListener(
            topics = "${spring.kafka.topic.finance}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "financeKafkaListenerContainerFactory"
    )
    public void consume(FinanceEvent event) {
        try {
            log.info("[kafka finance consumer] Consume the event {}", event);
            if(event.type().equals(SYNC_TRANSACTION)) {
                transactionSyncService.fetchAndSaveTransactionFromApi(event);
            } else if(event.type().equals(SYNC_MERCHANT)) {
                merchantSyncService.linkMerchantsToTransactions(event);
            } else if(event.type().equals(SYNC_CATEGORY_AI)) {
                categorySyncService.syncCategoryWithAIModel(event);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
