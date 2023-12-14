package site.packit.packit.domain.category.controller;

import org.hibernate.sql.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.category.dto.CreateCategoryReq;
import site.packit.packit.domain.category.dto.UpdateCategoryReq;
import site.packit.packit.domain.category.service.CategoryService;
import site.packit.packit.global.response.success.SingleSuccessApiResponse;
import site.packit.packit.global.response.success.SuccessApiResponse;
import site.packit.packit.global.response.util.ResponseUtil;

import static org.springframework.http.HttpStatus.OK;

@RequestMapping("/api")
@RestController
public class CategoryController {
    private final CategoryService categoryService;


    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 할 일 생성
     */
    @PostMapping("/travels/categories")
    public ResponseEntity<SingleSuccessApiResponse<Long>> createNewCategory(
            @AuthenticationPrincipal CustomUserPrincipal principal, @RequestBody CreateCategoryReq createCategoryReq
    ) {
        return ResponseUtil.successApiResponse(OK, "새로운 할 일 생성에 성공했습니다.",
                categoryService.createNewCategory(principal.getMemberId(), createCategoryReq));
    }

    /**
     * 할 일 수정
     */
    @PatchMapping("/travels/categories")
    public ResponseEntity<SuccessApiResponse> updateCategory(
            @AuthenticationPrincipal CustomUserPrincipal principal, @RequestBody UpdateCategoryReq updateCategoryReq
    ) {
        categoryService.updateCategoryTitle(principal.getMemberId(), updateCategoryReq);
        return ResponseUtil.successApiResponse(OK, "할 일 제목 수정에 성공했습니다.");
    }

    /**
     * 할 일 삭제
     */
    @DeleteMapping("/travels/categories/{categoryId}")
    public ResponseEntity<SuccessApiResponse> deleteCategory(
            @AuthenticationPrincipal CustomUserPrincipal principal, @PathVariable Long categoryId
    ) {
        categoryService.deleteCategory(principal.getMemberId(), categoryId);
        return ResponseUtil.successApiResponse(OK, "할 일이 삭제되었습니다.");
    }

}
