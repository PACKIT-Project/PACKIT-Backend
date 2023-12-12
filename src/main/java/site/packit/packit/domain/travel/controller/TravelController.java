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

}
