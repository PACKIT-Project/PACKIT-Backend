package site.packit.packit.domain.travel.dto;

import site.packit.packit.domain.travel.constant.DestinationType;

import java.time.LocalDateTime;
import java.util.List;

public record TravelDetailDto(
        Long travelId,

        String title,

        String dDay,

        DestinationType destinationType,

        LocalDateTime startDate,

        LocalDateTime endDate,

        List<CheckListDto> checkListDtoList,

        Boolean isInStorage

) {


}
