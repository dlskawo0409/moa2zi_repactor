package com.ssafy.moa2zi.chat.presentation;

import com.ssafy.moa2zi.chat.application.ChatService;
import com.ssafy.moa2zi.chat.dto.request.ChatGetRequest;
import com.ssafy.moa2zi.chat.dto.request.ChatSendRequest;
import com.ssafy.moa2zi.chat.dto.response.ChatGetResponse;
import com.ssafy.moa2zi.chat_room_read.application.ChatRoomReadService;
import com.ssafy.moa2zi.chat_room_read.dto.request.ChatRoomReadUpdateRequest;
import com.ssafy.moa2zi.common.storage.application.ImageUtil;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatRoomReadService chatRoomReadService;

    @MessageMapping("/chat/send")
    public void sendMessage(
            @Valid @RequestBody ChatSendRequest chatSendRequest){
        chatService.sendMessage(chatSendRequest);
    }

    @PostMapping(value = "/lounge/{loungeId}/chat/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> sendImage(
            @PathVariable(name = "loungeId") Long loungeId,
            @RequestParam("image") MultipartFile multipartFile,
            @AuthenticationPrincipal CustomMemberDetails loginMember
            ) throws IOException {

        Map<String, String> result = new HashMap<>();
        result.put("image", chatService.sendImage(loungeId, multipartFile, loginMember));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/chat")
    public ResponseEntity<ChatGetResponse> getChat(
            @Valid ChatGetRequest chatGetRequest,
            @AuthenticationPrincipal CustomMemberDetails loginMemberDetails
    ) throws AccessDeniedException {

        return ResponseEntity.ok(chatService.getChat(chatGetRequest, loginMemberDetails));
    }

    @PutMapping("/chat/check")
    public ResponseEntity<Void> updateChatRoomRead(
            @Valid @RequestBody ChatRoomReadUpdateRequest chatRoomReadUpdateRequest,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    )
    {
        chatRoomReadService.updateChatRoomRead(chatRoomReadUpdateRequest, loginMember);

        return ResponseEntity.ok().build();
    }

}
