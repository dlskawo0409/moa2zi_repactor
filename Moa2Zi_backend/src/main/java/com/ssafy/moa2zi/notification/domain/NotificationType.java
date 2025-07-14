package com.ssafy.moa2zi.notification.domain;

import lombok.Getter;

@Getter
public enum NotificationType {
    REVIEW_LIKE("%s님이 챌린지 후기에 좋아요를 남겼습니다."), // 성공 후기 좋아요
    CALENDAR_COMMENT("%s님이 가계부에 댓글을 달았습니다."), // 달력 댓글
    FRIEND_REQUEST("%s님이 친구를 요청했습니다."), // 친구 요청
    FRIEND_REJECT("%s님이 친구를 거절하였습니다."), // 찬구 응답
    FRIEND_ACCEPT("%s님이 친구 요청을 수락했습니다."),
    POCKET_MONEY("오늘부터 하루 %d원씩 사용해야 용돈을 지킬 수 있어요!"),
    TOP_SPENDING("최근 %s에서 %d원을 결제했어요. 나의 top %d 소비에요!"),
    MONTH_SPENDING_INCREASE("이 주변에서 한달 전보다 %d%% 지출이 증가했어요");

    private final String template;

    NotificationType(String template) {
        this.template = template;
    }

}
