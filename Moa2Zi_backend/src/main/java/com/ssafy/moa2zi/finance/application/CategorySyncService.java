package com.ssafy.moa2zi.finance.application;

import com.google.common.net.HttpHeaders;
import com.ssafy.moa2zi.category.domain.Category;
import com.ssafy.moa2zi.category.domain.CategoryRepository;
import com.ssafy.moa2zi.category.domain.CategoryType;
import com.ssafy.moa2zi.finance.event.FinanceEvent;
import com.ssafy.moa2zi.transaction.domain.Transaction;
import com.ssafy.moa2zi.transaction.domain.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CategorySyncService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final WebClient webClient;

    public CategorySyncService(
            CategoryRepository categoryRepository,
            TransactionRepository transactionRepository,
            @Qualifier("modelApiWebClient") WebClient webClient
    ) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.webClient = webClient;
    }

    @Transactional
    public void syncCategoryWithAIModel(FinanceEvent event) {

        // 소비 카테고리 모두 가져오기
        Map<String, Long> categoryMap = categoryRepository.findByCategoryType(CategoryType.SPEND).stream()
                .collect(Collectors.toMap(Category::getCategoryName, Category::getId));

        // 분류 대상 거래내역 리스트
        List<Transaction> transactions = transactionRepository.findByCategoryIdIsNullAndMerchantNameIsNotNull();
        for (Transaction transaction : transactions) {
            String merchantName = transaction.getMerchantName();
            if (merchantName == null || merchantName.isEmpty()) {
                log.warn("[CategorySyncService] 거래처 상호명이 존재하지 않습니다. transactionId : {}", transaction.getTransactionId());
                continue;
            }

            // AI 모델 API 로부터 예측된 카테고리 받아오기
            Optional<String> predictedCategoryOpt = predictCategory(merchantName);
            if (predictedCategoryOpt.isEmpty()) {
                log.error("[CategorySyncService] 카테고리 예측 실패. transactionId: {}, merchant: {}", transaction.getTransactionId(), merchantName);
                continue;
            }

            // 분류된 카테고리 ID 로 거래내역 카테고리 업데이트
            String predictedCategory = predictedCategoryOpt.get();
            String formattedCategory = predictedCategory.contains(" ") // DB 포맷에 맞춰 변환
                    ? predictedCategory.replace(" ", "/")
                    : predictedCategory;

            Long categoryId = categoryMap.get(formattedCategory);
            if(categoryId == null) {
                log.warn("[CategorySyncService] 존재하지 않는 카테고리 분류입니다. merchant : {}, prediction : {}", transaction.getMerchantName(), formattedCategory);
                continue;
            }
            log.info("분류가 잘되었어요~~~ : {} merchant : {} transactionId : {}", categoryId, transaction.getMerchantName(), transaction.getTransactionId());
            transactionRepository.updateCategory(transaction.getTransactionId(), categoryId);
        }
    }


    /**
     * 카테고리 분류 AI 모델 호출하여 예측된 카테고리 받기
     */
    private Optional<String> predictCategory(String merchantName) {
        // API 요청에 보낼 body 데이터 생성
        Map<String, String> requestPayload = new HashMap<>();
        requestPayload.put("text", merchantName);

        try {
            Map<String, Object> response = webClient.post()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(requestPayload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("prediction")) {
                return Optional.ofNullable((String) response.get("prediction"));
            } else {
                log.error("[CategorySyncService] 예측 응답이 비어있습니다. merchant: {}", merchantName);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("[CategorySyncService] 모델 API 호출 중 오류 발생. merchant: {}", merchantName, e);
            return Optional.empty();
        }
    }

}
