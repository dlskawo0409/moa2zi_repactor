package com.ssafy.moa2zi.challenge.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.moa2zi.category.domain.Category;
import com.ssafy.moa2zi.challenge.application.ChallengeService;
import com.ssafy.moa2zi.challenge.domain.ChallengeRepository;
import com.ssafy.moa2zi.challenge.domain.ChallengeTimeRepository;
import com.ssafy.moa2zi.common.infrastructure.gpt.GptClient;
import com.ssafy.moa2zi.common.infrastructure.gpt.GptRequestOptions;
import com.ssafy.moa2zi.common.infrastructure.gpt.GptResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.ssafy.moa2zi.challenge.infrastructure.ChallengeFormatType.fromTypeNum;

/*
open ai 사용하여 챌린지 생성
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GptChallengeGenerator {

    private final GptClient gptClient;

    @Retryable(
            retryFor = { RuntimeException.class },
            maxAttempts = 3, // 최대 3번 재시도
            backoff = @Backoff(delay = 1500, multiplier = 2) // 1.5초 후 재시도, 지수 백오프 적용
    )
    public List<GptChallengeInfo> generateChallengeFromGpt(List<Category> categories) {

        // 카테고리를 반영한 프롬프팅 만들기
        String prompt = generatePrompt(categories);

        // open ai 요청, 응답
        GptResponse response = gptClient.send(
                prompt,
                GptRequestOptions.builder()
                        .model("gpt-4o")
                        .temperature(0.8F)
                        .frequencyPenalty(1.0F)
                        .presencePenalty(1.0F)
                        .maxTokens(500)
                        .responseFormat("json_object")
                        .build()
        );

        log.info("챌린지 생성 요청, GPT 응답: \n{}", response);

        List<GptChallengeInfo> challenges = convertToGptChallengeInfoList(response);
        checkChallengeFormat(challenges);

        // 응답 문자열을 challenge dto 로 역직렬화하여 반환
        return challenges;
    }

    private void checkChallengeFormat(List<GptChallengeInfo> challenges) {
        for(GptChallengeInfo challenge : challenges) {
            ChallengeFormatType formatType = fromTypeNum(challenge.challengeType())
                    .orElseThrow(() -> new NotFoundException("[GptChallengeGenerator] 존재하지 않는 챌린지 타입 : " + challenge.challengeType()));

            Integer period = challenge.period();
            if(period == null) {
                throw new IllegalArgumentException("챌린지의 period 값이 존재하지 않습니다.");
            }

            if (period < formatType.getMinPeriod() || period > formatType.getMaxPeriod()) {
                throw new IllegalArgumentException("챌린지 기간이 유효하지 않습니다: " + period);
            }

            switch (formatType) {
                case DAILY_BUDGET -> {
                    if (challenge.amount() == null || !formatType.getValidAmounts().contains(challenge.amount().intValue())) {
                        throw new IllegalArgumentException("일일 예산이 유효하지 않습니다: " + challenge.amount());
                    }
                }

                case TOTAL_BUDGET -> {
                    int minAmount = challenge.period() * 10000;
                    int maxAmount = challenge.period() * 30000;

                    if (challenge.amount() == null || challenge.amount() < minAmount || challenge.amount() > maxAmount) {
                        throw new IllegalArgumentException("총 예산이 없거나 범위에 유효하지 않습니다.");
                    }
                }

                case CATEGORY_DAILY_ZERO -> {
                    if (challenge.category() == null || challenge.category().isBlank()) {
                        throw new IllegalArgumentException("카테고리가 누락되었습니다.");
                    }

                    if(challenge.amount() == null) {
                        throw new IllegalArgumentException("일일 예산이 유효하지 않습니다");
                    }
                }

                case CATEGORY_TOTAL_BUDGET -> {
                    if (challenge.category() == null || challenge.category().isBlank()) {
                        throw new IllegalArgumentException("카테고리가 누락되었습니다.");
                    }

                    int minAmount = challenge.period() * 10000;
                    int maxAmount = challenge.period() * 30000;
                    if (challenge.amount() == null || challenge.amount() < minAmount || challenge.amount() > maxAmount) {
                        throw new IllegalArgumentException("총 예산이 없거나 범위에 유효하지 않습니다.");
                    }
                }

                case CATEGORY_LIMIT_COUNT -> {
                    if (challenge.category() == null || challenge.category().isBlank()) {
                        throw new IllegalArgumentException("카테고리가 누락되었습니다.");
                    }

                    if (challenge.limitCount() == null) {
                        throw new IllegalArgumentException("카운트 제한 값이 필요합니다.");
                    }

                    int countAllowed;
                    if (challenge.period() <= 7) {
                        countAllowed = (int) Math.floor(challenge.period() / 2.0); // 절반
                    } else {
                        countAllowed = (int) Math.floor(challenge.period() * (2.0 / 3.0)); // 2/3
                    }

                    if (challenge.limitCount() != countAllowed) {
                        throw new IllegalArgumentException("유효한 제한 횟수 범위가 아닙니다.");
                    }
                }

                case REDUCE_SPENDING -> {
                    if (challenge.percent() == null || challenge.percent() < 10 || challenge.percent() > 40) {
                        throw new IllegalArgumentException("퍼센트 값이 유효하지 않습니다: " + challenge.percent());
                    }
                }
            }

            if(challenge.tags() == null || challenge.tags().isEmpty()) {
                throw new IllegalArgumentException("태그가 없습니다.");
            }

        }
    }

    private List<GptChallengeInfo> convertToGptChallengeInfoList(
            GptResponse response
    )  {

        try {
            String content = response.getContent();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode listNode = mapper.readTree(content).get("challengeList");
            return mapper.readerForListOf(GptChallengeInfo.class).readValue(listNode);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generatePrompt(List<Category> categories) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("최근 1달 이내 소비가 많았던 카테고리 내역을 보고 아래 챌린지 포맷에 맞춰서 챌린지를 만들어 줘.\n")
                .append("사람들의 현실적인 생활비를 고려해서 만들어야 해.\n\n")
                .append("최근 1달 이내 소비가 많았던 카테고리 내역:\n");

        // 챌린지 생성에 반영할 카테고리 추가
        for (Category category : categories) {
            prompt.append("- ").append(category.getCategoryName()).append("\n");
        }

        prompt.append("\n")
                .append("응답 JSON의 각 챌린지에 포함된 \"category\" 필드는 반드시 위 카테고리 내역 중 하나만 사용해야 해.\n")
                .append("만약 특정 카테고리로 분류되지 않는 챌린지 포맷이면 category 가 null 이어도 돼.\n\n");

        prompt.append("\n")
                .append("챌린지 포맷:\n\n")
                .append(getChallengeFormatGuide())
                .append("---\n\n");

        prompt.append("challengeList 안에 위 포맷에 맞춰 랜덤으로 두 개의 챌린지만 만들어서 담아줘\n")
                .append("응답 message 안의 content 내용은 반드시 아래 JSON 포맷으로 응답해.\n")
                .append("- 모든 키와 문자열은 큰따옴표(\")로 감싸야 해\n")
                .append("- 쉼표(,) 누락 없이 유효한 JSON 형식을 유지해\n")
                .append("- JSON 외의 텍스트는 포함하지 마\n")
                .append("- tags 필드는 각 태그를 3개까지 콤마(,)로 구분한 문자열로 작성해\n")
                .append("- period 필드는 무조건 title 에서 'X일 동안' 에서 X 를 삽입해줘, 빈 값이면 안돼\n")
                .append("- challengeList 는 array 형태로 [ 로 시작하고 ] 로 닫아야 해\n\n")
                .append(getChallengeExampleJson());

        return prompt.toString();
    }

    private String getChallengeExampleJson() {
        return """
            {
              "challengeList": [
                {
                  "challengeType": 1,
                  "challengeTitle": "10일 동안 하루 15,000원 이하로 살기",
                  "period": 10,
                  "amount": 15000,
                  "limitCount": null,
                  "percent": null,
                  "category": "생활",
                  "tags": "생활비, 절약, 하루제한"
                },
                {
                  "challengeType": 4,
                  "challengeTitle": "20일 동안 식비 총 30,000원 이하로 살기",
                  "period": 20,
                  "amount": 30000,
                  "limitCount": null,
                  "percent": null,
                  "category": "식비",
                  "tags": "식비, 지출관리, 총액제한"
                }
              ]
            }
        """;
    }

    private String getChallengeFormatGuide() {
        StringBuilder sb = new StringBuilder();
        for (ChallengeFormatType type : ChallengeFormatType.values()) {
            sb.append(type.getTypeNum()).append(". ")
                    .append(type.getFormat()).append("\n");

            if (type.getMinPeriod() != null && type.getMaxPeriod() != null) {
                sb.append("   - 기간: ").append(type.getMinPeriod())
                        .append(" ~ ").append(type.getMaxPeriod()).append("일\n");
            }

            if (type.getValidAmounts() != null) {
                sb.append("   - 금액: ").append(type.getValidAmounts()).append("\n");
            }

            if (type.getHint() != null) {
                sb.append("   - 조건: ").append(type.getHint()).append("\n");
            }

            sb.append("\n");
        }

        return sb.toString();
    }

}
