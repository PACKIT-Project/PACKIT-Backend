package site.packit.packit.domain.travel.dto;

import site.packit.packit.domain.travel.constant.DestinationType;

import java.time.LocalDateTime;

public record TravelListDto(

        Long travelId,

        String title,

        String dDay,

        DestinationType destinationType,

        LocalDateTime startDate,

        LocalDateTime endDate,
        Boolean isInStorage,

        Integer unfinished,

        Integer finished


) {
}
