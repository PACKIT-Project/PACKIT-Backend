package site.packit.packit.domain.checkList.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.packit.packit.domain.checkList.entity.CheckList;
import site.packit.packit.domain.travel.entity.Travel;

import java.util.List;

public interface CheckListRepository
        extends JpaRepository<CheckList, Long> {

    @Query("SELECT COALESCE(MAX(c.listOrder), 0) FROM CheckList c WHERE c.travel = :travel")
    Integer findMaxListOrderByTravel(Travel travel);

    List<CheckList> findByTravelId(Long travelId);

    List<CheckList> findByTravelAndListOrderGreaterThan(Travel travel, Integer listOrder);

    List<CheckList> findByTravelIdOrderByListOrderAsc(Long travelId);

}