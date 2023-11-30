package site.packit.packit.domain.storage.dto;

import java.time.LocalDateTime;

public record StorageListDto(
        Long travelId,
        String Title,

        LocalDateTime startDate,

        LocalDateTime endDate
) {
}
