package com.ssafy.moa2zi.day.domain;

import com.ssafy.moa2zi.common.entity.BaseTimeEntity;
import com.ssafy.moa2zi.day.dto.request.DayCommentCreateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "day_comments")
public class DayComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "day_comment_id")
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long dayId;

    private Long parentId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private DayComment(
            Long dayId,
            Long memberId,
            Long parentId,
            String content
    ) {
        this.dayId = dayId;
        this.memberId = memberId;
        this.parentId = parentId;
        this.content = content;
    }

    public static DayComment createComment(
            Long dayId,
            Long memberId,
            DayCommentCreateRequest request
    ) {
        return new DayComment(
                dayId,
                memberId,
                request.parentId(),
                request.content()
        );
    }
}
