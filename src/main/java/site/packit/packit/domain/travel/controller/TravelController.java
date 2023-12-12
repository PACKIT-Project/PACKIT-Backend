package site.packit.packit.domain.travel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
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
}
