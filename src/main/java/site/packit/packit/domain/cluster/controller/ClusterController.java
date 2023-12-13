package site.packit.packit.domain.cluster.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.packit.packit.domain.auth.principal.CustomUserPrincipal;
import site.packit.packit.domain.cluster.dto.ClusterOrderReq;
import site.packit.packit.domain.cluster.dto.CreateClusterReq;
import site.packit.packit.domain.cluster.service.ClusterService;
import site.packit.packit.domain.member.dto.request.UpdateMemberProfileRequest;
import site.packit.packit.domain.travel.dto.CreateTravelReq;
import site.packit.packit.global.response.success.SingleSuccessApiResponse;
import site.packit.packit.global.response.success.SuccessApiResponse;
import site.packit.packit.global.response.util.ResponseUtil;

import static org.springframework.http.HttpStatus.OK;

@RequestMapping("/api")
@RestController
public class ClusterController {
    private final ClusterService clusterService;


    public ClusterController(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    /**
     * 할 일 그룹 생성
     */
    @PostMapping("/travels/clusters")
    public ResponseEntity<SingleSuccessApiResponse<Long>> createNewCluster(
            @AuthenticationPrincipal CustomUserPrincipal principal, @RequestBody CreateClusterReq createClusterReq
    ) {
        return ResponseUtil.successApiResponse(OK, "새로운 할 일 그룹 생성에 성공했습니다.",
                clusterService.createNewCluster(principal.getMemberId(), createClusterReq));
    }

    /**
     * 할 일 그룹 순서 변경
     */
    @PatchMapping("/travels/clusters/reorder")
    public ResponseEntity<SuccessApiResponse> updateClusterOrder(
            @AuthenticationPrincipal CustomUserPrincipal principal, @RequestBody ClusterOrderReq clusterOrderReq
    ) {
        clusterService.updateClusterOrder(principal.getMemberId(), clusterOrderReq);
        return ResponseUtil.successApiResponse(OK, "할 일 그룹 순서가 변경되었습니다.");
    }
}
