package site.packit.packit.domain.travel.dto;

public record TravelListRes(
        Long id,
        String title,
        String destination,
        String startDate,
        String endDate,
        String dDay,
        Long memberNum
) {
}
