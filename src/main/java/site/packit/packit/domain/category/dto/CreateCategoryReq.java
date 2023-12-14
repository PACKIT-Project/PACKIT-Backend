package site.packit.packit.domain.category.dto;

public record CreateCategoryReq(
        Long clusterId,
        String title

) {
}
