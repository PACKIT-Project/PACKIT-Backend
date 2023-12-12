package site.packit.packit.domain.travel.dto;

import java.util.List;

public record TravelCluster(
        Long clusterId,
        String title,
        int order,

        List<TravelCategory> travelCategoryList

) {
}
