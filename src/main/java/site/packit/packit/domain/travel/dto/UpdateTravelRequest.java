package site.packit.packit.domain.travel.dto;

import java.time.LocalDateTime;

public record UpdateTravelRequest(
        String title,

        LocalDateTime startDate,

        LocalDateTime endDate
) {
}
