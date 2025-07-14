package com.ssafy.moa2zi.finance.application;

import com.ssafy.moa2zi.finance.event.FinanceEvent;
import com.ssafy.moa2zi.merchant.domain.Merchant;
import com.ssafy.moa2zi.merchant.domain.MerchantRepository;
import com.ssafy.moa2zi.transaction.application.TransactionService;
import com.ssafy.moa2zi.transaction.domain.Transaction;
import com.ssafy.moa2zi.transaction.domain.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class MerchantSyncService {

    private final TransactionRepository transactionRepository;
    private final MerchantRepository merchantRepository;

    /**
     * 거래내역의 위치 정보를 가맹점 정보와 연결
     */
    @Transactional
    public void linkMerchantsToTransactions(FinanceEvent event) {
        Long memberId = event.memberId();

        // 가맹점 위치 정보가 업데이트되지 않은 거래내역 조회
        List<Transaction> transactions = transactionRepository.findIdByCoordinateIsNullAndMerchantIdIsNotNull(event.memberId());

        if(transactions.isEmpty()) {
            log.info("[MerchantSyncService] 모든 거래내역의 가맹점 위치 정보가 업데이트되었습니다, memberId {}", memberId);
            return;
        }

        // 거래내역의 가맹점 정보 추출 및 해당되는 가맹점 모두 조회
        Set<Long> merchantIds = transactions.stream()
                .map(Transaction::getMerchantId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, Merchant> merchantMap = merchantRepository.findByIdIn(merchantIds).stream()
                .collect(Collectors.toMap(
                        Merchant::getId,
                        Function.identity()
                ));

        // 각 거래내역에 대해 모든 가맹점 위치 정보 업데이트
        transactions.forEach(transaction -> updateMerchantLocationInfo(transaction, merchantMap));
    }

    private void updateMerchantLocationInfo(
            Transaction transaction,
            Map<Long, Merchant> merchantMap
    ) {

        Merchant merchant = merchantMap.get(transaction.getMerchantId());
        if (merchant == null) {
            log.info("[MerchantSyncService] 가맹점 '{}' 이 존재하지 않습니다. transactionId={}", transaction.getMerchantName(), transaction.getTransactionId());
            return;
        }

        try {
            transactionRepository.updateLocationFields(transaction.getTransactionId(), merchant);
        } catch (Exception ex) {
            log.warn("[MerchantSyncService] 가맹점 위치 정보 동기화 실패, transactionId={} merchantName={}",
                    transaction.getTransactionId(), transaction.getMerchantName(), ex);
        }
    }

}
