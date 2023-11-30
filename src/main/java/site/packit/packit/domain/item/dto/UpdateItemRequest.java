package site.packit.packit.domain.item.dto;

public record UpdateItemRequest(
        Long id,
        Integer order
) {
}
