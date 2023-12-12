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
import site.packit.packit.domain.travel.dto.TravelListDto;
import site.packit.packit.global.response.success.MultipleSuccessApiResponse;

import java.util.List;

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
        return ResponseEntity.ok(MultipleSuccessApiResponse.of(
                "여행지 검색에 성공했습니다.", destinationService.searchDestination(principal.getMemberId(), keyword)
        ));
    }

}
