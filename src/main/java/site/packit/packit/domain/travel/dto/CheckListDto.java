package site.packit.packit.domain.travel.dto;

import java.util.List;

public record CheckListDto(

        Long checkListId,
        String title,

        Integer order,

        Boolean essential,

        List<ItemDto> itemDtoList
) {
}
