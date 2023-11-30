package site.packit.packit.domain.checkList.dto;

public record UpdateCheckListRequest(
        Long id,
        Integer order
) {
}
