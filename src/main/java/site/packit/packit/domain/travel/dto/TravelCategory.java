package site.packit.packit.domain.travel.dto;

import java.util.List;

public record TravelCategory(
        Long categoryId,
        String title,
        int order,
        List<TravelItem> travelItemList
) {
}
