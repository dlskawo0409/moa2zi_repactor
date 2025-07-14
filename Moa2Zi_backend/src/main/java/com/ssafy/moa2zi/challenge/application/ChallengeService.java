package com.ssafy.moa2zi.challenge.application;

import com.ssafy.moa2zi.challenge.domain.*;
import com.ssafy.moa2zi.challenge.dto.request.*;
import com.ssafy.moa2zi.challenge.dto.response.*;
import com.ssafy.moa2zi.challenge.infrastructure.GptChallengeInfo;
import com.ssafy.moa2zi.common.storage.application.ImageUtil;
import com.ssafy.moa2zi.common.storage.application.StorageService;
import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.domain.MemberRepository;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import com.ssafy.moa2zi.notification.application.NotificationProducer;
import com.ssafy.moa2zi.notification.domain.NotificationMessage;
import com.ssafy.moa2zi.notification.domain.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.ssafy.moa2zi.challenge.domain.Status.SUCCESS;
import static com.ssafy.moa2zi.challenge.dto.response.AwardType.EXISTING;
import static com.ssafy.moa2zi.challenge.dto.response.AwardType.NEW;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeTimeRepository challengeTimeRepository;
    private final ChallengeParticipantRepository challengeParticipantRepository;
    private final ChallengeReviewLikeRepository challengeReviewLikeRepository;
    private final MemberRepository memberRepository;
    private final ImageUtil imageUtil;
    private final NotificationProducer notificationProducer;

    @Transactional
    public void createChallenge(List<GptChallengeInfo> challengeInfos) {
        LocalDateTime now = LocalDateTime.now();

        for(GptChallengeInfo challengeInfo : challengeInfos) {
            Challenge challenge = Challenge.createChallenge(challengeInfo);
            challengeRepository.save(challenge);

            LocalDate startDate = now.toLocalDate().plusDays(3); // 현재 시간 기준 3일 뒤 챌린지 시작
            LocalDateTime startTime = startDate.atTime(9, 0); // 항상 아침 9시 오픈
            LocalDateTime endTime = startDate.plusDays(challengeInfo.period()).atStartOfDay(); // 자정

            ChallengeTime challengeTime = ChallengeTime
                    .openChallengeTimeOf(
                            challenge.getId(),
                            startTime,
                            endTime
                    );

            challengeTimeRepository.save(challengeTime);
        }
    }

    public ChallengeSearchResponse getChallenges(
            ChallengeSearchRequest request,
            CustomMemberDetails loginMember
    ) {

        return switch (request.type()) {
            case MINE -> challengeRepository.findChallengesByMember(request, loginMember);
            case SEARCH -> challengeRepository.findChallenges(request, loginMember);
            case RECOMMEND -> challengeRepository.findRecommendChallengesByMember(loginMember, 10);
            case POPULAR -> challengeRepository.findPopularChallenges(3);
            default -> throw new IllegalArgumentException("지원하지 않는 요청 타입입니다: " + request.type());
        };
    }

    public ParticipantGetResponse getParticipants(
            Long challengeId,
            ParticipantGetRequest request,
            CustomMemberDetails loginMember
    ) {

        return challengeRepository.findParticipants(challengeId, request, loginMember);
    }

    @Transactional
    public void joinChallenge(
            Long challengeTimeId,
            CustomMemberDetails loginMember
    ) throws Exception {

        ChallengeTime challengeTime = findChallengeTimeById(challengeTimeId);
        validateBeforeChallengeStart(challengeTime);
        checkCurrentlyOngoingChallenge(loginMember.getMemberId(), challengeTime.getChallengeId());
        Member member = findMemberById(loginMember.getMemberId());
        ChallengeParticipant participant = ChallengeParticipant.createWithChallengeTimeAndMember(challengeTimeId, member);
        challengeParticipantRepository.save(participant);
        challengeRepository.updateParticipantCount(challengeTime.getChallengeId());
    }

    @Transactional
    public void createReview(
            Long challengeTimeId,
            ChallengeReviewCreateRequest request,
            CustomMemberDetails loginMember
    ) throws Exception {

        ChallengeParticipant participant = findParticipantByMemberIdAndChallengeTimeId(loginMember.getMemberId(), challengeTimeId);
        validateReviewPermission(participant);
        isAlreadyReviewed(participant);
        participant.createReview(request);
    }

    // 특정 챌린지 상장 조회
    public AwardInfoResponse getAwardByChallengeTimeId(
            Long challengeTimeId,
            CustomMemberDetails loginMember
    ) throws Exception {

        ChallengeParticipant challengeParticipant = checkAndFindSuccessParticipant(challengeTimeId, loginMember.getMemberId());
        AwardType awardType = (challengeParticipant.getIsRead()) ? EXISTING : NEW;
        Member member = findMemberById(loginMember.getMemberId());
        ChallengeInfo challengeInfo = challengeRepository.findChallengeInfoByParticipantId(challengeParticipant.getId());
        return AwardInfoResponse.createAwardWithChallengeInfo(member, challengeInfo, awardType);
    }

    // 나의 모든 상장 조회
    public List<AwardInfoResponse> getAwardsByMember(Long memberId) {
        Member member = findMemberById(memberId);
        List<ChallengeParticipant> challengeParticipants = challengeRepository.findSuccessParticipantsByMemberId(memberId);

        return challengeParticipants.stream()
                .map(challengeParticipant -> converToAwardInfoResponse(member, challengeParticipant))
                .toList();
    }

    private AwardInfoResponse converToAwardInfoResponse(
            Member member,
            ChallengeParticipant challengeParticipant
    ) {

        AwardType awardType = (challengeParticipant.getIsRead()) ? EXISTING : NEW;
        ChallengeInfo challengeInfo = challengeRepository.findChallengeInfoByParticipantId(challengeParticipant.getId());
        return AwardInfoResponse.createAwardWithChallengeInfo(member, challengeInfo, awardType);
    }

    @Transactional
    public void addAwardImage(
            AwardCreateImageRequest request,
            CustomMemberDetails loginMember
    ) throws Exception {

        ChallengeParticipant challengeParticipant = checkAndFindSuccessParticipant(
                request.challengeTimeId(), loginMember.getMemberId()
        );
        // 상장 이미지 S3 저장
        String imageKey = imageUtil.CovertToWebpAndStore(request.image(), "award");
        String awardImageUrl = imageUtil.getPreSignedUrl(imageKey);
        challengeParticipant.updateAwardImage(awardImageUrl);
    }

    @Transactional
    public void readAward(
            Long challengeTimeId,
            CustomMemberDetails loginMember
    ) throws Exception {

        ChallengeParticipant challengeParticipant = checkAndFindSuccessParticipant(challengeTimeId, loginMember.getMemberId());
        challengeParticipant.markAsRead(); // 읽음 표시
    }

    private ChallengeParticipant checkAndFindSuccessParticipant(Long challengeTimeId, Long memberId) throws IllegalAccessException {
        ChallengeParticipant challengeParticipant = findParticipantByMemberIdAndChallengeTimeId(memberId, challengeTimeId);
        if(!challengeParticipant.getStatus().equals(SUCCESS)) {
            throw new IllegalAccessException("해당 챌린지에 성공한 참여자가 아닙니다, challengeTimeId = " + challengeTimeId + " memberId = " + memberId);
        }
        return challengeParticipant;
    }

    private Challenge findChallengeByChallengeId(Long challengeId) {
        return challengeRepository.findById(challengeId)
                .orElseThrow(() -> new NotFoundException("해당 챌린지는 존재하지 않습니다, challengeId = " + challengeId));
    }

    private void isAlreadyReviewed(ChallengeParticipant participant) throws IllegalAccessException {
        if(participant.getReview() != null && !participant.getReview().isBlank()) {
            throw new IllegalAccessException("이미 리뷰를 작성한 이력이 있습니다.");
        }
    }

    private void validateReviewPermission(ChallengeParticipant participant) throws IllegalAccessException {
        if(!participant.getStatus().equals(SUCCESS)) {
            throw new IllegalAccessException("해당 챌린지에 성공한 이력이 없습니다.");
        }
    }

    private ChallengeParticipant findParticipantByMemberIdAndChallengeTimeId(Long memberId, Long challengeTimeId) {
        return challengeRepository.findParticipantByMemberIdAndChallengeTimeId(memberId, challengeTimeId)
                .orElseThrow(() -> new NotFoundException("해당 유저가 현재 챌린지에 참여한 이력이 없습니다."));
    }

    private void validateBeforeChallengeStart(ChallengeTime challengeTime) throws IllegalAccessException {
        if (LocalDateTime.now().isAfter(challengeTime.getStartTime())) {
            throw new IllegalAccessException("참여할 수 있는 기간이 아닙니다.");
        }
    }

    private void checkCurrentlyOngoingChallenge(Long memberId, Long challengeId) throws IllegalAccessException {
        if(challengeRepository.existsOngoingByMemberIdAndChallengeId(memberId, challengeId)) {
            throw new IllegalAccessException("현재 해당 챌린지를 이미 진행 중입니다.");
        }
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(memberId + " 의 유저를 찾을 수 없습니다."));
    }

    private ChallengeTime findChallengeTimeById(Long challengeTimeId) {
        return challengeTimeRepository.findById(challengeTimeId)
                .orElseThrow(() -> new NotFoundException(challengeTimeId + " 의 챌린지 타임을 찾을 수 없습니다."));
    }

    @Transactional
    public void toggleReviewLike(
            Long challengeParticipantId,
            CustomMemberDetails loginMember
    ) throws Exception {

        ChallengeParticipant participant = findParticipantById(challengeParticipantId);
        checkExistsReview(participant);
        if(isAlreadyLiked(loginMember.getMemberId(), challengeParticipantId)) {
            challengeReviewLikeRepository.deleteByMemberIdAndChallengeParticipantId(loginMember.getMemberId(), challengeParticipantId);
        } else {
            ChallengeReviewLike challengeReviewLike = ChallengeReviewLike.of(challengeParticipantId, loginMember.getMemberId());
            challengeReviewLikeRepository.save(challengeReviewLike);

            notificationProducer.send(NotificationMessage.builder()
                    .senderId(loginMember.getMemberId())
                    .receiverId(participant.getMemberId())
                    .notificationType(NotificationType.REVIEW_LIKE)
                    .build());

        }
    }

    private void checkExistsReview(ChallengeParticipant participant) throws IllegalAccessException {
        if(participant.getReview() == null || participant.getReviewedAt() == null) {
            throw new IllegalAccessException("해당 참여자의 리뷰가 존재하지 않습니다.");
        }
    }

    private ChallengeParticipant findParticipantById(Long challengeParticipantId) {
        return challengeParticipantRepository.findById(challengeParticipantId)
                .orElseThrow(() -> new NotFoundException("findParticipantById, challengeParticipantId = " + challengeParticipantId));
    }

    private boolean isAlreadyLiked(Long memberId, Long challengeParticipantId) {
        return challengeReviewLikeRepository.existsByMemberIdAndChallengeParticipantId(memberId, challengeParticipantId);
    }

    public ReviewGetResponse getChallengeReviews(
            Long challengeId,
            ReviewGetRequest request,
            CustomMemberDetails loginMember
    ) {

        return challengeRepository.findReviews(challengeId, request, loginMember);
    }

}
