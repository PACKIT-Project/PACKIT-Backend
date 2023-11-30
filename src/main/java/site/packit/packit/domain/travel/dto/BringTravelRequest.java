package site.packit.packit.domain.travel.dto;


import java.time.LocalDateTime;
public record BringTravelRequest(
        String title,

        LocalDateTime startDate,

        LocalDateTime endDate
) {
}