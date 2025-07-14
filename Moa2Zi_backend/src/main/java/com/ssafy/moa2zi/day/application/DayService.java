package com.ssafy.moa2zi.day.application;

import com.ssafy.moa2zi.common.util.FirebaseMessagingSnippets;
import com.ssafy.moa2zi.day.domain.Day;
import com.ssafy.moa2zi.day.dto.request.DayCommentCreateRequest;
import com.ssafy.moa2zi.day.domain.DayComment;
import com.ssafy.moa2zi.day.domain.DayCommentRepository;
import com.ssafy.moa2zi.day.domain.DayRepository;
import com.ssafy.moa2zi.day.dto.request.DayCommentSearchRequest;
import com.ssafy.moa2zi.day.dto.response.DayCommentSearchResponse;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import com.ssafy.moa2zi.notification.application.NotificationProducer;
import com.ssafy.moa2zi.notification.domain.NotificationMessage;
import com.ssafy.moa2zi.notification.domain.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DayService {

    private final DayCommentRepository dayCommentRepository;
    private final DayRepository dayRepository;
    private final NotificationProducer notificationProducer;

    @Transactional
    public void createComment(
            Long dayId,
            DayCommentCreateRequest request,
            CustomMemberDetails loginMember
    ) {

        Day day = findDayById(dayId);
        validateCommentExistsInDay(dayId, request.parentId());
        DayComment dayComment = DayComment.createComment(dayId, loginMember.getMemberId(), request);
        dayCommentRepository.save(dayComment);

        notificationProducer.send(NotificationMessage.builder()
                .senderId(loginMember.getMemberId())
                .receiverId(day.getMemberId())
                .notificationType(NotificationType.CALENDAR_COMMENT)
                .build());
    }

    private Day findDayById(Long dayId) {
        return dayRepository.findById(dayId)
                .orElseThrow(() -> new NotFoundException("해당 ID의 가계부 일자가 존재하지 않습니다, " + dayId));
    }

    private void validateCommentExistsInDay(Long dayId, Long commentId) {
        if(!Objects.isNull(commentId) && !dayCommentRepository.existsByIdAndDayId(commentId, dayId)) {
            throw new NotFoundException("해당 날짜에 존재하는 댓글 ID가 아닙니다.");
        }
    }

    public DayCommentSearchResponse getComments(
            Long dayId,
            DayCommentSearchRequest request
    ) {

        return dayCommentRepository.findComments(dayId, request);
    }

    @Transactional
    public void deleteComment(
            Long commentId,
            CustomMemberDetails loginMember
    ) throws Exception {

        DayComment dayComment = findCommentById(commentId);
        validateDeletePermission(dayComment, loginMember);
        dayCommentRepository.delete(dayComment);
    }

    private void validateDeletePermission(
            DayComment dayComment,
            CustomMemberDetails loginMember
    ) throws IllegalAccessException {

        if(!dayComment.getMemberId().equals(loginMember.getMemberId())) {
            throw new IllegalAccessException("댓글 삭제 권한이 없습니다.");
        }
    }

    public DayComment findCommentById(Long commentId) {
        return dayCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("findCommentById, commentId = " + commentId));
    }

}
