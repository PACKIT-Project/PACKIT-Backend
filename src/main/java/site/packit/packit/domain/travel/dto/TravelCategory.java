package site.packit.packit.domain.travel.dto;

import java.util.List;

public record TravelCategory(
        Long categoryId,
        String title,
        int order,
        int allItemNum,
        int checkedItemNum,
        List<TravelItem> travelItemList
) {
    public static TravelCategory createWithItemCounts(
            Long categoryId,
            String title,
            int order,
            List<TravelItem> travelItemList
    ) {
        int allItemNum = travelItemList.size();
        int checkedItemNum = (int) travelItemList.stream().filter(TravelItem::isChecked).count();

        return new TravelCategory(categoryId, title, order, allItemNum, checkedItemNum, travelItemList);
    }
}
