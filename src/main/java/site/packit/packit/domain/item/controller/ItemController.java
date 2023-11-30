package site.packit.packit.domain.item.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.item.dto.CreateItemRequest;
import site.packit.packit.domain.item.dto.UpdateItemRequest;
import site.packit.packit.domain.item.dto.UpdateItemTitleRequest;
import site.packit.packit.domain.item.service.ItemService;
import site.packit.packit.global.response.success.SingleSuccessApiResponse;
import site.packit.packit.global.response.success.SuccessApiResponse;

import java.util.List;

@RequestMapping("/api")
@RestController
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * 새로운 체크리스트 아이템 생성
     */
    @PostMapping(value="/travels/{travelId}/check-lists/{checklistId}/items")
    public ResponseEntity<SingleSuccessApiResponse<Long>> createCheckListItem(
            @PathVariable Long travelId, @PathVariable Long checklistId,
            @RequestBody CreateItemRequest createItemRequest, @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long itemId = itemService.createCheckList(travelId, checklistId, createItemRequest, principal.getMemberId());

        return ResponseEntity.ok(
                SingleSuccessApiResponse.of(
                        "새로운 아이템이 생성되었습니다.", itemId
                )
        );
    }

    /**
     * 체크리스트 아이템 순서 수정
     */
    @PatchMapping(value = "travels/{travelId}/check-lists/{checklistId}/items/order")
    public ResponseEntity<SuccessApiResponse> updateCheckListItem(
            @PathVariable Long travelId, @PathVariable Long checklistId,
            @RequestBody List<UpdateItemRequest> updateItemRequests, @AuthenticationPrincipal CustomUserPrincipal principal
    ){
        itemService.updateItemOrder(travelId, checklistId, updateItemRequests, principal.getMemberId());

        return ResponseEntity.ok(
                SuccessApiResponse.of(
                        "아이템 순서 수정이 완료되었습니다."
                )
        );

    }

    /**
     * 체크리스트 아이템 삭제
     */
    @DeleteMapping(value = "travels/{travelId}/check-lists/{checkListId}/items/{itemId}")
    public ResponseEntity<SuccessApiResponse> deleteCheckListItem(
            @PathVariable Long travelId, @PathVariable Long checkListId,
            @PathVariable Long itemId, @AuthenticationPrincipal CustomUserPrincipal principal
    ){
        itemService.deleteItemAndReorder(travelId, checkListId, itemId, principal.getMemberId());

        return ResponseEntity.ok(
                SuccessApiResponse.of(
                        "아이템 삭제가 완료되었습니다."
                )
        );
    }

    /**
     * 체크리스트 아이템 체크/체크 취소
     */
    @PatchMapping(value = "travels/{travelId}/check-lists/{checkListId}/items/{itemId}")
    public ResponseEntity<SuccessApiResponse> checkItem(
            @PathVariable Long travelId, @PathVariable Long checkListId,
            @PathVariable Long itemId, @AuthenticationPrincipal CustomUserPrincipal principal
    ){
        itemService.checkItem(travelId, checkListId, itemId, principal.getMemberId());

        return ResponseEntity.ok(
                SuccessApiResponse.of(
                        "아이템 체크&체크 취소가 완료되었습니다."
                )
        );
    }

    /**
     * 체크리스트 아이템 항목 수정
     */
    @PatchMapping(value = "travels/{travelId}/check-lists/{checkListId}/items/{itemId}/title")
    public ResponseEntity<SuccessApiResponse> modifyItem(
            @PathVariable Long travelId, @PathVariable Long checkListId,
            @PathVariable Long itemId, @RequestBody UpdateItemTitleRequest updateItemTitleRequest,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ){
        itemService.updateItemTitle(travelId, checkListId, itemId, principal.getMemberId(), updateItemTitleRequest);

        return ResponseEntity.ok(
                SuccessApiResponse.of(
                        "아이템 항목 수정이 완료되었습니다."
                )
        );
    }


}
