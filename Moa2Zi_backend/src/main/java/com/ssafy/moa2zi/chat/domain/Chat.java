package com.ssafy.moa2zi.chat.domain;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;


@Document(collection = "chat")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat implements Serializable {

    @Id
    private String chatId;

    private Long loungeId;
    private Long memberId;
    private MessageType messageType;
    private LocalDateTime timeStamp;
    private String content;

}
