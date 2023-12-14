package site.packit.packit.domain.category.dto;

public record UpdateCategoryReq(
        Long categoryId,
        String title
) {
}
