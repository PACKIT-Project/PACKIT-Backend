package site.packit.packit.domain.cluster.dto;

public record ClusterOrderReq(
        Long clusterId,
        int newOrder
) {
}
