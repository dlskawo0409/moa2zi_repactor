package com.ssafy.moa2zi.common.util;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.domain.MemberRepository;
import com.ssafy.moa2zi.notification.domain.Notification;
import com.ssafy.moa2zi.notification.domain.NotificationMessage;
import com.ssafy.moa2zi.notification.domain.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.webjars.NotFoundException;

@Slf4j
@Component
@RequiredArgsConstructor
public class FirebaseMessagingSnippets {

	private final MemberRepository memberRepository;

	public String sendToToken(Notification notification) throws Exception {

		Member sender = findMemberById(notification.getSenderId());
		Member receiver = findMemberById(notification.getReceiverId());
		String fcmToken = receiver.getFcmToken();

		if(fcmToken == null) {
			throw new IllegalAccessException("fckToken 이 존재하지 않습니다. memberId : " + notification.getReceiverId());
		}

		Message alarmMessage = Message.builder()
			.putData("message", notification.getMessage())
			.setToken(fcmToken)
			.build();

		// Send a message to the device corresponding to the provided
		// registration token.
		String response = FirebaseMessaging.getInstance().send(alarmMessage);
		// Response is a message ID string.
		log.info("firebase Successfully sent message: " + response);
		// [END send_to_token]
		return response;
	}

	private Member findMemberById(Long memberId) {
		if(memberId == null) {
			return null;
		}

		return memberRepository.findById(memberId)
				.orElseThrow(() -> new NotFoundException(memberId + " 의 유저를 찾을 수 없습니다."));
	}

}
