package com.ssafy.moa2zi.chat_room_read.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table( name = "chat_room_read")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_read_id")
    private Long id;

    private Long memberId;

    private Long loungeId;

    @Setter
    private LocalDateTime lastReadTime;
}
