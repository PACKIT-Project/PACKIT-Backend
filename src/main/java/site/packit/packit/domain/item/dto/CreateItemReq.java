package site.packit.packit.domain.item.dto;

public record CreateItemReq(
        Long categoryId,
        String title

) {
}
