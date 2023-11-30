package site.packit.packit.domain.travel.dto;

public record ItemDto(

        Long itemId,

        String title,

        Integer order,

        Boolean isChecked
) {
}
