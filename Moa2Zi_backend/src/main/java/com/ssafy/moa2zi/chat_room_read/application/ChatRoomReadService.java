package com.ssafy.moa2zi.chat_room_read.application;

import com.ssafy.moa2zi.chat_room_read.domain.ChatRoomRead;
import com.ssafy.moa2zi.chat_room_read.domain.ChatRoomReadRepository;
import com.ssafy.moa2zi.chat_room_read.dto.request.ChatRoomReadUpdateRequest;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

@Service
@RequiredArgsConstructor
public class ChatRoomReadService {

    private final ChatRoomReadRepository chatRoomReadRepository;

    @Transactional
    public void updateChatRoomRead(
            ChatRoomReadUpdateRequest chatRoomReadUpdateRequest,
            CustomMemberDetails loginMember

    ){

        ChatRoomRead chatRoomRead = chatRoomReadRepository.findByLoungeIdAndMemberId(
                chatRoomReadUpdateRequest.loungeId(),
                loginMember.getMemberId()
        ).orElseThrow(() -> new NotFoundException("조건에 맞는 라운지가 없습니다."));

        chatRoomRead.setLastReadTime(chatRoomReadUpdateRequest.lastReadTime());
    }

}
