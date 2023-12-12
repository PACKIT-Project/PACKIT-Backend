package site.packit.packit.domain.travel.dto;

import java.time.LocalDateTime;

public record CreateTravelReq(

        String title,

        Long destinationId,

        LocalDateTime startDate,

        LocalDateTime endDate

) {
}
