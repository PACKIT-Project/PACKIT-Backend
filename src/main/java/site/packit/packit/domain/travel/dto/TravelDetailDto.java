package site.packit.packit.domain.travel.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TravelDetailDto(
        Long travelId,

        String title,

        String dDay,

        String destination,

        LocalDateTime startDate,

        LocalDateTime endDate,

        List<CheckListDto> checkListDtoList,

        Boolean isInStorage

) {


}
