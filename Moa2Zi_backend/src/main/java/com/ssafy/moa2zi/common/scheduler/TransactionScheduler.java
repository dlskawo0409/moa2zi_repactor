package com.ssafy.moa2zi.common.scheduler;

import com.ssafy.moa2zi.finance.event.FinanceEvent;
import com.ssafy.moa2zi.finance.event.FinanceEventPublisher;
import com.ssafy.moa2zi.finance.event.FinanceEventType;
import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.domain.MemberRepository;
import com.ssafy.moa2zi.transaction.application.TransactionCacheService;
import com.ssafy.moa2zi.transaction.application.TransactionService;
import com.ssafy.moa2zi.transaction.domain.Transaction;
import com.ssafy.moa2zi.transaction.domain.TransactionRepository;
import com.ssafy.moa2zi.transaction.domain.TransactionTopSpend;
import com.ssafy.moa2zi.transaction.domain.TransactionTopSpendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionScheduler {

    private final TransactionRepository transactionRepository;
    private final TransactionTopSpendRepository transactionTopSpendRepository;
    private final MemberRepository memberRepository;
    private final TransactionCacheService transactionCacheService;
    private final FinanceEventPublisher financeEventPublisher;

//    @Scheduled(cron = "10 * * * * *")
    @Scheduled(cron = "0 0 */6 * * *")
    public void updateHighSpendingTop5() {

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime oneMonthAgo = today.minusMonths(1);
        log.info("TransactionScheduler : 유저의 top5 소비 내역 배치 작업 스케줄러 시작 - {}", today);

        // 최근 한 달간 거래내역이 10개 이상인 유저 ID
        Set<Long> memberIds = findActiveMembersInMonth(oneMonthAgo);

        // 유저 별 한달 간 거래내역을 지출 금액을 내림차순으로 정렬하여 조회
        List<Transaction> transactions = findTransactionByMemberIdOrderByBalance(memberIds, oneMonthAgo);

        // [ 유저ID, top5 거래 내역 ] 매핑
        Map<Long, List<Transaction>> transactionMap = findTop5SpendingMapGroupByMember(transactions);

        // DB에 저장할 엔티티 생성
        List<TransactionTopSpend> topSpendList = new ArrayList<>();
        for(Long memberId : transactionMap.keySet()) {
            List<Transaction> transactionByMember = transactionMap.get(memberId);
            for(int i=0; i<transactionByMember.size(); i++) {
                TransactionTopSpend topSpend = TransactionTopSpend.of(
                        transactionByMember.get(i),
                        i+1); // 순위

                topSpendList.add(topSpend);
            }
        }

        // 과거 기록 지우고 bulk insert 로 갱신
        transactionTopSpendRepository.deleteAll();
        transactionRepository.bulkInsertTopSpend(topSpendList);

        // Redis 에 모두 캐싱
        transactionCacheService.clearTop5Transactions(transactionMap.keySet());
        transactionCacheService.cacheTop5Transactions(transactionTopSpendRepository.findAll());

        log.info("TransactionScheduler : 총 {} 명의 top5 소비내역 저장, 저장된 수 {}개", memberIds.size(), topSpendList.size());
        log.info("TransactionScheduler : 유저의 top5 소비 내역 배치 작업 스케줄러 종료 - {}", LocalDateTime.now());
    }

    /**
     * 5일 간격으로 새벽 4시에 유저의 거래내역 동기화 작업 수행
     */
    @Scheduled(cron = "0 0 4 */5 * *")
    public void syncAllMemberTransactions() {
        log.info("TransactionScheduler : 거래내역 정기 동기화 작업 스케줄러 시작 - {}", LocalDateTime.now());

        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            financeEventPublisher.publish(FinanceEvent.builder()
                    .memberId(member.getMemberId())
                    .type(FinanceEventType.SYNC_TRANSACTION)
                    .build());
        }

        log.info("TransactionScheduler : 거래내역 정기 동기화 작업 스케줄러 완료 - {}", LocalDateTime.now());
    }

    /*
        유저 ID로 그룹화하여 내림차순 정렬된 순으로 5개만 가져옴
     */
    private Map<Long, List<Transaction>> findTop5SpendingMapGroupByMember(List<Transaction> transactions) {
        return transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getMemberId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream().limit(5).toList() // top 5 만 추출
                        )
                ));
    }

    private List<Transaction> findTransactionByMemberIdOrderByBalance(
            Set<Long> memberIds,
            LocalDateTime dateTime
    ) {

        return transactionRepository.findByMemberIdsAndDateOrderByBalance(memberIds, convertToIntTime(dateTime));
    }

    private Set<Long> findActiveMembersInMonth(LocalDateTime dateTime) {
        return transactionRepository.findMembersSpendingCountOver(convertToIntTime(dateTime), 10);
    }

    private int convertToIntTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return Integer.parseInt(dateTime.format(formatter));
    }

}
