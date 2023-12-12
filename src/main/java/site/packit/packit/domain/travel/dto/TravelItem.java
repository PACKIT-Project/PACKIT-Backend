package site.packit.packit.domain.travel.dto;

public record TravelItem(
        Long itemId,
        String title,
        int order,
        boolean isChecked
) {
}
