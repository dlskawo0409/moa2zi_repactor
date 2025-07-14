package com.ssafy.moa2zi.lounge.application;

import com.ssafy.moa2zi.chat.domain.Chat;
import com.ssafy.moa2zi.chat.domain.ChatRepository;
import com.ssafy.moa2zi.chat_room_read.domain.ChatRoomRead;
import com.ssafy.moa2zi.chat_room_read.domain.ChatRoomReadRepository;
import com.ssafy.moa2zi.friend.domain.FriendRepository;
import com.ssafy.moa2zi.lounge.domain.Lounge;
import com.ssafy.moa2zi.lounge.domain.LoungeRepository;
import com.ssafy.moa2zi.lounge.domain.LoungeStatus;
import com.ssafy.moa2zi.lounge.dto.request.LoungeCreateRequest;
import com.ssafy.moa2zi.lounge.dto.request.LoungeGetRequest;
import com.ssafy.moa2zi.lounge.dto.response.*;
import com.ssafy.moa2zi.lounge_participant.domain.LoungeParticipant;
import com.ssafy.moa2zi.lounge_participant.domain.LoungeParticipantRepository;
import com.ssafy.moa2zi.lounge_participant.dto.response.LoungeParticipantGetResponse;
import com.ssafy.moa2zi.member.domain.MemberRepository;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
@RequiredArgsConstructor
public class LoungeService {

    private final LoungeRepository loungeRepository;
    private final LoungeParticipantRepository loungeParticipantRepository;
    private final FriendRepository friendRepository;
    private final ChatRoomReadRepository chatRoomReadRepository;
    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;

    private final  LocalDateTime defaultReadTime = LocalDateTime.of(1970, 1, 1, 0, 0);

    @Transactional
    public void createLounge(
            LoungeCreateRequest loungeCreateRequest,
            CustomMemberDetails loginMember
    ) throws BadRequestException {
        int duration = 30;

        if(loungeCreateRequest.duration() != null && loungeCreateRequest.duration() > 0 ){
           duration = loungeCreateRequest.duration();
        }

        if(loungeCreateRequest.endTime().isBefore(LocalDateTime.now())){
            throw new BadRequestException("종료시간은 현재 시간 보다 늦어야 합니다.");
        }

        if(!loungeCreateRequest.participantList().contains(loginMember.getMemberId())){
            loungeCreateRequest.participantList().add(loginMember.getMemberId());
        }

        if(loungeCreateRequest.participantList().size() > 10){
            throw new BadRequestException("참여자는 최대 10명 입니다.");
        }

        // 이남재 20번 쿼리
        validateParticipantIsMyFriends(loungeCreateRequest.participantList(), loginMember.getMemberId());

        Lounge lounge = Lounge.builder()
                .title(loungeCreateRequest.title())
                .endTime(loungeCreateRequest.endTime())
                .duration(duration)
                .build();


        Lounge afterLounge = loungeRepository.save(lounge);

        List<LoungeParticipant> loungeParticipantList =  loungeCreateRequest.participantList().stream()
                .map(memberId -> LoungeParticipant.builder()
                        .loungeId(afterLounge.getId())
                        .memberId(memberId)
                        .build())
                .toList();

        LocalDateTime nowTime = LocalDateTime.now();

        List<ChatRoomRead> chatRoomReadList = loungeCreateRequest.participantList().stream()
                        .map(memberId -> ChatRoomRead.builder()
                                .loungeId(lounge.getId())
                                .memberId(memberId)
                                .lastReadTime(nowTime)
                                .build())
                .toList();


        loungeParticipantRepository.saveAll(loungeParticipantList);
        chatRoomReadRepository.saveAll(chatRoomReadList);
    }

    public LoungeListResponse getLounge(
            LoungeGetRequest loungeGetRequest,
            CustomMemberDetails loginMember
    ) throws BadRequestException {

        validateLoungeGetRequest(loungeGetRequest);

        int size = loungeGetRequest.size() == null ? 10 : loungeGetRequest.size();
        LocalDateTime today = LocalDateTime.now();

        List<LoungeWithGame> loungeWithGameList = loungeRepository.getLoungeWithGame(
            size,
            loungeGetRequest,
            today,
            loginMember.getMemberId()
        );

        List<Long> loungeIdList = loungeWithGameList.stream().distinct()
            .map(LoungeWithGame::id)
            .toList();

        Map<Long, LocalDateTime> latestChatMap =
            chatRepository.findLatestChatTimeByLoungeIds(loungeIdList);

        List<LoungeWithParticipant> loungesWithParticipantList = loungeRepository.getLoungeWithParticipantByLoungeIdList(loungeIdList);

        // 안 읽은 메세지 수 가져오기
        List<ChatRoomRead> chatRoomReadList = chatRoomReadRepository.findByMemberId(loginMember.getMemberId());

        Map<Long, Long> unreadCountMap = new HashMap<>();


        Long unReadNumSum = 0L;

        for(ChatRoomRead chatRoomRead : chatRoomReadList){

            Long count = chatRepository.countByLoungeIdAndTimeStampAfter(chatRoomRead.getLoungeId(), chatRoomRead.getLastReadTime());
            unReadNumSum += count;

            if(loungeIdList.contains(chatRoomRead.getLoungeId())){
                unreadCountMap.put(chatRoomRead.getLoungeId(), count);
            }
        }


        List<LoungeSearchResponse> loungeSearchResponseList = new ArrayList<>(loungeWithGameList.stream()
			.map(l -> {
				// 참가자 매핑
				List<LoungeParticipantGetResponse> participantList = loungesWithParticipantList.stream()
					.filter(p -> p.loungeId().equals(l.id()))
					.map(p -> LoungeParticipantGetResponse.builder()
						.memberId(p.memberId())
						.nickname(p.nickname())
						.profileImage(p.profileImage())
						.build()
					)
					.toList();

				// 상태 계산
				LoungeStatus loungeStatus = getLoungeStatusByToday(
                        l.gameEndTime(),
                        l.loungeEndTime(),
                        today
                );



				return new LoungeSearchResponse(
					l.id(),
					l.title(),
					loungeStatus,
					unreadCountMap.get(l.id()) == null ? 0 : unreadCountMap.get(l.id()) ,
                    latestChatMap.get(l.id()),
					participantList,
					l.createdAt()
				);
			})
			.toList());

        sortLoungeSearchResponseList(latestChatMap, loungeSearchResponseList);

        Long total = loungeRepository.getTotal(loungeGetRequest, loginMember.getMemberId());

        boolean hasNext = loungeWithGameList.size() > size;

        Long next = (hasNext) ? loungeSearchResponseList.get(loungeSearchResponseList.size()-1).loungeId() : null;
        LoungeStatus loungeStatus = (hasNext) ? loungeSearchResponseList.get(loungeSearchResponseList.size()-1).loungeStatus() : null;

        if(hasNext){
            loungeSearchResponseList.remove(loungeSearchResponseList.size() -1);
        }

        return new LoungeListResponse(
            loungeSearchResponseList,
            unReadNumSum,
            total,
            Math.min(loungeSearchResponseList.size() ,size),
            hasNext,
            next,
            loungeStatus
        );

    }


    protected void validateParticipantIsMyFriends(List<Long> participantList, Long memberId) throws BadRequestException {
        for (Long friendId : participantList) {
            if (!Objects.equals(friendId, memberId)
                    && !friendRepository.areTheyFriend(friendId, memberId)) {
                throw new BadRequestException(memberId+"와" +friendId + " 서로 친구가 아닙니다." );
            }
        }
    }

     public LoungeListWithNicknameResponse getLoungeWithNickname(
             LoungeGetRequest loungeGetRequest,
             CustomMemberDetails loginMember
     ) throws BadRequestException {

         if(loungeGetRequest.keyword() == null || loungeGetRequest.keyword().isBlank()){
             throw new BadRequestException("nickname 이 null 이거나 빈칸일 수 없습니다.");
         }

         validateLoungeGetRequest(loungeGetRequest);

         int size = loungeGetRequest.size() == null ? 10 : loungeGetRequest.size();

         // 내가 포함된 라운지 목록 Id 가져오기
         List<Long> loungeIdListWithMemberId =
                 loungeRepository.getLoungeIdListByMemberId(loginMember.getMemberId());

         // nickname 을 가지고 있는 memberId 목록 가져오기
         List<Long> memberIdListWithNickname =
                 memberRepository.getMemberIdListByNickname(loungeGetRequest.keyword(), loginMember.getMemberId());

         // loungeId 와 memberId 로 목록 가져오기
         List<LoungeWithGameAndParticipant> loungeWithGameAndParticipantList =
                 loungeRepository.getLoungeWithGameAndParticipantListByLoungeIdAndMemberId(loungeGetRequest,
                         loungeIdListWithMemberId,
                         memberIdListWithNickname,
                         loginMember.getMemberId()
                 );

         Long next = loungeWithGameAndParticipantList.stream()
                 .map(LoungeWithGameAndParticipant::loungeId)
                 .max(Long::compareTo)
                 .orElse(null);

         Map<Long, List<LoungeWithGameAndParticipant>> grouped = loungeWithGameAndParticipantList.stream()
                 .collect(Collectors.groupingBy(LoungeWithGameAndParticipant::loungeId));

         LocalDateTime today = LocalDateTime.now();

         List<Long> loungeIdList = grouped.keySet().stream().toList();

         Map<Long, Long> unreadCountMap = new HashMap<>();
         Map<Long, LocalDateTime> lastReadMap = chatRepository.findLatestChatTimeByLoungeIds(loungeIdList);

         LocalDateTime defaultReadTime = LocalDateTime.of(1970, 1, 1, 0, 0);

         for (Long loungeId : loungeIdList) {
             Optional<ChatRoomRead> chatRoomReadOpt = chatRoomReadRepository.findByLoungeIdAndMemberId(loungeId, loginMember.getMemberId());

             LocalDateTime lastReadTime = chatRoomReadOpt
                     .map(ChatRoomRead::getLastReadTime)
                     .orElse(defaultReadTime);

             Long count = chatRepository.countByLoungeIdAndTimeStampAfter(loungeId, lastReadTime);

             unreadCountMap.put(loungeId, count);
         }

         // 그룹을 LoungeSearchResponse로 변환
         List<LoungeSearchResponse> loungeSearchResponseList = new ArrayList<>(grouped.entrySet().stream()
                 .map(entry -> {
                     Long loungeId = entry.getKey();
                     List<LoungeWithGameAndParticipant> group = entry.getValue();
                     LoungeWithGameAndParticipant first = group.get(0);

                     List<LoungeParticipantGetResponse> participants = group.stream()
                             .map(p -> new LoungeParticipantGetResponse(
                                     p.memberId(),
                                     p.nickname(),
                                     p.profileImage()
                             ))
                             .collect(Collectors.toList());

                     LoungeStatus loungeStatus = getLoungeStatusByToday(
                             first.gameEndTime(),
                             first.loungeEndTime(),
                             today
                     );

                     return new LoungeSearchResponse(
                             loungeId,
                             first.title(),
                             loungeStatus,
                             unreadCountMap.get(loungeId) == null ? 0 : unreadCountMap.get(loungeId),
                             lastReadMap.get(loungeId),
                             participants,
                             first.createdAt()
                     );
                 })
                 .toList());


         Map<Long, LocalDateTime> latestChatMap =
                 chatRepository.findLatestChatTimeByLoungeIds(loungeIdList);

         sortLoungeSearchResponseList(latestChatMap, loungeSearchResponseList);

         Long total = loungeRepository.getLoungeWithNicknameTotal(loungeIdListWithMemberId, memberIdListWithNickname);
         boolean hasNext = loungeSearchResponseList.size() > size;

         if (hasNext && next != null) {
             loungeSearchResponseList = loungeSearchResponseList.stream()
                     .filter(lounge -> !lounge.loungeId().equals(next))
                     .collect(Collectors.toList());
         }

         return new LoungeListWithNicknameResponse(
                 loungeSearchResponseList,
                 total,
                 Math.min(loungeSearchResponseList.size() ,size),
                 hasNext,
                 next
         );

     }

     protected void sortLoungeSearchResponseList(
         Map<Long, LocalDateTime> latestChatMap ,
         List<LoungeSearchResponse> loungeSearchResponseList
     ){

        loungeSearchResponseList.sort(
            Comparator
                .comparing((LoungeSearchResponse l) -> {
                    int temp = 0;
                    if(l.loungeStatus() == LoungeStatus.RUNNING){
                        temp = 2;
                    }else if(l.loungeStatus() == LoungeStatus.COMPLETED){
                        temp = 1;
                    }else{
                        temp = 0;
                    }
                    return temp;
                })
                .thenComparing(l -> latestChatMap.getOrDefault(l.loungeId(), defaultReadTime)).reversed()
                .thenComparing(LoungeSearchResponse::loungeId, Comparator.reverseOrder())
        );
    }



     public LoungeDetailResponse getLoungeDetails(Long loungeId, CustomMemberDetails loginMember) throws AccessDeniedException {

        if(!loungeParticipantRepository.existsByLoungeIdAndMemberId(loungeId, loginMember.getMemberId())){
            throw new AccessDeniedException("라운지 접근 권한이 없습니다.");
        }

        Lounge lounge = loungeRepository.findById(loungeId)
                .orElseThrow(() -> new NotFoundException("존재하지않는 라운지입니다."));


        List<LoungeParticipantGetResponse> loungeParticipantGetResponseList =
                loungeParticipantRepository.getLoungeParticipantWithLoungeIdAndMemberId(loungeId);

        return LoungeDetailResponse.builder()
                .loungeId(lounge.getId())
                .title(lounge.getTitle())
                .loungeStatus(lounge.getEndTime().isAfter(LocalDateTime.now()) ? LoungeStatus.COMPLETED : LoungeStatus.TERMINATED)
                .participantList(loungeParticipantGetResponseList)
                .createdAt(lounge.getCreatedAt())
                .build();

     }

     public void validateLoungeGetRequest(LoungeGetRequest loungeGetRequest) throws BadRequestException {
         if(loungeGetRequest.next() == null ^ loungeGetRequest.loungeStatus() == null){
             throw new BadRequestException("next 와 loungeStatus 동시에 존재하거나 동시에 없어야 합니다.");
         }

     }


     public LoungeStatus getLoungeStatusByToday(
             LocalDateTime gameEndTime,
             LocalDateTime loungeEndTime,
             LocalDateTime today
     ){

         if (gameEndTime == null) {
             if(loungeEndTime.isAfter(today)){
                 return LoungeStatus.COMPLETED;
             }
             return LoungeStatus.TERMINATED;
         } else if (gameEndTime.isAfter(today)) {
             return  LoungeStatus.RUNNING;
         } else if (loungeEndTime.isAfter(today)) {
             return LoungeStatus.COMPLETED;
         } else {
             return LoungeStatus.TERMINATED;
         }

     }
}
