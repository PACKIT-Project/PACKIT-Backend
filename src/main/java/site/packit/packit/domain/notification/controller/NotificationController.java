package site.packit.packit.domain.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.member.service.MemberService;
import site.packit.packit.domain.notification.dto.NotificationHistoryDto;
import site.packit.packit.domain.notification.dto.SaveFcmTokenRequest;
import site.packit.packit.domain.notification.service.PushNotificationService;
import site.packit.packit.global.response.success.MultipleSuccessApiResponse;
import site.packit.packit.global.response.success.SuccessApiResponse;
import site.packit.packit.global.response.util.ResponseUtil;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static site.packit.packit.domain.notification.constant.NotificationType.ACTIVE;
import static site.packit.packit.domain.notification.constant.NotificationType.TRAVEL_REMIND;

@RestController
public class NotificationController {

    private final MemberService memberService;
    private final PushNotificationService pushNotificationService;

    public NotificationController(MemberService memberService, PushNotificationService pushNotificationService) {
        this.memberService = memberService;
        this.pushNotificationService = pushNotificationService;
    }

    @PostMapping("/api/active-notifications/subscriber")
    public ResponseEntity<SuccessApiResponse> addActivePushNotificationSubscriber(@AuthenticationPrincipal CustomUserPrincipal principal) {
        Member member = memberService.getMember(principal.getName());
        pushNotificationService.createSubscriber(ACTIVE, member);

        return ResponseUtil.successApiResponse(OK, "성공적으로 활동 알림 수신이 설정되었습니다.");
    }

    @DeleteMapping("/api/active-notifications/subscriber")
    public ResponseEntity<SuccessApiResponse> deleteActivePushNotificationSubscriber(@AuthenticationPrincipal CustomUserPrincipal principal) {
        Member member = memberService.getMember(principal.getName());
        pushNotificationService.deleteSubscriber(ACTIVE, member);

        return ResponseUtil.successApiResponse(OK, "성공적으로 활동 알림 수신이 해제되었습니다.");
    }

    @PostMapping("/api/travel-remind-notifications/subscriber")
    public ResponseEntity<SuccessApiResponse> addTravelRemindPushNotificationSubscriber(@AuthenticationPrincipal CustomUserPrincipal principal) {
        Member member = memberService.getMember(principal.getName());
        pushNotificationService.createSubscriber(TRAVEL_REMIND, member);

        return ResponseUtil.successApiResponse(OK, "성공적으로 여행 리마이드 알림 수신이 설정되었습니다.");
    }

    @DeleteMapping("/api/travel-remind-notifications/subscriber")
    public ResponseEntity<SuccessApiResponse> deleteTravelRemindNotificationPushNotificationSubscriber(@AuthenticationPrincipal CustomUserPrincipal principal) {
        Member member = memberService.getMember(principal.getName());
        pushNotificationService.deleteSubscriber(TRAVEL_REMIND, member);

        return ResponseUtil.successApiResponse(OK, "성공적으로 여행 리마이드 알림 수신이 해제되었습니다.");
    }

    @PostMapping("/api/fcm-token")
    public ResponseEntity<SuccessApiResponse> saveFcmToken(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody SaveFcmTokenRequest request
    ) {
        Member member = memberService.getMember(principal.getName());
        pushNotificationService.saveFcmToken(member, request.token());

        return ResponseUtil.successApiResponse(OK, "성공적으로 FCM 토큰이 저장되었습니다.");
    }

    @GetMapping("/api/notifications/histories")
    public ResponseEntity<MultipleSuccessApiResponse<NotificationHistoryDto>> getNotificationHistories(@AuthenticationPrincipal CustomUserPrincipal principal) {
        Member member = memberService.getMember(principal.getName());
        List<NotificationHistoryDto> notificationHistories = pushNotificationService.getNotificationHistories(member);

        return ResponseUtil.successApiResponse(OK, "성공적으로 알림 기록이 조회되었습니다.", notificationHistories);
    }
}
