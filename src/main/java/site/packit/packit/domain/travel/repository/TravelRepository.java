package site.packit.packit.domain.travel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.packit.packit.domain.travel.entity.Travel;

import java.time.LocalDateTime;
import java.util.List;

public interface TravelRepository
        extends JpaRepository<Travel, Long> {

    List<Travel> findByStartDateAfterAndMemberIdOrderByStartDateAsc(LocalDateTime startDate, Long memberId);

    List<Travel> findByEndDateBeforeAndMemberIdOrderByEndDateDesc(LocalDateTime endDate, Long memberId);

//    List<Travel> findAllByStartDateAndMember_IsEmailAuthorized(LocalDateTime startDate, Boolean isEmailAuthorized);

    List<Travel> findByStartDateBeforeAndMemberIdOrderByStartDateDesc(
            LocalDateTime startDate, Long memberId
    );
  
    int countAllByMember_Id(Long memberId);
}
