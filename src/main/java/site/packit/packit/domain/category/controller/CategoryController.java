package site.packit.packit.domain.category.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.category.dto.CreateCategoryReq;
import site.packit.packit.domain.category.service.CategoryService;
import site.packit.packit.domain.cluster.dto.CreateClusterReq;
import site.packit.packit.global.response.success.SingleSuccessApiResponse;
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
    @PostMapping("/travels/clusters/categories")
    public ResponseEntity<SingleSuccessApiResponse<Long>> createNewCluster(
            @AuthenticationPrincipal CustomUserPrincipal principal, @RequestBody CreateCategoryReq createCategoryReq
    ) {
        return ResponseUtil.successApiResponse(OK, "새로운 할 일 생성에 성공했습니다.",
                categoryService.createNewCategory(principal.getMemberId(), createCategoryReq));
    }

}
