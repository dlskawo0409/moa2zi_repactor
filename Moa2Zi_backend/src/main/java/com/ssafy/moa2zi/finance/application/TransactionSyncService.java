package com.ssafy.moa2zi.finance.application;

import com.ssafy.moa2zi.account.domain.Account;
import com.ssafy.moa2zi.account.domain.AccountRepository;
import com.ssafy.moa2zi.card.domain.Card;
import com.ssafy.moa2zi.card.domain.CardRepository;
import com.ssafy.moa2zi.common.util.AESUtil;
import com.ssafy.moa2zi.day.domain.Day;
import com.ssafy.moa2zi.day.domain.DayRepository;
import com.ssafy.moa2zi.finance.dto.card.CardTransactionGetRequest;
import com.ssafy.moa2zi.finance.dto.card.CardTransactionGetResponse;
import com.ssafy.moa2zi.finance.dto.transaction.*;
import com.ssafy.moa2zi.finance.event.FinanceEvent;
import com.ssafy.moa2zi.finance.event.FinanceEventPublisher;
import com.ssafy.moa2zi.finance.event.FinanceEventType;
import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.domain.MemberRepository;
import com.ssafy.moa2zi.transaction.domain.Transaction;
import com.ssafy.moa2zi.transaction.domain.TransactionRepository;
import com.ssafy.moa2zi.transaction.domain.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionSyncService {

    private final FinanceAdminService financeAdminService;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final DayRepository dayRepository;
    private final TransactionRepository transactionRepository;
    private final AESUtil aesUtil;

    private final FinanceCacheService financeCacheService;
    // private final FinanceEventPublisher financeEventPublisher;
    private final FinanceProducer financeProducer;


    /**
     * 금융망 API 로부터 거래내역 조회하여 DB 에 저장
     * 가맹점 연동 이벤트 발생
     */
    @Transactional
    public void fetchAndSaveTransactionFromApi(FinanceEvent event) throws Exception {
        Long memberId = event.memberId();
        List<Transaction> fetchedTransactions = new ArrayList<>();

        boolean lockAcquired = false;
        try {

            // 현재 동기화 작업이 수행되고 있는지 확인, 없다면 Redis 키 생성
            lockAcquired = !financeCacheService.isProcessingSyncTask(memberId);
            if(!lockAcquired) {
                log.warn("[TransactionSyncService] 이미 거래내역 동기화 작업을 처리 중입니다. memberId : {}", memberId);
                return;
            }

            // 계좌 거래내역 가져오기
            fetchedTransactions.addAll(processAccountTransactions(memberId));

            // 카드 결제내역 가져오기
            fetchedTransactions.addAll(processCardTransactions(memberId));

            if(fetchedTransactions.isEmpty()) {
                log.info("[TransactionSyncService] 새로운 거래내역이 없습니다. memberId: {}, 현재 시간 : {} ",  memberId, LocalDateTime.now());
                return;
            }

            // 새로운 거래내역 bulk insert
            transactionRepository.bulkInsertTransaction(fetchedTransactions);

            // 가맹점 연동 작업 이벤트 발행
            financeProducer.send(FinanceEvent.builder()
                    .memberId(memberId)
                    .type(FinanceEventType.SYNC_MERCHANT)
                    .build());
            // AI 카테고리 분류 작업 이벤트 발행
            financeProducer.send(FinanceEvent.builder()
                    .memberId(memberId)
                    .type(FinanceEventType.SYNC_CATEGORY_AI)
                    .transactions(fetchedTransactions)
                    .build());

        } finally {
            if(lockAcquired) {
                financeCacheService.clearProcessing(memberId);
            }
        }
    }

    /*
        API 호출해서 거래내역 가져온 후 저장
     */
    private List<Transaction> processAccountTransactions(Long memberId) throws Exception {
        List<Account> accountList = accountRepository.findByMemberId(memberId);
        List<Transaction> transactions = new ArrayList<>();
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        for(Account account : accountList) {
            try {
                String accountNo = aesUtil.decrypt(account.getAccountNo());
                String lastTransactionDate = account.getLastTransactionDate();
                String lastTransactionTime = account.getLastTransactionTime();
                TransactionHistoryRequest request = new TransactionHistoryRequest(
                        accountNo,
                        lastTransactionDate,
                        today,
                        TransactionApiType.A,
                        OrderByType.ASC
                );
                // API 로부터 거래내역 조회
                TransactionHistoryResponse response = financeAdminService.getTransactionHistory(request, memberId);
                transactions.addAll(createAccountTransaction(account, memberId, lastTransactionTime, response));
            } catch (Exception e) {
                log.error("[TransactionSyncService] 계좌 거래내역 처리 실패, accountId: {}, memberId: {}", account.getId(), memberId, e);
            }
        }

        return transactions;
    }

    private List<Transaction> processCardTransactions(Long memberId) throws Exception {
        List<Card> cards = cardRepository.findByMemberId(memberId);
        List<Transaction> transactions = new ArrayList<>();
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        for (Card card : cards) {
            try {
                String cardNo = aesUtil.decrypt(card.getCardNo());
                String cvc = card.getCvc();
                String lastTransactionDate = card.getLastTransactionDate();
                String lastTransactionTime = card.getLastTransactionTime();
                CardTransactionGetRequest request = new CardTransactionGetRequest(
                        cardNo,
                        cvc,
                        lastTransactionDate,
                        today
                );
                // API 로부터 거래내역 조회
                CardTransactionGetResponse response = financeAdminService.getCardTransactionByMember(memberId, request);
                transactions.addAll(createCardTransaction(card, memberId, lastTransactionTime, response));
            } catch (Exception e) {
                log.error("[TransactionSyncService] 카드 거래내역 처리 실패, cardId: {}, memberId: {}", card.getId(), memberId, e);
            }
        }

        return transactions;
    }

    private List<Transaction> createAccountTransaction(
            Account account,
            Long memberId,
            String lastTransactionTime,
            TransactionHistoryResponse response
    ) {

        if(response.list() == null) {
            return List.of();
        }

        List<Transaction> transactions = new ArrayList<>();

        // 필요한 거래내역 날짜 집합
        Set<String> transactionDates = response.list().stream()
                .map(TransactionHistoryResponse.TransactionInfo::transactionDate)
                .collect(Collectors.toSet());
        Map<Integer, Day> dayMap = getOrCreateDays(memberId, transactionDates);

        // 마지막 거래내역 동기화 시점 이후의 거래내역만 필터링
        List<TransactionHistoryResponse.TransactionInfo> filteredList = response.list().stream()
                .filter(tx -> isNewTransaction(
                        tx.transactionTime(),
                        tx.transactionDate(),
                        account.getLastTransactionTime(),
                        account.getLastTransactionDate()
                ))
                .toList();


        for(TransactionHistoryResponse.TransactionInfo transactionInfo : filteredList) {
            int transactionDate = Integer.parseInt(transactionInfo.transactionDate());
            Transaction transaction = Transaction.builder()
                    .transactionUniqueNo(transactionInfo.transactionUniqueNo())
                    .memberId(memberId)
                    .dayId(dayMap.get(transactionDate).getId())
                    .accountNo(account.getAccountNo())
                    .balance(transactionInfo.transactionBalance())
                    .transactionType(convertTransactionType(transactionInfo.transactionType()))
                    .transactionTime(transactionInfo.transactionTime())
                    .paymentMethod(account.getAccountName())
                    .memo(transactionInfo.transactionMemo())
                    .isInBudget(true)
                    .build();

            transactions.add(transaction);
        }

        // 최신 거래 시간 계산
        filteredList.stream()
                .map(TransactionHistoryResponse.TransactionInfo::transactionTime)
                .max(String::compareTo)
                .ifPresent(account::updateLastTransactionTime);

        return transactions;
    }

    private List<Transaction> createCardTransaction(
            Card card,
            Long memberId,
            String lastTransactionTime,
            CardTransactionGetResponse response
    ) {

        if(response.transactionList() == null) {
            return List.of();
        }

        List<Transaction> transactions = new ArrayList<>();
        Set<String> transactionDates = response.transactionList().stream()
                .map(CardTransactionGetResponse.TransactionInfo::transactionDate)
                .collect(Collectors.toSet());
        Map<Integer, Day> dayMap = getOrCreateDays(memberId, transactionDates);

        // 마지막 거래내역 동기화 시점 이후의 거래내역만 필터링
        List<CardTransactionGetResponse.TransactionInfo> filteredList = response.transactionList().stream()
                .filter(tx -> isNewTransaction(
                        tx.transactionTime(),
                        tx.transactionDate(),
                        card.getLastTransactionTime(),
                        card.getLastTransactionDate()
                ))
                .toList();

        for(CardTransactionGetResponse.TransactionInfo transactionInfo : filteredList) {
            int transactionDate = Integer.parseInt(transactionInfo.transactionDate());
            String merchantName = extractMerchantName(transactionInfo.merchantName());
            Long merchantId = Long.valueOf(extractMerchantId(transactionInfo.merchantName()));
            Transaction transaction = Transaction.builder()
                    .transactionUniqueNo(transactionInfo.transactionUniqueNo())
                    .memberId(memberId)
                    .dayId(dayMap.get(transactionDate).getId())
                    .cardNo(card.getCardNo())
                    .balance(transactionInfo.transactionBalance())
                    .transactionType(TransactionType.SPEND)
                    .transactionTime(transactionInfo.transactionTime())
                    .paymentMethod(card.getCardName())
                    .merchantId(merchantId)
                    .merchantName(merchantName) // 상호명 반드시
                    .isInBudget(true)
                    .build();

            transactions.add(transaction);
        }

        // 최신 거래 시간 계산
        filteredList.stream()
                .map(CardTransactionGetResponse.TransactionInfo::transactionTime)
                .max(String::compareTo)
                .ifPresent(card::updateLastTransactionTime);

        return transactions;
    }

    private boolean isNewTransaction(
            String txTime,
            String txDate,
            String lastSyncTime,
            String lastSyncDate
    ) {

        if (txDate.compareTo(lastSyncDate) > 0) {
            return true;
        } else if (txDate.equals(lastSyncDate)) {
            return txTime.compareTo(lastSyncTime) > 0;
        } else {
            return false;
        }
    }

    private String extractMerchantName(String raw) {
        if (raw == null || raw.isBlank()) return "";

        String[] parts = raw.trim().split("\\s+");
        if (parts.length <= 1) return raw;
        return String.join(" ", Arrays.copyOf(parts, parts.length - 1));
    }

    private String extractMerchantId(String raw) {
        if (raw == null || raw.isBlank()) return "";

        String[] parts = raw.split(" ");
        return parts[parts.length - 1];
    }

    /**
     * 주어진 거래내역 날짜를 조회하거나 없으면 생성
     */
    private Map<Integer, Day> getOrCreateDays(Long memberId, Set<String> transactionDates) {
        Map<Integer, Day> dayMap = new HashMap<>();
        for (String date : transactionDates) {
            int dateInt = Integer.parseInt(date);
            if (!dayMap.containsKey(dateInt)) {
                Day day = dayRepository.findDayByTransactionDateAndMemberId(dateInt, memberId)
                        .orElseGet(() -> dayRepository.save(
                                Day.builder()
                                        .memberId(memberId)
                                        .transactionDate(dateInt)
                                        .build()
                        ));
                dayMap.put(dateInt, day);
            }
        }
        return dayMap;    }

    private TransactionType convertTransactionType(TransactionApiType apiType) {
        return (TransactionApiType.M.equals(apiType)) ? TransactionType.INCOME : TransactionType.SPEND;
    }

}
