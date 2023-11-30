package site.packit.packit.domain.checkList.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.checkList.dto.CreateCheckListRequest;
import site.packit.packit.domain.checkList.dto.UpdateCheckListRequest;
import site.packit.packit.domain.checkList.dto.UpdateListTitleRequest;
import site.packit.packit.domain.checkList.service.CheckListService;
import site.packit.packit.global.response.success.SingleSuccessApiResponse;
import site.packit.packit.global.response.success.SuccessApiResponse;

import java.util.List;

@RequestMapping("/api")
@RestController
public class CheckListController {

    private final CheckListService checkListService;

    public CheckListController(CheckListService checkListService) {
        this.checkListService = checkListService;
    }


    /**
     * 새로운 체크리스트 생성
     */
    @PostMapping(value="travels/{travelId}/check-lists")
    public ResponseEntity<SingleSuccessApiResponse<Long>> createCheckList(
            @PathVariable Long travelId, @RequestBody CreateCheckListRequest createCheckListRequest, @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long checkListId = checkListService.createCheckList(travelId, createCheckListRequest, principal.getMemberId());

        return ResponseEntity.ok(
                SingleSuccessApiResponse.of(
                        "새로운 체크리스트가 생성되었습니다.", checkListId
                )
        );
    }

    /**
     * 체크리스트 순서 수정
     */
    @PatchMapping(value = "travels/{travelId}/check-lists/order")
    public ResponseEntity<SuccessApiResponse> updateCheckList(
            @PathVariable Long travelId,@RequestBody List<UpdateCheckListRequest> updateCheckListRequests, @AuthenticationPrincipal CustomUserPrincipal principal
    ){

        checkListService.updateCheckListOrder(travelId, updateCheckListRequests, principal.getMemberId());

        return ResponseEntity.ok(
                SuccessApiResponse.of(
                        "체크리스트 순서 수정이 완료되었습니다."
                )
        );

    }

    /**
     * 체크리스트 삭제
     */
    @DeleteMapping(value = "travels/{travelId}/check-lists/{checkListId}")
    public ResponseEntity<SuccessApiResponse> deleteCheckList(
            @PathVariable Long travelId, @PathVariable Long checkListId, @AuthenticationPrincipal CustomUserPrincipal principal
    ){
        checkListService.deleteCheckListAndReorder(travelId, checkListId, principal.getMemberId());

        return ResponseEntity.ok(
                SuccessApiResponse.of(
                        "체크리스트 삭제가 완료되었습니다."
                )
        );
    }

    /**
     * 체크리스트 항목 수정
     */
    @PatchMapping(value = "travels/{travelId}/check-lists/{checkListId}")
    public ResponseEntity<SuccessApiResponse> modifyCheckList(
            @PathVariable Long travelId, @PathVariable Long checkListId,
            @RequestBody UpdateListTitleRequest updateListTitleRequest, @AuthenticationPrincipal CustomUserPrincipal principal
    ){

        checkListService.updateCheckListTitle(travelId, checkListId, updateListTitleRequest, principal.getMemberId());

        return ResponseEntity.ok(
                SuccessApiResponse.of(
                        "체크리스트 항목 수정이 완료되었습니다."
                )
        );

    }


}
