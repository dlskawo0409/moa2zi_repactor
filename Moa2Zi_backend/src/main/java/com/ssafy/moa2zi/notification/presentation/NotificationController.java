package com.ssafy.moa2zi.notification.presentation;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.ssafy.moa2zi.auth.application.RedisRefreshTokenService;
import com.ssafy.moa2zi.common.util.FirebaseMessagingSnippets;
import com.ssafy.moa2zi.member.application.MemberService;
import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import com.ssafy.moa2zi.notification.application.NotificationService;
import com.ssafy.moa2zi.notification.domain.Notification;
import com.ssafy.moa2zi.notification.domain.NotificationType;
import com.ssafy.moa2zi.notification.dto.request.FCMTokenRequest;
import com.ssafy.moa2zi.notification.dto.request.LocationPostRequest;
import com.ssafy.moa2zi.notification.dto.response.NotificationResponse;
import com.ssafy.moa2zi.transaction.application.TransactionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final RedisRefreshTokenService redisRefreshTokenService;
    private final MemberService memberService;
    private final TransactionService transactionService;

    @Value("${spring.jwt.access-token-name}") String accessTokenName;
    @Value("${spring.jwt.refresh-token-name}") String refreshTokenName;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {

        SseEmitter sseEmitter = notificationService.subscribe(loginMember, lastEventId);
        return ResponseEntity.ok(sseEmitter);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {

        List<NotificationResponse> result = notificationService.getUnreadNotifications(loginMember);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) {

        List<NotificationResponse> result = notificationService.getAllNotifications(loginMember);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable(name = "notificationId") Long notificationId
    ) {

        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    /**
     * 안드로이드 위치 전송
     */
    @PostMapping("/android")
    public ResponseEntity<NotificationResponse> postLocation(
            HttpServletRequest request,
            HttpServletResponse response,
            @Valid @RequestBody LocationPostRequest locationPostRequest
    ) throws Exception {

        // 알림 수신자
        Member member = getMemberByCookies(request);
        log.info("memberId: "+String.valueOf(member.getMemberId()));

        response = addNewCookie(response,member);

        if(locationPostRequest.latitude().equals(37F)){
            return ResponseEntity.noContent().build();
        }

        float latitude = locationPostRequest.latitude();
        float longitude = locationPostRequest.longitude();
        List<NotificationResponse> result = transactionService.checkAlertByLocation(latitude, longitude, member);
        if(result.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return  ResponseEntity.ok(result.get(0));
    }

    @PutMapping("/firebase/token")
    public ResponseEntity<?> updateFCMToken(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestBody FCMTokenRequest fcmTokenRequest
    ) throws BadRequestException {

        Member member = getMemberByCookies(request);

        memberService.updateFCMToken(fcmTokenRequest.token(), member);

        response = addNewCookie(response ,member);

        return ResponseEntity.ok(null);
    }

    @PostMapping("/firebase/test")
    public ResponseEntity<Void> sendFCMMessage(
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws FirebaseMessagingException {

//        String fcmToken = memberService.findMember(loginMember.getMemberId()).getFcmToken();
        return ResponseEntity.ok().build();
    }


    Member getMemberByCookies(
            HttpServletRequest request
    ) throws BadRequestException {
        String refresh = null;
        Cookie[] cookies = request.getCookies();

        if(cookies == null || cookies.length == 0){
            throw new BadRequestException("refresh token null");
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(refreshTokenName)) {
                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            //response status code
            throw new BadRequestException("refresh token null");
        }

        return redisRefreshTokenService.getMemberByRefreshToken(refresh);

    }

    HttpServletResponse addNewCookie(HttpServletResponse response, Member member){

        String newRefreshToken = redisRefreshTokenService.generateRefreshToken(member.getMemberId());

        ResponseCookie cookie = ResponseCookie.from(refreshTokenName, newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None") // 중요!
                .maxAge(Duration.ofHours(4))
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return response;
    }

}
