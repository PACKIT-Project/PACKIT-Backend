package site.packit.packit.domain.storage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.storage.dto.StorageListDto;
import site.packit.packit.domain.storage.service.StorageService;
import site.packit.packit.global.response.success.MultipleSuccessApiResponse;
import site.packit.packit.global.response.success.SuccessApiResponse;

import java.util.List;

@RequestMapping("/api/storage")
@RestController
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * 보관함 추가/취소 버튼
     */
    @PostMapping("/{travelId}")
    public ResponseEntity<SuccessApiResponse> updateStorage(
            @PathVariable Long travelId, @AuthenticationPrincipal CustomUserPrincipal principal
    ){
        storageService.toggleStorage(principal.getMemberId(), travelId);
        return ResponseEntity.ok(
                SuccessApiResponse.of(
                        "보관함 추가/취소에 성공했습니다."
                ));
    }

    /**
     * 보관함 전체 목록
     */
    @GetMapping("")
    public ResponseEntity<MultipleSuccessApiResponse<StorageListDto>> getStorageList(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ){
        List<StorageListDto> storageList = storageService.getStorageList(principal.getMemberId());

        return ResponseEntity.ok(
                MultipleSuccessApiResponse.of(
                        "보관함 전체 목록이 조회되었습니다.", storageList
                )
        );
    }

}
