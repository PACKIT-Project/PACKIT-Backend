package site.packit.packit.domain.item.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.category.dto.CreateCategoryReq;
import site.packit.packit.domain.item.dto.CreateItemReq;
import site.packit.packit.domain.item.service.ItemService;
import site.packit.packit.global.response.success.SingleSuccessApiResponse;
import site.packit.packit.global.response.success.SuccessApiResponse;
import site.packit.packit.global.response.util.ResponseUtil;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RequestMapping("/api")
@RestController
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }


    /**
     * 할 일 아이템 생성
     */
    @PostMapping("/travels/items")
    public ResponseEntity<SingleSuccessApiResponse<Long>> createNewItem(
            @AuthenticationPrincipal CustomUserPrincipal principal, @RequestBody CreateItemReq createItemReq
    ){
        return ResponseUtil.successApiResponse(OK, "새로운 할 일 아이템 생성에 성공했습니다.",
                itemService.createNewItem(principal.getMemberId(), createItemReq));
    }


    /**
     * 할 일 아이템 삭제
     */
    @DeleteMapping("/travels/items/{itemId}")
    public ResponseEntity<SuccessApiResponse> deleteItem(
            @AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable Long itemId
    ){
        itemService.deleteItem(principal.getMemberId(), itemId);
        return ResponseUtil.successApiResponse(OK, "아이템 삭제에 성공했습니다.");
    }



}
