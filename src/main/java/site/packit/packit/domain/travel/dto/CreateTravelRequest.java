package site.packit.packit.domain.travel.dto;

import java.time.LocalDateTime;

public record CreateTravelRequest(

        String title,

        Long destinationId,

        LocalDateTime startDate,

        LocalDateTime endDate

) {
}
