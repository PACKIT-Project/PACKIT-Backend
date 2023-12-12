package site.packit.packit.domain.destination.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.destination.dto.DestinationDto;
import site.packit.packit.domain.destination.service.DestinationService;
import site.packit.packit.global.response.success.MultipleSuccessApiResponse;
import site.packit.packit.global.response.util.ResponseUtil;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RequestMapping("/api/destination")
@RestController
public class DestinationController {
    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    /**
     * 여행지 검색
     */
    @GetMapping(value = "/search")
    public ResponseEntity<MultipleSuccessApiResponse<DestinationDto>> searchDestination(
            @AuthenticationPrincipal CustomUserPrincipal principal, @RequestParam String keyword
    ) {
        return ResponseUtil.successApiResponse(OK, "여행지 검색에 성공했습니다.",
                destinationService.searchDestination(principal.getMemberId(), keyword));
    }

}
