package site.packit.packit.domain.travel.dto;

import java.util.List;

public record TravelDetailRes(
        Long travelId,
        String title,
        int dDay,
        String destination,
        String startDate,
        String endDate,
        Long memberNum,
        List<TravelCluster> travelClusterList


) {
}
