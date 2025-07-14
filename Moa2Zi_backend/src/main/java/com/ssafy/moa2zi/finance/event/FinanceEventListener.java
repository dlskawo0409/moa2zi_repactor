package com.ssafy.moa2zi.finance.event;

import com.ssafy.moa2zi.finance.application.CategorySyncService;
import com.ssafy.moa2zi.finance.application.MerchantSyncService;
import com.ssafy.moa2zi.finance.application.TransactionSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinanceEventListener {

    private final TransactionSyncService transactionSyncService;
    private final MerchantSyncService merchantSyncService;
    private final CategorySyncService categorySyncService;

    /**
     * SYNC_TRANSACTION 이벤트
     */
    @Async("transactionSyncTaskAsyncExecutor")
    @EventListener
    public void handleTransactionSyncEvent(FinanceEvent event) throws Exception {
        if (event.type() != FinanceEventType.SYNC_TRANSACTION) return;

        log.info("[FinanceEventListener] SYNC_TRANSACTION 이벤트 수신: {}", event);
        transactionSyncService.fetchAndSaveTransactionFromApi(event);
    }

    /**
     * SYNC_MERCHANT 이벤트: 거래내역 동기화 후 가맹점 위치 정보 동기화 비동기 처리
     */
    @Async("transactionSyncTaskAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMerchantSyncEvent(FinanceEvent event) {
        if (event.type() != FinanceEventType.SYNC_MERCHANT) return;

        log.info("[FinanceEventListener] SYNC_MERCHANT 이벤트 수신: {}", event);
        merchantSyncService.linkMerchantsToTransactions(event);
    }

    /**
     * SYNC_CATEGORY_AI 이벤트: 거래내역 카테고리 분류
     */
    @Async("transactionSyncTaskAsyncExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCategorySyncEvent(FinanceEvent event) {
        if (event.type() != FinanceEventType.SYNC_CATEGORY_AI) return;

        log.info("[FinanceEventListener] SYNC_CATEGORY_AI 이벤트 수신: {}", event);
        categorySyncService.syncCategoryWithAIModel(event);
    }

}