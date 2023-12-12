package site.packit.packit.domain.travel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.destination.dto.DestinationDto;
import site.packit.packit.domain.travel.dto.*;
import site.packit.packit.domain.travel.service.TravelService;
import site.packit.packit.global.response.success.MultipleSuccessApiResponse;
import site.packit.packit.global.response.success.SingleSuccessApiResponse;
import site.packit.packit.global.response.success.SuccessApiResponse;
import site.packit.packit.global.response.util.ResponseUtil;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;


@RequestMapping("/api/travels")
@RestController
public class TravelController {

    private final TravelService travelService;
    public TravelController(TravelService travelService) {
        this.travelService = travelService;
    }

    /**
     * 새로운 여행 생성
     */
    @PostMapping(value = "/new")
    public ResponseEntity<SingleSuccessApiResponse<Long>> createNewTravel(
            @AuthenticationPrincipal CustomUserPrincipal principal, @RequestBody CreateTravelReq createTravelReq
    ) {
        return ResponseUtil.successApiResponse(OK, "새로운 여행 생성에 성공했습니다.",
                travelService.createNewTravel(principal.getMemberId(), createTravelReq));
    }

    /**
     * 현재 동행자 수 & 초대코드 확인
     */
    @GetMapping(value = "/invitations/{travelId}")
    public ResponseEntity<SingleSuccessApiResponse<TravelInviteRes>> getInvitationCode(
            @AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable Long travelId
    ) {
        return ResponseUtil.successApiResponse(OK, "현재 동행자 수, 초대코드 확인에 성공했습니다.",
                travelService.getInvitationCode(principal.getMemberId(), travelId));
    }

    /**
     * 동행자 추가 (초대코드 입력) API
     */
    @PostMapping(value = "/invitations")
    public ResponseEntity<SingleSuccessApiResponse<Long>> invitationTravel(
            @AuthenticationPrincipal CustomUserPrincipal principal, @RequestParam String invitationCode
    ){
        return ResponseUtil.successApiResponse(OK, "여행 참여에 성공했습니다.",
                travelService.invitationTravel(principal.getMemberId(), invitationCode));
    }

    /**
     * 동행자 목록 조회 API
     */
    @GetMapping(value = "/members/{travelId}")
    public ResponseEntity<MultipleSuccessApiResponse<TravelMemberRes>> getTravelMemberList(
            @AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable Long travelId
    ) {
        return ResponseUtil.successApiResponse(OK, "동행자 목록 조회에 성공했습니다.",
                travelService.getTravelMemberList(principal.getMemberId(), travelId));
    }


}
