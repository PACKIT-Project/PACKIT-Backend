package site.packit.packit.domain.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.member.entity.Member;
import site.packit.packit.domain.member.service.MemberService;
import site.packit.packit.domain.notification.service.PushNotificationService;
import site.packit.packit.global.response.success.SuccessApiResponse;
import site.packit.packit.global.response.util.ResponseUtil;

import static org.springframework.http.HttpStatus.OK;

@RequestMapping("/api/notification/subscriber")
@RestController
public class NotificationController {

    private final MemberService memberService;
    private final PushNotificationService pushNotificationService;

    public NotificationController(MemberService memberService, PushNotificationService pushNotificationService) {
        this.memberService = memberService;
        this.pushNotificationService = pushNotificationService;
    }

    @PostMapping
    public ResponseEntity<SuccessApiResponse> addPushNotificationSubscriber(@AuthenticationPrincipal CustomUserPrincipal principal) {
        Member member = memberService.getMember(principal.getName());
        pushNotificationService.createSubscriber(member);

        return ResponseUtil.successApiResponse(OK, "성공적으로 알림 수신이 설정되었습니다.");
    }

    @DeleteMapping
    public ResponseEntity<SuccessApiResponse> deletePushNotificationSubscriber(@AuthenticationPrincipal CustomUserPrincipal principal) {
        Member member = memberService.getMember(principal.getName());
        pushNotificationService.deleteSubscriber(member);

        return ResponseUtil.successApiResponse(OK, "성공적으로 알림 수신이 해제되었습니다.");
    }
}
