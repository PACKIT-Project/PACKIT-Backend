package site.packit.packit.domain.travel.dto;

import java.util.List;

public record TravelCluster(
        Long clusterId,
        String title,
        int order,
        int allItemNum,
        List<TravelCategory> travelCategoryList

) {
    public static TravelCluster createWithItemCounts(
            Long clusterId,
            String title,
            int order,
            List<TravelCategory> travelCategoryList
    ) {
        int allItemNum = travelCategoryList.stream()
                .mapToInt(TravelCategory::allItemNum)
                .sum();

        return new TravelCluster(clusterId, title, order, allItemNum, travelCategoryList);
    }
}
