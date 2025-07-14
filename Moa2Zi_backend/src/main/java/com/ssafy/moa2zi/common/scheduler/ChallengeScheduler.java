package com.ssafy.moa2zi.common.scheduler;

import com.ssafy.moa2zi.category.domain.Category;
import com.ssafy.moa2zi.category.domain.CategoryRepository;
import com.ssafy.moa2zi.category.domain.CategoryType;
import com.ssafy.moa2zi.challenge.application.ChallengeService;
import com.ssafy.moa2zi.challenge.domain.*;
import com.ssafy.moa2zi.challenge.dto.request.ChallengePassCond;
import com.ssafy.moa2zi.challenge.dto.request.ChallengeRecommendCond;
import com.ssafy.moa2zi.challenge.infrastructure.GptChallengeGenerator;
import com.ssafy.moa2zi.challenge.infrastructure.GptChallengeInfo;
import com.ssafy.moa2zi.member.domain.Gender;
import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.domain.MemberRepository;
import com.ssafy.moa2zi.transaction.domain.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChallengeScheduler {

    private final CategoryRepository categoryRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeTimeRepository challengeTimeRepository;
    private final ChallengeParticipantRepository challengeParticipantRepository;
    private final ChallengeRecommendRepository challengeRecommendRepository;
    private final TransactionRepository transactionRepository;
    private final MemberRepository memberRepository;
    private final GptChallengeGenerator generator;
    private final ChallengeService challengeService;

    /**
     * 챌린지 생성 작업
     * 매주 월요일 자정
     */
//    @Scheduled(cron = "10 * * * * *")
    @Scheduled(cron = "0 0 0 ? * MON")
    public void generateChallenges() {
        log.info("ChallengeScheduler : 챌린지 생성 스케줄러 시작 - {}", LocalDateTime.now());
        List<Category> categories = categoryRepository.findByParentIdIsNullAndCategoryType(CategoryType.SPEND);
        List<GptChallengeInfo> challengeInfos = generator.generateChallengeFromGpt(categories);
        challengeService.createChallenge(challengeInfos);
        log.info("ChallengeScheduler : 챌린지 생성 스케줄러 완료 - 총 {}개 생성", challengeInfos.size());
    }

    /**
     * 매일 새벽 3시
     * 추천 챌린지 업데이트 작업
     */
//    @Scheduled(cron = "10 * * * * *")
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void refreshRecommendChallengeDaily() {
        log.info("ChallengeScheduler : 추천 챌린지 생성 스케줄러 시작 - {}", LocalDateTime.now());
        List<Member> members = memberRepository.findAll();
        if(members.isEmpty()) {
            log.info("ChallengeScheduler : 추천 대상자가 없어 스케줄러를 종료합니다.");
            return;
        }

        // 유저별 이전 3일간 추천 목록 가져오기
        Map<Long, Set<Long>> previousRecommendMap = getPreviousRecommendMapByMembers();
        List<ChallengeRecommend> allRecommends = new ArrayList<>();

        // 유저 별 추천 리스트 생성
        for(Member member : members) {
            List<ChallengeRecommend> memberRecommends = processMemberRecommendations(member, previousRecommendMap);
            allRecommends.addAll(memberRecommends);
        }

        // 3일 전 추천 목록은 삭제
        LocalDateTime cutoffDate = LocalDate.now().minusDays(2).atStartOfDay();
        challengeRecommendRepository.deleteAllByCreatedAtBefore(cutoffDate);
        challengeRecommendRepository.bulkInsert(allRecommends);
        log.info("ChallengeScheduler : 추천 챌린지 생성 스케줄러 종료 - {}", LocalDateTime.now());
    }

    /**
     * 매일 자정 동작
     * 데일리 참여자 상태 체크 및 챌린지 종료 후 상태 업데이트 작업
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void dailyChallengeCheck() {
        checkChallengeParticipantStatus();
        completeChallenges();
    }

    /*
        멤버별 추천 챌린지 처리하여 반환
     */
    private List<ChallengeRecommend> processMemberRecommendations(Member member, Map<Long, Set<Long>> previousRecommendMap) {
        Long memberId = member.getMemberId();
        int age = getAgeGroup(member.getBirthday());
        Gender gender = member.getGender();

        // 현재 유저의 가장 지출이 많았던 카테고리 리스트
        List<String> topSpendingCatogoryList = transactionRepository.findTopSpendingCategoriesByMemberId(memberId, 2);
        log.info("ChallengeScheduler : 유저 {} 의 정보, 지출 카테고리: {}, 연령대: {}, 성별: {}", memberId, topSpendingCatogoryList, age, gender);

        // 유저 정보를 반영한 추천 조건 생성
        List<ChallengeRecommendCond> conditions = generateRecommendCondByMemberInfo(topSpendingCatogoryList, age, gender);
        Set<Long> recommendedChallengeTimeIds = new LinkedHashSet<>();
        Set<Long> previousRecommendations = previousRecommendMap.getOrDefault(memberId, Collections.emptySet());

        // 추천 조건에 따른 챌린지 조회
        for (ChallengeRecommendCond cond : conditions) {
            List<Long> ids = challengeRepository.findTopNChallengesByCond(memberId, cond, 10, previousRecommendations);
            recommendedChallengeTimeIds.addAll(ids);
            if (recommendedChallengeTimeIds.size() >= 10) {
                break;
            }
        }

        if (recommendedChallengeTimeIds.isEmpty()) {
            return Collections.emptyList();
        }

        return recommendedChallengeTimeIds.stream()
                .limit(10)
                .map(challengeTimeId -> ChallengeRecommend.createChallengeRecommendOf(challengeTimeId, memberId))
                .collect(Collectors.toList());
    }


    /**
     * 챌린지 참여자 데일리 상태 체크
     */
    private void checkChallengeParticipantStatus() {
        LocalDateTime now = LocalDateTime.now();
        log.info("ChallengeScheduler : 챌린지 참여 멤버의 데일리 상태 체크 시작 - {}", LocalDateTime.now());

        // 현재 진행 중인 챌린지 가져오기
        List<ChallengeTime> activeChallengeTimes = challengeTimeRepository.findByStartTimeIsBeforeAndEndTimeIsAfter(now, now);
        if(activeChallengeTimes.isEmpty()) {
            log.info("ChallengeScheduler : 진행 중인 챌린지가 없어 스케줄러를 종료합니다.");
            return;
        }
        log.info("ChallengeScheduler : 진행 중인 챌린지 목록 개수 : {}", activeChallengeTimes.size());

        Map<Long, Challenge> challengeMap = getChallengeMapByTimeIds(activeChallengeTimes);
        List<Long> failedParticipantIds = new ArrayList<>();

        for(ChallengeTime challengeTime : activeChallengeTimes) {
            List<ChallengeParticipant> participants = challengeParticipantRepository
                    .findByChallengeTimeIdAndCheckRequiredTrue(challengeTime.getId());

            // 챌린지를 통과한 참여자 ID
            Set<Long> passedParticipantIds = new HashSet<>(
                    checkPassedParticipantToday(
                            participants,
                            challengeTime,
                            challengeMap.get(challengeTime.getChallengeId()),
                            now
                    )
            );

            // 실패한 참여자만 필터링
            List<Long> failedIds =  participants.stream()
                    .map(ChallengeParticipant::getId)
                    .filter(id -> !passedParticipantIds.contains(id))
                    .toList();
            failedParticipantIds.addAll(failedIds);
        }

        // 실패한 참여자들의 상태 업데이트
        if(!failedParticipantIds.isEmpty()) {
            challengeParticipantRepository.updateCheckRequiredFalseByIds(failedParticipantIds);
        }
    }

    /**
     * 어제 종료된 챌린지에 대해 성공/실패 처리 업데이트
     */
    private void completeChallenges() {
        LocalDateTime now = LocalDateTime.now();
        log.info("ChallengeScheduler : 챌린지 완료 작업 시작 - {}", now);

        // 어제 일자에 종료된 챌린지 조회
        LocalDateTime startOfYesterday = now.toLocalDate().minusDays(1).atStartOfDay();
        LocalDateTime endOfYesterday = now.toLocalDate().atStartOfDay();
        List<ChallengeTime> finishedChallengeTimes = challengeTimeRepository.findByEndTimeIsBetween(startOfYesterday, endOfYesterday);

        if(finishedChallengeTimes.isEmpty()) {
            log.info("ChallengeScheduler : 어제 일자에 완료된 챌린지가 없어 스케줄러를 종료합니다.");
            return;
        }

        // 성공한 참가자, 실패한 참가자 분류
        List<Long> successParticipantIds = new ArrayList<>();
        List<Long> failedParticipantIds = new ArrayList<>();

        for(ChallengeTime challengeTime : finishedChallengeTimes) {
            List<ChallengeParticipant> participants = challengeParticipantRepository.findByChallengeTimeId(challengeTime.getId());

            Map<Boolean, List<Long>> partitioned = participants.stream()
                    .collect(Collectors.partitioningBy(
                            ChallengeParticipant::getCheckRequired,
                            Collectors.mapping(ChallengeParticipant::getId, Collectors.toList())
                    ));

            successParticipantIds.addAll(partitioned.getOrDefault(true, List.of()));
            failedParticipantIds.addAll(partitioned.getOrDefault(false, List.of()));
        }

        log.info("ChallengeScheduler : 성공한 참여자 수 {}명, 실패한 참여자 수 {}명", successParticipantIds.size(), failedParticipantIds.size());
        if(!successParticipantIds.isEmpty()) {
            challengeParticipantRepository.updateStatusByIds(Status.SUCCESS, successParticipantIds);
        }
        if(!failedParticipantIds.isEmpty()) {
            challengeParticipantRepository.updateStatusByIds(Status.FAIL, failedParticipantIds);
        }
    }


    /**
     * 멤버별 이전 추천 내역을 조회하여 그룹핑한 Map 반환 (memberId -> Set of challengeTimeId)
     */
    private Map<Long, Set<Long>> getPreviousRecommendMapByMembers() {
        return challengeRecommendRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        ChallengeRecommend::getMemberId,
                        Collectors.mapping(ChallengeRecommend::getChallengeTimeId, Collectors.toSet())
                ));
    }

    private int getAgeGroup(LocalDateTime birthday) {
        return ((LocalDate.now().getYear() - birthday.getYear() + 1) / 10) * 10;
    }

    // 추천 챌린지 조건 생성 (fallback 반영)
    private List<ChallengeRecommendCond> generateRecommendCondByMemberInfo(List<String> topSpendingCatogoryList, Integer age, Gender gender) {
        List<ChallengeRecommendCond> result = new ArrayList<>();
        if(age == null || gender == null) {
            log.info("ChallengeScheduler : 연령과 성별은 추천 시 필수 항목입니다.");
            return List.of();
        }

        if(!topSpendingCatogoryList.isEmpty()) {

            // 카테고리 + 연령 + 성별
            result.add(ChallengeRecommendCond.builder()
                    .topSpendingCategoryList(topSpendingCatogoryList)
                    .age(age)
                    .gender(gender)
                    .excludePrevious(true)
                    .build());

            // 카테고리 + 연령
            result.add(ChallengeRecommendCond.builder()
                    .topSpendingCategoryList(topSpendingCatogoryList)
                    .age(age)
                    .excludePrevious(true)
                    .build());

            // 카테고리
            result.add(ChallengeRecommendCond.builder()
                    .topSpendingCategoryList(topSpendingCatogoryList)
                    .excludePrevious(true)
                    .build());
        }
        // 연령 + 성별
        result.add(ChallengeRecommendCond.builder()
                .age(age)
                .gender(gender)
                .excludePrevious(false)
                .build());

        // global
        result.add(ChallengeRecommendCond.builder().excludePrevious(false).build());
        return result;
    }

    private Map<Long, Challenge> getChallengeMapByTimeIds(List<ChallengeTime> challengeTimes) {
        List<Long> challengeTimeIds = challengeTimes
                .stream()
                .map(ChallengeTime::getId)
                .toList();
        List<Challenge> challenges = challengeRepository.findChallengesInChallengeIds(challengeTimeIds);
        return challenges.stream()
                .collect(Collectors.toMap(Challenge::getId, Function.identity()));
    }

    /**
     * 해당 날짜에 각 챌린지에 대해 참여자의 당일 통과 여부를 체크
     */
    private List<Long> checkPassedParticipantToday(
            List<ChallengeParticipant> participants,
            ChallengeTime challengeTime,
            Challenge challenge,
            LocalDateTime now
    ) {

        List<Long> participantIds = participants.stream()
                .map(ChallengeParticipant::getId)
                .toList();

        return switch(challenge.getChallengeType()) {
            case 1,2,3,4,5 -> {

                ChallengePassCond challengePassCond = ChallengePassCond.builder()
                        .challengeType(challenge.getChallengeType())
                        .amount(challenge.getAmount())
                        .percent(challenge.getPercent())
                        .limitCount(challenge.getLimitCount())
                        .categoryName(challenge.getCategoryName())
                        .build();

                yield challengeRepository.findIdsByChallengePassCond(challengePassCond, participantIds, now);
            }
            case 6 -> {

                LocalDateTime startOfLastMonthBeforeChallenge = challengeTime.getStartTime().minusMonths(1);

                // 지난 한달 총 지출
                Map<Long, Long> lastTotalSpending = transactionRepository
                        .findTotalSpendingBetween(startOfLastMonthBeforeChallenge, startOfLastMonthBeforeChallenge.plusMonths(1));

                // 챌린지 기간 동안의 총 지출
                Map<Long, Long> currentTotalSpending = transactionRepository
                        .findTotalSpendingBetween(challengeTime.getStartTime(), challengeTime.getStartTime().plusMonths(1));

                yield findPassedParticipantsIdsByComparingSpending( // 지난 한 달과 지출 비교
                        participantIds,
                        lastTotalSpending,
                        currentTotalSpending,
                        challenge.getPercent()
                );
            }

            default -> throw new IllegalArgumentException("존재하지 않는 챌린지 유형입니다.");
        };
    }


    private List<Long> findPassedParticipantsIdsByComparingSpending(
            List<Long> participantIds,
            Map<Long, Long> lastTotalSpending,
            Map<Long, Long> currentTotalSpending,
            int percent
    ) {

        return participantIds.stream()
                .filter(memberId -> {
                    long last = lastTotalSpending.getOrDefault(memberId, 0L);
                    long current = currentTotalSpending.getOrDefault(memberId, 0L);
                    if (last == 0) return false;
                    return current <= last * (percent / 100.0);
                })
                .toList();
    }

}
