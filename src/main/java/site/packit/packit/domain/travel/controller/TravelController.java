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
     * 동행자 추가 (초대코드 입력)
     */
    @PostMapping(value = "/invitations")
    public ResponseEntity<SingleSuccessApiResponse<Long>> invitationTravel(
            @AuthenticationPrincipal CustomUserPrincipal principal, @RequestParam String invitationCode
    ){
        return ResponseUtil.successApiResponse(OK, "여행 참여에 성공했습니다.",
                travelService.invitationTravel(principal.getMemberId(), invitationCode));
    }

    /**
     * 동행자 목록 조회
     */
    @GetMapping(value = "/members/{travelId}")
    public ResponseEntity<MultipleSuccessApiResponse<TravelMemberRes>> getTravelMemberList(
            @AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable Long travelId
    ){
        return ResponseUtil.successApiResponse(OK, "동행자 목록 조회에 성공했습니다.",
                travelService.getTravelMemberList(principal.getMemberId(), travelId));
    }

    /**
     * 예정된 여행 목록 조회
     */
    @GetMapping(value = "/upcoming")
    public ResponseEntity<MultipleSuccessApiResponse<TravelListRes>> getMyTravelUpcoming(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ){
        return ResponseUtil.successApiResponse(OK, "예정된 여행 목록 조회에 성공했습니다.",
                travelService.getMyTravel(principal.getMemberId(), true));
    }

    /**
     * 지난 여행 목록 조회
     */
    @GetMapping(value = "/past")
    public ResponseEntity<MultipleSuccessApiResponse<TravelListRes>> getMyTravelPast(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ){
        return ResponseUtil.successApiResponse(OK, "지난 여행 목록 조회에 성공했습니다.",
                travelService.getMyTravel(principal.getMemberId(), false));
    }


    /**
     * 여행 나의 리스트 상세 조회
     */
    @GetMapping(value = "/myList/{travelId}")
    public ResponseEntity<SingleSuccessApiResponse<TravelDetailRes>> getMyToDoList(
            @AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable Long travelId
    ){
        return ResponseUtil.successApiResponse(OK, "나의 체크리스트 조회에 성공했습니다.",
                travelService.getMyToDoList(principal.getMemberId(), travelId));
    }

    /**
     * 여행 동행자 리스트 상세 조회
     */
    @GetMapping(value = "/list/{travelId}/{memberId}")
    public ResponseEntity<SingleSuccessApiResponse<TravelDetailRes>> getMyToDoList(
            @AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable Long travelId, @PathVariable Long memberId
    ){
        return ResponseUtil.successApiResponse(OK, "동행자 체크리스트 조회에 성공했습니다.",
                travelService.getToDoList(principal.getMemberId(), memberId, travelId));
    }

    /**
     * 여행 삭제
     */
    @DeleteMapping("/{travelId}")
    public ResponseEntity<SuccessApiResponse> deleteTravel(
            @AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable Long travelId
    ) {
        travelService.deleteTravel(principal.getMemberId(), travelId);
        return ResponseUtil.successApiResponse(OK, "여행이 삭제되었습니다.");
    }

    /**
     * 여행 정보 조회
     */
    @GetMapping(value = "/{travelId}")
    public ResponseEntity<SingleSuccessApiResponse<TravelListRes>> getTravelInfo(
            @AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable Long travelId
    ){
        return ResponseUtil.successApiResponse(OK, "여행 정보 조회에 성공했습니다.",
                travelService.getTravelInfo(principal.getMemberId(), travelId));
    }


}
