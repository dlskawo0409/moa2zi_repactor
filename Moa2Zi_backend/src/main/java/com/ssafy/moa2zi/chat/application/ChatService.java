package com.ssafy.moa2zi.chat.application;

import com.ssafy.moa2zi.chat.domain.Chat;
import com.ssafy.moa2zi.chat.domain.ChatRepository;
import com.ssafy.moa2zi.chat.domain.MessageType;
import com.ssafy.moa2zi.chat.dto.request.ChatGetRequest;
import com.ssafy.moa2zi.chat.dto.request.ChatSendRequest;
import com.ssafy.moa2zi.chat.dto.response.ChatGetResponse;
import com.ssafy.moa2zi.chat.dto.response.ChatWithMember;
import com.ssafy.moa2zi.chat_room_read.domain.ChatRoomReadRepository;
import com.ssafy.moa2zi.common.storage.application.ImageUtil;
import com.ssafy.moa2zi.lounge.domain.LoungeRepository;
import com.ssafy.moa2zi.lounge_participant.domain.LoungeParticipantRepository;
import com.ssafy.moa2zi.lounge_participant.dto.response.LoungeParticipantGetResponse;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final String TOPIC = "chat-messages";
    private final KafkaProducer kafkaProducer;
    private final ChatRepository chatRepository;
    private final ChatRoomReadRepository chatRoomReadRepository;
    private final LoungeRepository loungeRepository;
    private final LoungeParticipantRepository loungeParticipantRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ImageUtil imageUtil;

    @Transactional
    public void sendMessage(ChatSendRequest chatSendRequest) {
        Chat chat = Chat.builder()
                .chatId(String.valueOf(UUID.randomUUID()))
                .loungeId(chatSendRequest.loungeId())
                .memberId(chatSendRequest.memberId())
                .messageType(chatSendRequest.messageType())
                .timeStamp(chatSendRequest.localDateTime())
                .content(chatSendRequest.content())
                .build();

        Chat resultChat = chatRepository.save(chat);

        //Kafka 전송 (Consumer 없이 사용 가능)
        kafkaProducer.send(TOPIC, chatSendRequest);

        //WebSocket 통해 클라이언트에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/chat/" + chatSendRequest.loungeId(), chatSendRequest);
        chatRoomReadRepository.findByLoungeIdAndMemberId(chatSendRequest.loungeId(), resultChat.getMemberId())
                .ifPresent(chatRoomRead -> chatRoomRead.setLastReadTime(resultChat.getTimeStamp()));
    }

    public String sendImage(
            Long loungeId,
            MultipartFile multipartFile,
            CustomMemberDetails loginMember
    ) throws IOException {
        if(!loungeRepository.existsById(loungeId)){
            throw new NotFoundException("라운드를 찾을 수 없습니다.");
        }

        if(!loungeParticipantRepository.existsByLoungeIdAndMemberId(loungeId, loginMember.getMemberId())){
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

       return imageUtil.CovertToWebpAndStore(multipartFile, "chat/"+loungeId);

    }

    public ChatGetResponse getChat(
            ChatGetRequest chatGetRequest,
            CustomMemberDetails loginMember
    ) throws AccessDeniedException {

        if(!loungeParticipantRepository.existsByLoungeIdAndMemberId(chatGetRequest.loungeId(), loginMember.getMemberId())){
            throw new AccessDeniedException("라운지 접근 권한이 없습니다.");
        }

        Long loungeId = chatGetRequest.loungeId();
        LocalDateTime nextChatTime = chatGetRequest.next() != null ? chatGetRequest.next() : null;
        int pageSize = chatGetRequest.size() != null ? chatGetRequest.size() : 20;

        Pageable pageable = PageRequest.of(0, pageSize + 1);

        List<LoungeParticipantGetResponse> loungeParticipantGetResponseList =
                loungeParticipantRepository.getLoungeParticipantWithLoungeIdAndMemberId(loungeId);

        Map<Long, LoungeParticipantGetResponse> memberMap =
                loungeParticipantGetResponseList.stream()
                        .collect(Collectors.toMap(
                                LoungeParticipantGetResponse::memberId,
                                Function.identity()
                        ));

        List<Chat> chatList;
        if (nextChatTime != null) {
            chatList = chatRepository.findByLoungeIdAndTimeStampLessThanOrderByTimeStampDesc(loungeId, nextChatTime, pageable);
        } else {
            chatList = chatRepository.findByLoungeIdOrderByTimeStampDesc(loungeId, pageable);
        }

        boolean hasNext = chatList.size() > pageSize;

        if (hasNext) {
            chatList = chatList.subList(0, pageSize); // 초과한 하나 제거
        }

        //member 와 조합
        List<ChatWithMember> chatWithMemberList = chatList.stream()
                .map(now -> {
                    LoungeParticipantGetResponse member = memberMap.get(now.getMemberId());

                    String content = now.getContent();

                    if(now.getMessageType().equals(MessageType.IMAGE)
                        && content != null
                            && content.startsWith("chat/")
                    ){
                        content = imageUtil.getPreSignedUrl(content);
                    }


                    return ChatWithMember.builder()
                        .chatId(now.getChatId())
                        .loungeId(now.getLoungeId())
                        .memberId(now.getMemberId())
                        .nickname(member == null ? null : member.nickname())
                        .profileImage(member == null ? null : member.profileImage())
                        .messageType(now.getMessageType())
                        .timeStamp(now.getTimeStamp())
                        .content(content)
                        .build();
                })
                .toList();

        Long total = chatRepository.countByLoungeId(loungeId);

        Integer size = Math.min(chatList.size(), pageSize);

        LocalDateTime next = hasNext ? chatList.get(chatList.size() - 1).getTimeStamp() : null;

        return new ChatGetResponse(chatWithMemberList, total, size, hasNext, next);

    }

}
