package site.packit.packit.domain.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.travel.entity.Travel;
import site.packit.packit.global.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static site.packit.packit.domain.travel.exception.TravelErrorCode.TRAVEL_NOT_FOUND;

public interface TravelRepository
        extends JpaRepository<Travel, Long> {

    boolean existsByInvitationCode(String invitationCode);

    default Travel findByIdOrThrow(Long travelId) {
        return findById(travelId)
                .orElseThrow(() -> new ResourceNotFoundException(TRAVEL_NOT_FOUND));
    }

    Optional<Travel> findByInvitationCode(String invitationCode);

    List<Travel> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
