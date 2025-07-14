package com.ssafy.moa2zi.quiz.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.moa2zi.common.infrastructure.gpt.GptClient;
import com.ssafy.moa2zi.common.infrastructure.gpt.GptRequestOptions;
import com.ssafy.moa2zi.common.infrastructure.gpt.GptResponse;
import com.ssafy.moa2zi.day.domain.DayRepository;
import com.ssafy.moa2zi.day.dto.request.DayTransactionSumGetRequest;
import com.ssafy.moa2zi.day.dto.request.SumWithCategory;
import com.ssafy.moa2zi.game.domain.Game;
import com.ssafy.moa2zi.game.domain.GameRepository;
import com.ssafy.moa2zi.lounge.domain.LoungeRepository;
import com.ssafy.moa2zi.lounge.dto.response.LoungeWithGame;
import com.ssafy.moa2zi.game.domain.GameRepository;
import com.ssafy.moa2zi.lounge.domain.LoungeRepository;
import com.ssafy.moa2zi.lounge_participant.domain.LoungeParticipantRepository;
import com.ssafy.moa2zi.lounge_participant.dto.response.LoungeParticipantGetResponse;
import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.domain.MemberRepository;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import com.ssafy.moa2zi.quiz.domain.IsCorrect;
import com.ssafy.moa2zi.quiz.domain.Quiz;
import com.ssafy.moa2zi.quiz.domain.QuizRepository;
import com.ssafy.moa2zi.quiz.dto.response.QuizGetResponse;
import com.ssafy.moa2zi.quiz.dto.response.QuizWithRankResultResponse;
import com.ssafy.moa2zi.quiz.dto.response.QuizWithResultResponse;
import com.ssafy.moa2zi.quiz.dto.response.QuizWithResultSumResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private final DayRepository dayRepository;
    private final LoungeParticipantRepository loungeParticipantRepository;
    private  final QuizRepository quizRepository;
    private final MemberRepository memberRepository;
    private final LoungeRepository loungeRepository;

    private final GptClient gptClient;

    public List<Quiz> createQuiz(
            @Valid DayTransactionSumGetRequest dayTransactionSumGetRequest,
            CustomMemberDetails loginMember
    ) throws BadRequestException, JsonProcessingException {


        Member member = memberRepository.findById(loginMember.getMemberId())
                .orElseThrow(() -> new NotFoundException("멤버가 존재하지 않습니다."));

        //방에 참가한 인원 검색
         List<LoungeParticipantGetResponse> loungeParticipantGetResponseList
                 = loungeParticipantRepository.getLoungeParticipantWithLoungeIdAndMemberId(dayTransactionSumGetRequest.loungeId());

         //
        int memberSize = loungeParticipantGetResponseList.size();

        if(memberSize < 2){
            throw new BadRequestException("참가자가 너무 작습니다.");
        }

        int quizSizePerMember = getQuizCount(memberSize);

        List<SumWithCategory> sumList
                = dayRepository.getSumByDayGroupByCategory(
                        dayTransactionSumGetRequest,
                        loginMember.getMemberId()
                );

        if(sumList.isEmpty()){
            return null;
        }

        String prompt =  generatePrompt(quizSizePerMember, sumList);

        GptResponse response = gptClient.send(
                prompt,
                GptRequestOptions.builder()
                        .model("gpt-4o")
                        .temperature(0.7F)
                        .frequencyPenalty(1.0F)
                        .maxTokens(500)
                        .responseFormat("json_object")
                        .build()
        );

        log.info("퀴즈 생성 요청, GPT 응답: \n{}", response);

        List<Quiz> quizList = convertToQuizList(
                dayTransactionSumGetRequest.gameId(),
                member.getNickname(),
                response
        );

        quizRepository.saveAll(quizList);
        return quizList;

    }

    public int getQuizCount(int memberSize){
        if(memberSize == 2){
            return 3;
        }
        else if( 3 <= memberSize && memberSize <= 5){
            return 2;
        }
        return 1;
    }


    private String generatePrompt(
            int quizSizePerMember,
            List<SumWithCategory> sumList
    ) throws JsonProcessingException {

        StringBuilder prompt = new StringBuilder();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(sumList);

        prompt.append(json)
                .append("\n 일정한 기간 동안 내가 사용한 카테고리별 금액이야\n")
                .append("재치있게 yes or no 로 대답할 수 있는 문제 만들어줘")
                .append("정답도 같이 아래 예시 처럼 json으로 줘\n")
                .append("{\n")
                .append("  \"questions\": [\n")
                .append("    {\n")
                .append("      \"question\": \"문제 내용\",\n")
                .append("      \"answer\": \"Yes or No\"\n")
                .append("    },\n")
                .append("    ...\n")
                .append("  ]\n")
                .append("}\n\n")
                .append("꼭").append(quizSizePerMember).append("개 만들어야해!");

        return prompt.toString();
    }

    private List<Quiz> convertToQuizList(
            Long gameId,
            String nickname,
            GptResponse response
    ){
        try{
            String content = response.getContent();
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(content);
            JsonNode questionsNode = root.get("questions");

            if (questionsNode == null || !questionsNode.isArray()) {
                return Collections.emptyList();
            }

            return StreamSupport.stream(questionsNode.spliterator(), false)
                    .map(q -> Quiz.builder()
                            .gameId(gameId)
                            .context(nickname +" : "+q.get("question").asText())
                            .answer(IsCorrect.fromString(q.get("answer").asText()))
                            .build())
                    .toList();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // todo 1. 날짜 주기
    //      2. 다음 퀴즈 주기


    public QuizGetResponse getQuiz(
            Long quizId
    ){

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NotFoundException("퀴즈를 찾을 수 없습니다."));

        LoungeWithGame loungeWithGame = loungeRepository.getLoungeByGameId(quiz.getGameId())
                .orElseThrow(() -> new NotFoundException("라운지 및 게임을 가져오는 데 실패했습니다."));

        List<Quiz> quizList = quizRepository.findByGameId(quiz.getGameId());

        int now = 0;
        Long next = null;

        for(int i = 0; i<quizList.size(); ++i){
            if(Objects.equals(quiz.getId(), quizList.get(i).getId())){
                now = i + 1;
                if(now < quizList.size()){

                    quiz.setContext(getQuizContextWithMember(quiz.getContext() , quiz.getMemberId()));

                    next = quizList.get(now).getId();
                }
            }
        }

        return QuizGetResponse.builder()
                .totalQuizSize((long) quizList.size())
                .nowCount((long) now)
                .nextQuizId(next)
                .quiz(quiz)
                .loungeWithGame(loungeWithGame)
                .build();


    }

    public QuizWithResultSumResponse getQuizWithResult(Long gameId, Long memberId){

        List<QuizWithResultResponse> quizWithResultResponseList
                = quizRepository.getQuizWithResult(gameId, memberId);

        quizWithResultResponseList.forEach(quiz ->
                quiz.setContent(getQuizContextWithMember(quiz.getContent(), quiz.getMemberId()))
        );


        Integer totalQuizCount = quizRepository.countByGameId(gameId);
        int rightAnswerCount = (int) quizWithResultResponseList.stream()
                .filter(QuizWithResultResponse::getIsCorrect)
                .count();


        return QuizWithResultSumResponse.builder()
                .totalQuizCount(totalQuizCount)
                .rightAnswerCount(rightAnswerCount)
                .quizWithResultResponseList(quizWithResultResponseList)
                .build();
    }

    public QuizWithRankResultResponse getQuizWithRanking(
            Long gameId,
            Long memberId
    ){

        Long loungeId = loungeRepository.getLoungeIdByGameId(gameId);

        List<LoungeParticipantGetResponse> loungeParticipantGetResponseList =
                loungeParticipantRepository.getLoungeParticipantWithLoungeIdAndMemberId(loungeId);

        List<Long> participantIdList = loungeParticipantGetResponseList.stream()
                .map(LoungeParticipantGetResponse::memberId)
                .toList();

        return quizRepository.getQuizWithRanking(gameId, memberId, participantIdList);
    }

    

    public String getQuizContextWithMember(String beforeContext, Long memberId){
        Member member =  memberRepository.findById(memberId).orElse(null);
        String nickname = "";
        if(member != null){
            nickname = member.getNickname();
        }

        return nickname +" : "+beforeContext;
    }

}
