package com.ssafy.moa2zi.common.scheduler;

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
import com.ssafy.moa2zi.lounge.domain.Lounge;
import com.ssafy.moa2zi.lounge.domain.LoungeRepository;
import com.ssafy.moa2zi.lounge.dto.response.LoungeWithParticipant;
import com.ssafy.moa2zi.lounge_participant.domain.LoungeParticipantRepository;
import com.ssafy.moa2zi.lounge_participant.dto.response.LoungeParticipantGetResponse;
import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.domain.MemberRepository;
import com.ssafy.moa2zi.quiz.domain.IsCorrect;
import com.ssafy.moa2zi.quiz.domain.Quiz;
import com.ssafy.moa2zi.quiz.domain.QuizRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Component
@RequiredArgsConstructor
public class MidnightScheduler {

    private final LoungeRepository loungeRepository;
    private final GameRepository gameRepository;
    private final DayRepository dayRepository;
    private final LoungeParticipantRepository loungeParticipantRepository;
    private  final QuizRepository quizRepository;
    private final MemberRepository memberRepository;

    private final GptClient gptClient;

    // 매일 자정 (00:00:00)에 실행
    @Scheduled(cron = "00 00 0 * * *")
    public void runAtMidnight() {
        log.info("매일 자정에 실행되는 작업입니다. 현재 시간: " + java.time.LocalDateTime.now());

        LocalDateTime today = LocalDateTime.now();

        List<Lounge> loungeList = loungeRepository.getLoungeListLargeThanEndTime(today);

        Map<Long, Lounge> loungeMap = loungeList.stream()
                .filter(l -> {
                    LocalDate createdDate = l.getCreatedAt().toLocalDate();
                    long daysBetween = ChronoUnit.DAYS.between(createdDate, today);
                    return l.getDuration() >= daysBetween && daysBetween % l.getDuration() == 0;
                })
                .collect(Collectors.toMap(
                        Lounge::getId,
                        Function.identity()
                ));

        List<Game> newGameList = loungeMap.keySet().stream()
                .map(l -> Game.builder()
                        .loungeId(l)
                        .createdAt(today)
                        .endTime(today.plusDays(1)) // 오늘 + 1일
                        .build())
                .toList();

        gameRepository.bulkInsert(newGameList);
        //
        // //참가자 가져오기
        List<LoungeWithParticipant> loungeWithParticipantList =
                loungeRepository.getLoungeWithParticipantByLoungeIdList(
                        loungeMap.keySet().stream().toList()
                );

        Map<Long, List<LoungeWithParticipant>> grouped = loungeWithParticipantList.stream()
                .collect(Collectors.groupingBy(LoungeWithParticipant::loungeId));

        Integer todayInteger = localDateTimeToInt(today);

        Map<Long, DayTransactionSumGetRequest> requestMap = newGameList.stream()
            .map(game -> {
                Lounge l = loungeMap.get(game.getLoungeId());
                Integer startTimeInteger = localDateTimeToInt(today.minusDays(l.getDuration()));

                Optional<Long> optionalGameId = gameRepository.findGameIdByLoungeIdAndCreatedAtAndEndTime(
                    game.getLoungeId(), game.getCreatedAt(), game.getEndTime()
                );

                log.info("gameId : "+optionalGameId.get());

                return optionalGameId.map(gameId ->
                    DayTransactionSumGetRequest.builder()
                        .loungeId(game.getLoungeId())
                        .gameId(optionalGameId.get())
                        .startTime(startTimeInteger)
                        .endTime(todayInteger)
                        .build()
                );
            })
            .filter(Optional::isPresent) // null 제거
            .map(Optional::get)          // Optional 해제
            .collect(Collectors.toMap(
                DayTransactionSumGetRequest::loungeId,
                Function.identity()
            ));
        
        // 라운지 당
        grouped.keySet().forEach(loungeId -> {
            List<LoungeWithParticipant> loungeWithParticipant = grouped.get(loungeId);

            loungeWithParticipant.forEach(participant -> {
                try {
                    createQuiz(
                            requestMap.get(loungeId),
                            participant.memberId()
                    );
                } catch (BadRequestException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        });



    }

    public static int localDateTimeToInt(LocalDateTime dateTime) {
        return Integer.parseInt(dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
    }


    public void createQuiz(
            @Valid DayTransactionSumGetRequest dayTransactionSumGetRequest,
            Long memberId
    ) throws BadRequestException, JsonProcessingException {


        Member member = memberRepository.findById(memberId)
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
                memberId
        );

        if(sumList.isEmpty()){
            return;
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
                member.getMemberId(),
                response
        );

        quizRepository.saveAll(quizList);

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
            Long memberId,
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
                            .context(q.get("question").asText())
                            .answer(IsCorrect.fromString(q.get("answer").asText()))
                            .memberId(memberId)
                            .build())
                    .toList();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}