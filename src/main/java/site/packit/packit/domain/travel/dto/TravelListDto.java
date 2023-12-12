package site.packit.packit.domain.travel.dto;

import java.time.LocalDateTime;

public record TravelListDto(

        Long travelId,

        String title,

        String dDay,

        String destination,

        LocalDateTime startDate,

        LocalDateTime endDate,
        Boolean isInStorage,

        Integer unfinished,

        Integer finished


) {
}
