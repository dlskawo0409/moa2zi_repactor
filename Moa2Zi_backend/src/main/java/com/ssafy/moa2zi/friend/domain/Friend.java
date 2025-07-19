package com.ssafy.moa2zi.friend.domain;

import com.ssafy.moa2zi.common.entity.BaseTimeEntity;
import com.ssafy.moa2zi.member.domain.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "friends")
public class Friend extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_id")
    private Long id;

    @Column(nullable = false)
    private Long requestId;

    @Column(nullable = false)
    private Long acceptId;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Status status;

    private Friend(Long requestId, Long acceptId) {
        this.requestId = requestId;
        this.acceptId = acceptId;
        this.status = Status.PENDING;
    }

    public static Friend createFriend(
            Member requestMember,
            Member acceptMember
    ) {
        return new Friend(
                requestMember.getMemberId(),
                acceptMember.getMemberId()
        );
    }

    public void markAsAccepted() {
        this.status = Status.ACCEPTED;
    }
}
