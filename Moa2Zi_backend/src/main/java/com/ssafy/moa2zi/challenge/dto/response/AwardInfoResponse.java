package com.ssafy.moa2zi.challenge.dto.response;

import com.ssafy.moa2zi.challenge.domain.Challenge;
import com.ssafy.moa2zi.challenge.domain.ChallengeTime;
import com.ssafy.moa2zi.member.domain.Member;
import lombok.Builder;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import static com.ssafy.moa2zi.challenge.dto.response.AwardType.EXISTING;
import static com.ssafy.moa2zi.challenge.dto.response.AwardType.NEW;

@Builder
public record AwardInfoResponse(
        AwardType type,
        Long challengeTimeId,
        String title,
        String awardCategory,
        String nickName,
        String description,
        String issuedDate,
        String challengeStartDate,
        String challengeEndDate,
        String awardImage
) {

    public static AwardInfoResponse fromAwardImage(
            Long challengeTimeId,
            String awardImage
    ) {

        return AwardInfoResponse.builder()
                .type(EXISTING)
                .challengeTimeId(challengeTimeId)
                .awardImage(awardImage)
                .build();
    }

    public static AwardInfoResponse createAwardWithChallengeInfo(
            Member member,
            ChallengeInfo challengeInfo,
            AwardType awardType
    ) {

        String title = getAwardTitle();
        String awardCategory = challengeInfo.getTitle() + " 부문";

        String description = String.format(
                "%s 님은 %s 챌린지를 성공적으로 완주하였기에 이 상장을 수여합니다.",
                member.getNickname(),
                challengeInfo.getTitle()
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String issuedDate = challengeInfo.getEndTime().plusDays(1).format(formatter);
        String challengeStartDate = challengeInfo.getStartTime().format(formatter);
        String challengeEndDate = challengeInfo.getEndTime().format(formatter);

        return AwardInfoResponse.builder()
                .type(awardType)
                .challengeTimeId(challengeInfo.getChallengeTimeId())
                .title(title)
                .awardCategory(awardCategory)
                .nickName(member.getNickname())
                .description(description)
                .issuedDate(issuedDate)
                .challengeStartDate(challengeStartDate)
                .challengeEndDate(challengeEndDate)
                .build();
    }

    // 일단 상장 타이틀은 랜덤으로 부여 (추후 GPT? )
    public static String getAwardTitle() {
        List<String> awardTitles = List.of(
                "베스트 챌린저상",
                "지갑 지킴이상",
                "절약 마스터상",
                "현명한 소비자상",
                "잔고 요정상",
                "무지출의 신상",
                "알뜰살뜰상",
                "한 푼의 기적상",
                "슬기로운 가계부상",
                "절제왕상",
                "소비 컨트롤러상",
                "예산 수호자상",
                "가계부 철인상",
                "지출 정복자상",
                "검소함의 미학상",
                "돈길만 걷자상"
        );

        Random random = new Random();
        return awardTitles.get(random.nextInt(awardTitles.size()));
    }
}
