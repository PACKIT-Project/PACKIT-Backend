package site.packit.packit.domain.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.destination.entity.Destination;
import site.packit.packit.domain.travel.entity.Travel;
import site.packit.packit.global.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import static site.packit.packit.domain.travel.exception.TravelErrorCode.DESTINATION_NOT_FOUND;

public interface TravelRepository
        extends JpaRepository<Travel, Long> {

    List<Travel> findByStartDateAfterAndMemberIdOrderByStartDateAsc(LocalDateTime startDate, Long memberId);

    List<Travel> findByEndDateBeforeAndMemberIdOrderByEndDateDesc(LocalDateTime endDate, Long memberId);

    List<Travel> findByStartDateBeforeAndMemberIdOrderByStartDateDesc(
            LocalDateTime startDate, Long memberId
    );
  
    int countAllByMember_Id(Long memberId);
}
